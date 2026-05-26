package auction.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.entity.user.UserEntity;
import com.app.entity.wallet.Transaction;
import com.app.entity.wallet.Wallet;
import com.app.mapper.TransactionMapper;
import com.app.repository.TransactionRepository;
import com.app.service.wallet.TransactionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith (MockitoExtension.class)
public class TransactionTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;
    
    // ====== DEPOSIT ======
    @Test
    void shouldCreateDepositTransaction(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        Wallet wallet = user.getWallet();

        Money amount = new Money(5000L);

        TransactionType type = TransactionType.DEPOSIT;

        transactionService.createTransaction(wallet, amount, type);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(type, transaction.getType());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
    }

    // ====== WITHDRAW ======
    @Test
    void shouldCreateWithdrawTransaction(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        Wallet wallet = user.getWallet();

        Money amount = new Money(5000L);

        TransactionType type = TransactionType.WITHDRAW;

        transactionService.createTransaction(wallet, amount, type);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(type, transaction.getType());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    void shouldCreateBidLockTransaction(){

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        Wallet wallet = user.getWallet();

        Money amount = new Money(5000L);

        TransactionType type = TransactionType.BID_LOCK;

        transactionService.createTransaction(wallet, amount, type);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(type, transaction.getType());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    void shouldCreateRefundTransaction(){

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        Wallet wallet = user.getWallet();

        Money amount = new Money(5000L);

        TransactionType type = TransactionType.REFUND;

        transactionService.createTransaction(wallet, amount, type);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(type, transaction.getType());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    void shouldCreatePaymentTransaction(){

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        Wallet wallet = user.getWallet();

        Money amount = new Money(5000L);

        TransactionType type = TransactionType.PAYMENT;

        transactionService.createTransaction(wallet, amount, type);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(type, transaction.getType());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    void shouldCreateReceiveTransaction(){

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        Wallet wallet = user.getWallet();

        Money amount = new Money(5000L);

        TransactionType type = TransactionType.RECEIVE;

        transactionService.createTransaction(wallet, amount, type);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(type, transaction.getType());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
    }
}