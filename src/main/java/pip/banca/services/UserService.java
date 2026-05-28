package pip.banca.services;

import org.springframework.stereotype.Service;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;

import java.util.List;

/**
 * Service for user creation and lookup operations.
 */
@Service
public class UserService {

    /**
     * Repository used to persist and query users.
     */
    private final UserRepository userRepository;

    /**
     * Creates a user service backed by the supplied repository.
     *
     * @param userRepository repository used by this service
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates and persists a new user.
     *
     * @param firstName user first name
     * @param lastName user last name
     * @param email unique email address
     * @param phoneNumber optional phone number
     * @param address user address
     * @param cnp unique personal identification number
     * @return saved user entity
     */
    public User createUser(String firstName, String lastName, String email, String phoneNumber, String address, String cnp) {
        return userRepository.save(new User(firstName, lastName, email, phoneNumber, address, cnp));
    }

    /**
     * Returns all users in the repository.
     *
     * @return all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by email.
     *
     * @param email email address to search for
     * @return user with the supplied email
     * @throws RuntimeException when no user exists for the email
     */
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
