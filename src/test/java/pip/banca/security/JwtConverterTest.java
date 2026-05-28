package pip.banca.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtConverterTest {

    @Test
    void convertUsesConfiguredPrincipalAndResourceRoles() {
        JwtConverter converter = new JwtConverter();
        ReflectionTestUtils.setField(converter, "principleAttribute", "preferred_username");
        ReflectionTestUtils.setField(converter, "resourceId", "bank-api");
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "subject-id")
                .claim("preferred_username", "ana")
                .claim("resource_access", Map.of("bank-api", Map.of("roles", List.of("user", "admin"))))
                .build();

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertThat(token.getName()).isEqualTo("ana");
        assertThat(token.getAuthorities()).contains(
                new SimpleGrantedAuthority("ROLE_user"),
                new SimpleGrantedAuthority("ROLE_admin"));
    }

    @Test
    void convertFallsBackToSubjectAndNoResourceRolesWhenMissing() {
        JwtConverter converter = new JwtConverter();
        ReflectionTestUtils.setField(converter, "resourceId", "bank-api");
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "subject-id")
                .build();

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertThat(token.getName()).isEqualTo("subject-id");
        assertThat(token.getAuthorities()).isEmpty();
    }
}
