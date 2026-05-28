package pip.banca.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class ProxyForwardingFilterTest {

    @Test
    void filterIsDisabledBecauseProxyControllerHandlesForwarding() {
        ProxyForwardingFilter filter = new ProxyForwardingFilter(WebClient.builder());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/payment");

        Boolean shouldNotFilter = ReflectionTestUtils.invokeMethod(filter, "shouldNotFilter", request);

        assertThat(shouldNotFilter).isTrue();
    }
}
