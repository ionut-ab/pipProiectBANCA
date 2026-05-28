package pip.banca.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIbanMappingTest {

    @Test
    void constructorCreatesEmptyAccountForOwner() {
        User owner = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");

        UserIbanMapping account = new UserIbanMapping("RO01BANK", owner);

        assertThat(account.getIBAN()).isEqualTo("RO01BANK");
        assertThat(account.getAccountOwner()).isSameAs(owner);
        assertThat(account.getBalance()).isZero();
        assertThat(account.getCreationTimestamp()).isNotNull();
    }

    @Test
    void addMoneyIncreasesBalanceAndRejectsNegativeAmounts() throws Exception {
        UserIbanMapping account = new UserIbanMapping("RO01BANK", new User());

        account.addMoney(250.0);

        assertThat(account.getBalance()).isEqualTo(250.0);
        assertThatThrownBy(() -> account.addMoney(-1.0))
                .isInstanceOf(Exception.class)
                .hasMessage("Can't add NEGATIVE money!");
    }

    @Test
    void subtractMoneyDecreasesBalanceAndRejectsInvalidAmounts() throws Exception {
        UserIbanMapping account = new UserIbanMapping("RO01BANK", new User());
        account.setBalance(300.0);

        account.subtractMoney(125.0);

        assertThat(account.getBalance()).isEqualTo(175.0);
        assertThatThrownBy(() -> account.subtractMoney(-1.0))
                .isInstanceOf(Exception.class)
                .hasMessage("The amount to subtract must be positive");
        assertThatThrownBy(() -> account.subtractMoney(500.0))
                .isInstanceOf(Exception.class)
                .hasMessage("Insufficient funds");
    }
}
