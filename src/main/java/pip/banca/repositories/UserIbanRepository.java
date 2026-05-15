package pip.banca.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserIbanRepository extends JpaRepository<UserIbanMapping, Long> {
    // Derived query methods
    List<UserIbanMapping> findByAccountOwner(User accountOwner);

    Optional<UserIbanMapping> findByIBAN(String IBAN);

}