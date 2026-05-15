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

@Component
@Order(1)
public class ProxyForwardingFilter extends OncePerRequestFilter {

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    private final WebClient webClient;

    public ProxyForwardingFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

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