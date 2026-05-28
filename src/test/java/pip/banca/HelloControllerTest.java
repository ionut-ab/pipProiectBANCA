package pip.banca;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerTest {

    @Test
    void indexReturnsGreeting() {
        assertThat(new HelloController().index()).isEqualTo("hai noroi");
    }
}
