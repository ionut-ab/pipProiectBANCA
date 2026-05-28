package pip.banca.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;


/**
 * Controller that forwards authenticated API requests to the configured backend.
 */
@RequestMapping("/")
@RestController
public class ProxyController {

    /**
     * Base URL of the backend service that receives forwarded requests.
     */
    @Value("${backend.base-url}")
    private String backendBaseUrl;

    /**
     * WebClient used to relay incoming HTTP requests.
     */
    private final WebClient webClient;

    /**
     * Builds a proxy controller with a WebClient created from the provided builder.
     *
     * @param webClientBuilder builder configured by the Spring context
     */
    public ProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Forwards administrator API requests to the backend.
     *
     * @param request original servlet request
     * @return backend response body and status
     * @throws IOException when the request body cannot be read
     */
    @PreAuthorize("hasRole('admin')")
    @RequestMapping("/api/admin/**")
    public Mono<ResponseEntity<byte[]>> adminRoutes(HttpServletRequest request) throws IOException {
        return forwardRequest(request);
    }

    /**
     * Forwards regular user API requests to the backend.
     *
     * @param request original servlet request
     * @return backend response body and status
     * @throws IOException when the request body cannot be read
     */
    @PreAuthorize("hasRole('user')")
    @RequestMapping("/api/**")
    public Mono<ResponseEntity<byte[]>> userRoutes(HttpServletRequest request) throws IOException {
        return forwardRequest(request);
    }

    /**
     * Copies the incoming request to the backend and enriches it with authenticated user headers.
     *
     * @param request original servlet request
     * @return backend response body and status
     * @throws IOException when the request body cannot be read
     */
    private Mono<ResponseEntity<byte[]>> forwardRequest(HttpServletRequest request) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        String email = "";

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            userId = jwtAuth.getToken().getSubject();
            email = jwtAuth.getToken().getClaimAsString("email");
        }

        String targetUrl = backendBaseUrl + request.getRequestURI();
        String method = request.getMethod();
        byte[] body = request.getInputStream().readAllBytes();

        return webClient
                .method(HttpMethod.valueOf(method))
                .uri(targetUrl)
                .header("X-User-Id", userId)
                .header("X-User-Email", email)
                .header("Authorization", request.getHeader("Authorization"))
                .bodyValue(body.length > 0 ? body : new byte[0])
                .exchangeToMono(response -> response.toEntity(byte[].class));
    }
}
