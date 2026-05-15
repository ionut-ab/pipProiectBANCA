package pip.banca.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/")
@RestController
public class ProxyController {

    @PreAuthorize("hasRole('admin')")
    @RequestMapping("/admin/**")
    public ResponseEntity<?> adminRoutes(HttpServletRequest request) {
        // Only Keycloak users with the 'admin' realm role can reach this
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('user')")
    @RequestMapping("/**")
    public ResponseEntity<?> userRoutes(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }
}