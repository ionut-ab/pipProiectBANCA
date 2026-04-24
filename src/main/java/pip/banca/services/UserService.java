package pip.banca.services;

import org.springframework.stereotype.Service;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String firstName, String lastName, String email, String phoneNumber, String address, String cnp) {
        return userRepository.save(new User(firstName, lastName, email, phoneNumber, address, cnp));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}