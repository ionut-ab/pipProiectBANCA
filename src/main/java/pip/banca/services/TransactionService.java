package pip.banca.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pip.banca.repositories.TransactionRepository;

@Service
public class TransactionService {
    public final TransactionRepository repo;

    @Autowired
    private UserIbanService ibanServ;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    public boolean StartTransaction(String sender_iban, String receiver_iban, Double amount)
    {
        try{
            var senderAccount = ibanServ.SubtractMoney(sender_iban, -amount);
            if (senderAccount == null){
                throw new Exception("Sender account has insufficient funds!");
            }
            var receiverAccount = ibanServ.AddMoney(receiver_iban, amount);
            if (receiverAccount == null){
                throw new Exception("Sender account has insufficient funds!");
            }

            boolean result = ibanServ.SaveAccountState(senderAccount);
            if(!result){
                throw new Exception("Could not save sender account!");
            }

            result = ibanServ.SaveAccountState(receiverAccount);
            if(!result){
                throw new Exception("Could not save receiver account!");
            }

        }catch(Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }
}
