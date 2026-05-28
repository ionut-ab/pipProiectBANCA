package pip.banca.controllers;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ProxyControllerTest {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void routesKeepExpectedMappings() throws Exception {
        RequestMapping classMapping = ProxyController.class.getAnnotation(RequestMapping.class);
        RequestMapping adminMapping = ProxyController.class.getMethod("adminRoutes", jakarta.servlet.http.HttpServletRequest.class)
                .getAnnotation(RequestMapping.class);
        RequestMapping userMapping = ProxyController.class.getMethod("userRoutes", jakarta.servlet.http.HttpServletRequest.class)
                .getAnnotation(RequestMapping.class);

        assertThat(classMapping.value()).containsExactly("/");
        assertThat(adminMapping.value()).containsExactly("/api/admin/**");
        assertThat(userMapping.value()).containsExactly("/api/**");
    }

    @Test
    void userRoutesForwardsBodyAuthorizationAndJwtHeaders() throws IOException {
        AtomicReference<String> receivedBody = new AtomicReference<>();
        AtomicReference<String> userIdHeader = new AtomicReference<>();
        AtomicReference<String> emailHeader = new AtomicReference<>();
        AtomicReference<String> authorizationHeader = new AtomicReference<>();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/payment", exchange -> {
            receivedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            userIdHeader.set(exchange.getRequestHeaders().getFirst("X-User-Id"));
            emailHeader.set(exchange.getRequestHeaders().getFirst("X-User-Email"));
            authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] response = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(201, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        ProxyController controller = new ProxyController(WebClient.builder());
        ReflectionTestUtils.setField(controller, "backendBaseUrl",
                "http://127.0.0.1:" + server.getAddress().getPort());
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user-123")
                .claim("email", "ana@example.com")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/payment");
        request.addHeader("Authorization", "Bearer token");
        request.setContent("{\"amount\":10}".getBytes(StandardCharsets.UTF_8));

        Mono<ResponseEntity<byte[]>> responseMono = controller.userRoutes(request);
        ResponseEntity<byte[]> response = responseMono.block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(new String(response.getBody(), StandardCharsets.UTF_8)).isEqualTo("{\"ok\":true}");
        assertThat(receivedBody.get()).isEqualTo("{\"amount\":10}");
        assertThat(userIdHeader.get()).isEqualTo("user-123");
        assertThat(emailHeader.get()).isEqualTo("ana@example.com");
        assertThat(authorizationHeader.get()).isEqualTo("Bearer token");
    }
}
