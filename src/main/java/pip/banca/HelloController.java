package pip.banca;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pip.banca.entities.User;
import pip.banca.repositories.UserRepository;
import pip.banca.services.UserService;

@RestController
public class HelloController {

        private User UserRepository;

    @GetMapping("/say-hello")
    public String index() {
        return "hai noroi";
    }

}