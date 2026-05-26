package auction.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.dto.response.wallet.WalletResponse;
import com.app.entity.user.UserEntity;
import com.app.entity.wallet.Wallet;
import com.app.exception.user.UserNotFoundException;
import com.app.mapper.WalletMapper;
import com.app.repository.UserRepository;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;

@ExtendWith (MockitoExtension.class)
public class WalletTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private WalletService walletService;

    private UserEntity user;

    private Wallet wallet;

    @BeforeEach
    void setUp(){

        user = new UserEntity(
                "hieu",
                "abc@gmail.com",
                "0123456789",
                "123456");

        wallet = user.getWallet();
    }

    @Test
    void shouldDepositSuccessfully(){

        Money amount = new Money(5000L);

        WalletResponse response = mock(WalletResponse.class);

        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));

        when(walletMapper.toResponse(wallet)).thenReturn(response);

        WalletResponse result = walletService.deposit(user.getUserId(), amount);

        assertEquals(amount, wallet.getBalance());

        verify(transactionService).createTransaction(wallet, amount, TransactionType.DEPOSIT);

        verify(userRepository).save(user);

        assertEquals(response, result);
    }

    @Test
    void shouldWithdrawSuccessfully(){

        wallet.deposit(new Money(1000L));

        Money amount = new Money(300L);

        WalletResponse response = mock(WalletResponse.class);

        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));

        when(walletMapper.toResponse(wallet)).thenReturn(response);

        WalletResponse result = walletService.withdraw(user.getUserId(), amount);

        assertEquals(new Money(700L).getValue(), wallet.getBalance().getValue());

        verify(transactionService).createTransaction(wallet, amount, TransactionType.WITHDRAW);

        verify(userRepository).save(user);

        assertEquals(response, result);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){

        when(userRepository.findByUserId("invalid-id")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.deposit("invalid-id", new Money(1000L)));
    }
}
