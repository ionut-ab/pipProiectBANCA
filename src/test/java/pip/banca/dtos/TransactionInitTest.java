package pip.banca.dtos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionInitTest {

    @Test
    void storesTransferRequestFields() {
        TransactionInit init = new TransactionInit();
        init.senderIBAN = "RO01SENDER";
        init.receiverIBAN = "RO02RECEIVER";
        init.amount = 125.50;
        init.description = "rent";

        assertThat(init.senderIBAN).isEqualTo("RO01SENDER");
        assertThat(init.receiverIBAN).isEqualTo("RO02RECEIVER");
        assertThat(init.amount).isEqualTo(125.50);
        assertThat(init.description).isEqualTo("rent");
    }
}
