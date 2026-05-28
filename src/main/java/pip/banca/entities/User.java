package pip.banca.entities;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

/**
 * JPA entity representing an application user.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Database-generated user identifier.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * User first name.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * User last name.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * Unique email address used to identify and log in the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Optional unique phone number for the user.
     */
    @Column(unique = true, nullable = true)
    private String phoneNumber;

    /**
     * User address.
     */
    @Column(nullable = false)
    private String address;

    /**
     * Unique personal identification number.
     */
    @Column(nullable = false, unique = true)
    private String CNP;


    // Constructors
    /**
     * Creates an empty user for JPA.
     */
    public User() {}

    /**
     * Creates a user with the required profile data.
     *
     * @param firstName user first name
     * @param lastName user last name
     * @param email unique email address
     * @param phoneNumber optional unique phone number
     * @param address user address
     * @param cnp unique personal identification number
     */
    public User(String firstName,
                String lastName,
                String email,
                String phoneNumber,
                String address,
                String cnp)
    {
        this.firstName = firstName;
        this.email = email;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.CNP = cnp;
    }

    // Getters & setters
    /**
     * Gets the database-generated user identifier.
     *
     * @return user identifier
     */
    public UUID getId() { return id; }

    /**
     * Gets the user's last name.
     *
     * @return last name
     */
    public String getLastName() {return lastName;}

    /**
     * Gets the user's first name.
     *
     * @return first name
     */
    public String getFirstName() { return firstName; }

    /**
     * Gets the user's email address.
     *
     * @return email address
     */
    public String getEmail() { return email; }

    /**
     * Gets the user's phone number.
     *
     * @return phone number
     */
    public String getPhoneNumber() {return phoneNumber;}

    /**
     * Gets the user's personal identification number.
     *
     * @return personal identification number
     */
    public String getCNP() {return CNP;}

    /**
     * Gets the user's address.
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Updates the user's first name.
     *
     * @param name new first name
     */
    public void setFirstName(String name) { this.firstName = name; }

    /**
     * Updates the user's address.
     *
     * @param address new address
     */
    public void setAddress(String address) {this.address = address; }

    /**
     * Updates the user's phone number.
     *
     * @param phNumber new phone number
     */
    public void setPhoneNumber(String phNumber) {this.phoneNumber = phNumber;}

    /**
     * Updates the user's email address.
     *
     * @param email new email address
     */
    public void setEmail(String email) { this.email = email; }

}
