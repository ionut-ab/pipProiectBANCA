package pip.banca;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApplicationTest {

    @Test
    void initSavesSeedUser() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);

        CommandLineRunner runner = new Application().init(userRepository);
        runner.run();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void initSwallowsRepositoryExceptions() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        org.mockito.Mockito.when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("database unavailable"));

        CommandLineRunner runner = new Application().init(userRepository);
        runner.run();

        verify(userRepository).save(any(User.class));
    }
}
