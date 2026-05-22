package pip.banca.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pip.banca.entities.Transaction;
import pip.banca.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {
    public final TransactionRepository repo;

    @Autowired
    private UserIbanService ibanServ;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public boolean StartTransaction(UUID senderID, UUID receiverID, String senderIBAN, String receiverIBAN, Double amount) {
        try {
            var senderAccountOpt = ibanServ.GetUserIban(senderIBAN);
            var receiverAccountOpt = ibanServ.GetUserIban(receiverIBAN);

            if (senderAccountOpt.isEmpty()) {
                System.out.println("Sender account not found: " + senderIBAN);
                return false;
            }
            if (receiverAccountOpt.isEmpty()) {
                System.out.println("Receiver account not found: " + receiverIBAN);
                return false;
            }

            var senderAccount = senderAccountOpt.get();
            var receiverAccount = receiverAccountOpt.get();

            senderAccount.subtractMoney(amount);
            receiverAccount.addMoney(amount);

            ibanServ.SaveAccountState(senderAccount);
            ibanServ.SaveAccountState(receiverAccount);

            Transaction transaction = new Transaction(
                    senderAccount.getAccountOwner(),
                    receiverAccount.getAccountOwner(),
                    senderAccount,
                    receiverAccount,
                    amount.floatValue(),
                    LocalDateTime.now()
            );
            repo.save(transaction);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
