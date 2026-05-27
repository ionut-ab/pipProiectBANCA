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


@RequestMapping("/")
@RestController
public class ProxyController {

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    private final WebClient webClient;

    public ProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PreAuthorize("hasRole('admin')")
    @RequestMapping("/admin/**")
    public Mono<ResponseEntity<byte[]>> adminRoutes(HttpServletRequest request) throws IOException {
        return forwardRequest(request);
    }

    @PreAuthorize("hasRole('user')")
    @RequestMapping("/**")
    public Mono<ResponseEntity<byte[]>> userRoutes(HttpServletRequest request) throws IOException {
        return forwardRequest(request);
    }

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