package pip.banca.services;

import org.junit.jupiter.api.Test;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;
import pip.banca.repositories.UserIbanRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserIbanServiceTest {

    private final UserIbanRepository repository = mock(UserIbanRepository.class);
    private final UserIbanService service = new UserIbanService(repository);

    @Test
    void getUserIbanDelegatesToRepository() {
        UserIbanMapping account = new UserIbanMapping("RO01BANK", new User());
        when(repository.findByIBAN("RO01BANK")).thenReturn(Optional.of(account));

        assertThat(service.GetUserIban("RO01BANK")).contains(account);
    }

    @Test
    void addMoneySavesUpdatedAccountOrReturnsNullWhenMissing() {
        UserIbanMapping account = new UserIbanMapping("RO01BANK", new User());
        when(repository.findByIBAN("RO01BANK")).thenReturn(Optional.of(account));
        when(repository.findByIBAN("MISSING")).thenReturn(Optional.empty());

        UserIbanMapping result = service.AddMoney("RO01BANK", 75.0);

        assertThat(result).isSameAs(account);
        assertThat(account.getBalance()).isEqualTo(75.0);
        verify(repository).save(account);
        assertThat(service.AddMoney("MISSING", 10.0)).isNull();
    }

    @Test
    void subtractMoneySavesUpdatedAccountOrReturnsNullOnErrors() {
        UserIbanMapping account = new UserIbanMapping("RO01BANK", new User());
        account.setBalance(100.0);
        when(repository.findByIBAN("RO01BANK")).thenReturn(Optional.of(account));
        when(repository.findByIBAN("MISSING")).thenReturn(Optional.empty());

        UserIbanMapping result = service.SubtractMoney("RO01BANK", 40.0);

        assertThat(result).isSameAs(account);
        assertThat(account.getBalance()).isEqualTo(60.0);
        verify(repository).save(account);
        assertThat(service.SubtractMoney("RO01BANK", 1000.0)).isNull();
        assertThat(service.SubtractMoney("MISSING", 1.0)).isNull();
    }

    @Test
    void saveAndCreateAccountReportRepositoryFailures() {
        UserIbanMapping account = new UserIbanMapping("RO01BANK", new User());
        UserIbanRepository failingRepository = mock(UserIbanRepository.class);
        when(failingRepository.save(account)).thenThrow(new RuntimeException("cannot save"));
        UserIbanService failingService = new UserIbanService(failingRepository);

        assertThat(service.SaveAccountState(account)).isTrue();
        assertThat(service.CreateAccount(account)).isTrue();
        assertThat(failingService.SaveAccountState(account)).isFalse();
        assertThat(failingService.CreateAccount(account)).isFalse();
    }

    @Test
    void createAccountForUserStartsWithDefaultBalance() {
        User owner = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");

        boolean created = service.CreateAccount("RO01BANK", owner);

        assertThat(created).isTrue();
        org.mockito.ArgumentCaptor<UserIbanMapping> captor = org.mockito.ArgumentCaptor.forClass(UserIbanMapping.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getIBAN()).isEqualTo("RO01BANK");
        assertThat(captor.getValue().getAccountOwner()).isSameAs(owner);
        assertThat(captor.getValue().getBalance()).isEqualTo(5000.0);
    }
}
