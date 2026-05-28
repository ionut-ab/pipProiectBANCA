package pip.banca.services;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.stereotype.Service;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;
import pip.banca.repositories.UserIbanRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


/**
 * Service for account lookup, account creation, and balance updates.
 */
@Service
public class UserIbanService {
    /**
     * Repository used to persist and query account mappings.
     */
    private final UserIbanRepository repo;

    /**
     * Creates an account service backed by the supplied repository.
     *
     * @param repo account mapping repository
     */
    public UserIbanService(UserIbanRepository repo) {
        this.repo = repo;
    }

    /**
     * Finds an account mapping by IBAN.
     *
     * @param IBAN account IBAN
     * @return account mapping with the supplied IBAN, when present
     */
    public Optional<UserIbanMapping> GetUserIban(String IBAN){
        return repo.findByIBAN(IBAN);
    }

    /**
     * Adds money to an account and saves the updated account state.
     *
     * @param IBAN account IBAN
     * @param amount amount to add
     * @return updated account, or null when the account is missing or the update fails
     */
    public UserIbanMapping AddMoney(String IBAN, Double amount){
        UserIbanMapping account;
        try
        {
            var result = this.GetUserIban(IBAN);
            if(result.isEmpty()){
                throw new Exception("ACCOUNT NOT FOUND");
            }
            account = result.get();
            account.addMoney(amount);
            this.repo.save(account);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

        return account;
    }

    /**
     * Subtracts money from an account and saves the updated account state.
     *
     * @param IBAN account IBAN
     * @param amount amount to subtract
     * @return updated account, or null when the account is missing or the update fails
     */
    public UserIbanMapping SubtractMoney(String IBAN, Double amount){
        UserIbanMapping account;
        try
        {
            var result = this.GetUserIban(IBAN);
            if(result.isEmpty()){
                throw new Exception("ACCOUNT NOT FOUND");
            }
            account = result.get();
            account.subtractMoney(amount);
            this.repo.save(account);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

        return account;
    }

    /**
     * Persists the current state of an account mapping.
     *
     * @param account account mapping to save
     * @return true when the save succeeds
     */
    public boolean SaveAccountState(UserIbanMapping account){
        try{
            this.repo.save(account);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }

        System.out.println("Successfully saved account with IBAN " + account.getIBAN() + " !");
        return true;
    }

    /**
     * Persists a new account mapping.
     *
     * @param account account mapping to create
     * @return true when the account is saved successfully
     */
    public boolean CreateAccount(UserIbanMapping account){
        try{
            this.repo.save(account);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }

        System.out.println("Successfully created account with IBAN " + account.getIBAN() + " !");
        return true;
    }

    /**
     * Creates a new account for a user with the default starting balance.
     *
     * @param IBAN account IBAN
     * @param accountOwner owner of the new account
     * @return true when the account is saved successfully
     */
    public boolean CreateAccount(String IBAN, User accountOwner) {
        UserIbanMapping account = new UserIbanMapping(IBAN, accountOwner);
        account.setBalance(5000.0);
        return this.SaveAccountState(account);
    }
}
