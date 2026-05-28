package pip.banca.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pip.banca.entities.Transaction;
import pip.banca.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service that executes money transfers and records transaction history.
 */
@Service
public class TransactionService {
    /**
     * Repository used to persist transaction records.
     */
    public final TransactionRepository repo;

    /**
     * Service used to load and save IBAN account balances.
     */
    @Autowired
    private UserIbanService ibanServ;

    /**
     * Creates a transaction service backed by the supplied repository.
     *
     * @param repo transaction repository
     */
    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    /**
     * Transfers money from one account to another and stores a transaction record.
     *
     * @param senderID identifier of the user initiating the transfer
     * @param receiverID identifier of the user receiving the transfer
     * @param senderIBAN IBAN sending money
     * @param receiverIBAN IBAN receiving money
     * @param amount amount to transfer
     * @param description transfer description
     * @return true when the transfer and transaction save succeed
     */
    @Transactional
    public boolean StartTransaction(UUID senderID, UUID receiverID, String senderIBAN, String receiverIBAN, Double amount, String description) {
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
                    LocalDateTime.now(),
                    description
            );
            repo.save(transaction);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
