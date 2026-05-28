package pip.banca.entities;


import jakarta.persistence.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity that maps an IBAN account to its owning user and current balance.
 */
@Entity
@Table(name = "user_iban_mapping")
public class UserIbanMapping {
    /**
     * IBAN that uniquely identifies this account.
     */
    @Id
    private String IBAN;

    /**
     * User that owns the account.
     */
    @ManyToOne
    @JoinColumn(name="account_owner")
    private User accountOwner;

    /**
     * Current account balance.
     */
    @Column(nullable = false)
    private Double balance;

    /**
     * UTC timestamp when the account mapping was created.
     */
    @Column
    private LocalDateTime creationTimestamp;

    /**
     * Creates an account mapping for a user with a zero starting balance.
     *
     * @param IBAN unique account IBAN
     * @param account_owner user that owns the account
     */
    public UserIbanMapping(String IBAN, User account_owner){
        this.IBAN = IBAN;
        this.accountOwner = account_owner;
        this.balance = 0.0;
        this.creationTimestamp = LocalDateTime.now(Clock.systemUTC());
    }

    /**
     * Creates an empty account mapping for JPA.
     */
    public UserIbanMapping() {

    }

    /**
     * Gets the account balance.
     *
     * @return current balance
     */
    public double getBalance() {return balance;}

    /**
     * Gets the account IBAN.
     *
     * @return account IBAN
     */
    public String getIBAN(){return IBAN;}

    /**
     * Gets the user that owns the account.
     *
     * @return account owner
     */
    public User getAccountOwner() {return accountOwner;}

    /**
     * Adds money to this account.
     *
     * @param money amount to add
     * @throws Exception when the amount is negative
     */
    public void addMoney(Double money) throws Exception{
        if(money < 0){
            throw new Exception("Can't add NEGATIVE money!");
        }
        this.balance += money;
    }

    /**
     * Subtracts money from this account.
     *
     * @param money amount to subtract
     * @throws Exception when the amount is negative or exceeds the current balance
     */
    public void subtractMoney(Double money) throws Exception {
        if (money < 0) {
            throw new Exception("The amount to subtract must be positive");
        }
        if (this.balance < money) {
            throw new Exception("Insufficient funds");
        }
        this.balance -= money;
    }

    /**
     * Gets the account creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    /**
     * Updates the account balance directly.
     *
     * @param balance new balance
     */
    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
