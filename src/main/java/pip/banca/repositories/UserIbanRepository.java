package pip.banca.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * Repository for persisting and querying IBAN account mappings.
 */
public interface UserIbanRepository extends JpaRepository<UserIbanMapping, String> {
    // Derived query methods
    /**
     * Finds all account mappings owned by a user.
     *
     * @param accountOwner owner to search for
     * @return account mappings owned by the user
     */
    List<UserIbanMapping> findByAccountOwner(User accountOwner);

    /**
     * Finds an account mapping by IBAN.
     *
     * @param IBAN account IBAN
     * @return account mapping with the supplied IBAN, when present
     */
    Optional<UserIbanMapping> findByIBAN(String IBAN);

}
