package pip.banca.dtos;

/**
 * Request payload used to initiate a transfer between two IBAN accounts.
 */
public class TransactionInit {
    /**
     * IBAN of the account that sends money.
     */
    public String senderIBAN;

    /**
     * IBAN of the account that receives money.
     */
    public String receiverIBAN;

    /**
     * Amount of money to transfer.
     */
    public Double amount;

    /**
     * Human-readable transfer description.
     */
    public String description;
}
