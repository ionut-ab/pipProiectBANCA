package pip.banca.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pip.banca.entities.User;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * Repository for persisting and querying users.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    // Derived query methods
    /**
     * Finds a user by unique email address.
     *
     * @param email email address to search for
     * @return user with the supplied email, when present
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds users with the supplied first name.
     *
     * @param name first name to search for
     * @return users matching the first name
     */
    List<User> findByFirstName(String name);

    /**
     * Finds users with the supplied last name.
     *
     * @param name last name to search for
     * @return users matching the last name
     */
    List<User> findByLastName(String name);

    // Custom JPQL query
    /**
     * Finds users whose email ends with the supplied domain suffix.
     *
     * @param domain email domain suffix
     * @return users with emails matching the domain suffix
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findByEmailDomain(@Param("domain") String domain);
}
