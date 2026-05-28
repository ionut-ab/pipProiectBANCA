package pip.banca;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;
import pip.banca.services.UserService;

/**
 * Simple controller used for the health-style greeting endpoint.
 */
@RestController
public class HelloController {

    /**
     * Returns the fixed greeting exposed by the application.
     *
     * @return greeting text
     */
    @GetMapping("/say-hello")
    public String index() {
        return "hai noroi";
    }

}
