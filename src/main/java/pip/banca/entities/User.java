package pip.banca.entities;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String CNP;

    @Column(nullable = false)
    private Double balance;


    // Constructors
    public User() {}

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
        this.balance = 0.0;
    }

    // Getters & setters
    public UUID getId() { return id; }
    public String getLastName() {return lastName;}
    public String getFirstName() { return firstName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() {return phoneNumber;}
    public String getCNP() {return CNP;}
    public double getBalance() {return balance;}

    public void setFirstName(String name) { this.firstName = name; }
    public void setAddress(String address) {this.address = address; }
    public void setPhoneNumber(String phNumber) {this.phoneNumber = phNumber;}
    public void setEmail(String email) { this.email = email; }


    public void addMoney(float money) throws Exception{
        if(money < 0){
            throw new Exception("Can't add NEGATIVE money!");
        }
        this.balance += money;
    }

    public void subtractMoney(float money) throws Exception{
        if(money > 0){
            throw new Exception("Can't subtract POSITIVE money!");
        }
        this.balance -= money;
    }
}