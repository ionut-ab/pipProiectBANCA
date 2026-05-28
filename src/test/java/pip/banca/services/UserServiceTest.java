package pip.banca.services;

import org.junit.jupiter.api.Test;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);

    @Test
    void createUserSavesNewUser() {
        User saved = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");

        assertThat(result).isSameAs(saved);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsersReturnsRepositoryData() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        assertThat(userService.getAllUsers()).isSameAs(users);
    }

    @Test
    void getByEmailReturnsUserOrThrows() {
        User user = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThat(userService.getByEmail("ana@example.com")).isSameAs(user);
        assertThatThrownBy(() -> userService.getByEmail("missing@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}
