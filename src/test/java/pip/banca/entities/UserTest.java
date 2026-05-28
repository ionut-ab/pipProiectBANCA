package pip.banca.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void constructorAndSettersExposeUserData() {
        User user = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "1234567890123");

        user.setFirstName("Maria");
        user.setEmail("maria@example.com");
        user.setPhoneNumber("0799999999");
        user.setAddress("Bucuresti");

        assertThat(user.getId()).isNull();
        assertThat(user.getFirstName()).isEqualTo("Maria");
        assertThat(user.getLastName()).isEqualTo("Ionescu");
        assertThat(user.getEmail()).isEqualTo("maria@example.com");
        assertThat(user.getPhoneNumber()).isEqualTo("0799999999");
        assertThat(user.getAddress()).isEqualTo("Bucuresti");
        assertThat(user.getCNP()).isEqualTo("1234567890123");
    }
}
