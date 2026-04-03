package pip.banca;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;

import java.util.Arrays;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			User user = new User("Alice", "alice@example.com");
			try {
				userRepository.save(user);
				System.out.println("User saved!");
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}

		};
	}
}
