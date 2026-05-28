package pip.banca;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;

import java.util.Arrays;

/**
 * Spring Boot entry point for the banking service.
 */
@SpringBootApplication
public class Application {

	/**
	 * Starts the Spring application context.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Creates a startup runner that inserts a seed user when the application starts.
	 *
	 * @param userRepository repository used to persist the seed user
	 * @return command-line runner executed by Spring Boot
	 */
	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			User user = new User("Alice", "Popescu", "alice@example.com", "07namcartela", "sub podu ros", "nam");
			try {
				userRepository.save(user);
				System.out.println("User saved!");
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}

		};
	}
}
