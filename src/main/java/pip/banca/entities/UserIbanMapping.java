package pip.banca.entities;


import jakarta.persistence.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_iban_mapping")
public class UserIbanMapping {
    @Id
    private String IBAN;

    @ManyToOne
    @JoinColumn(name="account_owner")
    private User accountOwner;

    @Column(nullable = false)
    private Double balance;

    @Column
    private LocalDateTime creationTimestamp;

    public UserIbanMapping(String IBAN, User account_owner){
        this.IBAN = IBAN;
        this.accountOwner = account_owner;
        this.balance = 0.0;
        this.creationTimestamp = LocalDateTime.now(Clock.systemUTC());
    }

    public UserIbanMapping() {

    }

    public double getBalance() {return balance;}
    public String getIBAN(){return IBAN;}
    public User getAccountOwner() {return accountOwner;}

    public void addMoney(Double money) throws Exception{
        if(money < 0){
            throw new Exception("Can't add NEGATIVE money!");
        }
        this.balance += money;
    }

    public void subtractMoney(Double money) throws Exception {
        if (money < 0) {
            throw new Exception("The amount to subtract must be positive");
        }
        if (this.balance < money) {
            throw new Exception("Insufficient funds");
        }
        this.balance -= money;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
