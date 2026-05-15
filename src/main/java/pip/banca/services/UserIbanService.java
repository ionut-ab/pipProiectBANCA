package pip.banca.services;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.stereotype.Service;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;
import pip.banca.repositories.UserIbanRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserIbanService {
    private final UserIbanRepository repo;

    public UserIbanService(UserIbanRepository repo) {
        this.repo = repo;
    }

    public Optional<UserIbanMapping> GetUserIban(String IBAN){
        return repo.findByIBAN(IBAN);
    }

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
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

        return account;
    }

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
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

        return account;
    }

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

    public boolean CreateAccount(String IBAN, User accountOwner) {
        UserIbanMapping account = new UserIbanMapping(IBAN, accountOwner);
        return this.SaveAccountState(account);
    }
}
