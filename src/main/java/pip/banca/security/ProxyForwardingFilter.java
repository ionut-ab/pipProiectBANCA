package pip.banca.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

/**
 * Servlet filter that can forward authenticated requests to the backend service.
 */
@Component
@Order(1)
public class ProxyForwardingFilter extends OncePerRequestFilter {

    /**
     * Base URL of the backend service that receives forwarded requests.
     */
    @Value("${backend.base-url}")
    private String backendBaseUrl;

    /**
     * WebClient used to forward HTTP requests.
     */
    private final WebClient webClient;

    /**
     * Creates the filter with a WebClient from the supplied builder.
     *
     * @param webClientBuilder builder configured by the Spring context
     */
    public ProxyForwardingFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Disables this filter because request forwarding is handled by {@link pip.banca.controllers.ProxyController}.
     *
     * @param request incoming servlet request
     * @return true to skip this filter for every request
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Now that ProxyController handles forwarding, we disable this filter.
        return true;
    }

    /**
     * Forwards authenticated requests or rejects unauthenticated requests if the filter is enabled.
     *
     * @param request incoming servlet request
     * @param response servlet response to write to
     * @param filterChain downstream filter chain for local user routes
     * @throws ServletException when downstream filters fail
     * @throws IOException when request or response IO fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/users/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Auth check is already done by Spring Security before this runs.
        // Here you can enrich the request before forwarding.

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String userId = jwtAuth.getToken().getSubject();
            String email  = jwtAuth.getToken().getClaimAsString("email");

            // Forward the request to the backend with extra headers
            forwardRequest(request, response, userId, email);
        } else {
            // Not authenticated — Spring Security would have blocked this already,
            // but just in case:
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    /**
     * Sends the current request to the backend with authenticated user headers.
     *
     * @param request incoming servlet request
     * @param response servlet response to write backend data into
     * @param userId authenticated user identifier
     * @param email authenticated user email
     * @throws IOException when request or response IO fails
     */
    private void forwardRequest(HttpServletRequest request,
                                HttpServletResponse response,
                                String userId,
                                String email) throws IOException {

        String targetUrl = backendBaseUrl + request.getRequestURI();
        String method    = request.getMethod();
        String body      = new String(request.getInputStream().readAllBytes());

        // Build and send the forwarded request
        WebClient.ResponseSpec responseSpec = webClient
                .method(HttpMethod.valueOf(method))
                .uri(targetUrl)
                .header("X-User-Id", userId)        // Enriched headers
                .header("X-User-Email", email)
                .header("Authorization", request.getHeader("Authorization")) // Forward original token
                .bodyValue(body.isEmpty() ? "" : body)
                .retrieve();

        byte[] backendResponse = responseSpec
                .bodyToMono(byte[].class)
                .block();

        response.setContentType("application/json");
        response.getOutputStream().write(backendResponse != null ? backendResponse : new byte[0]);
    }
}
