package pip.banca.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for creating WebClient builders used by proxy components.
 */
@Configuration
public class WebClientConfig {

    /**
     * Provides a WebClient builder bean.
     *
     * @return WebClient builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
