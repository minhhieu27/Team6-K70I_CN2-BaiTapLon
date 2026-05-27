package auction.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.TransactionType;
import com.app.common.enums.VIPLevel;
import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.repository.AuctionRepository;
import com.app.service.auction.AuctionNotifyService;
import com.app.service.auction.AuctionPaymentService;
import com.app.service.bid.AutoBidService;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;

@ExtendWith(MockitoExtension.class)
public class AuctionPaymentServiceTest {
    
    @Mock
    private WalletService walletService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AutoBidService autoBidService;

    @Mock
    private AuctionNotifyService auctionNotifyService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AuctionPaymentService auctionPaymentService;

    private UserEntity winner;

    private UserEntity seller;

    @BeforeEach
    void setUp(){

        winner = new UserEntity("winner", "winner@gmail.com", "0123456789", "12345678");

        seller = new UserEntity("seller", "seller@gmail.com", "0123456788", "12345678");
    }

    @Test
    void shouldThrowExceptionWhenNoWinner(){

        AuctionEntity auction = mock(AuctionEntity.class);

        when(auction.isPaid()).thenReturn(false);

        when(auction.getHighestBidder()).thenReturn(null);

        auctionPaymentService.settleAuction(auction);

        verify(auction).setStatus(AuctionStatus.FAILED);

        verify(auctionRepository).save(auction);
    }

    @Test
    void shouldSettleAuctionSuccessfully(){

        AuctionEntity auction = mock(AuctionEntity.class);

        winner.getWallet().deposit(new Money(50000L));
        Money amount = new Money(1000L);

        winner.setVipLevel(VIPLevel.BRONZE);

        when(auction.isPaid()).thenReturn(false);

        when(auction.getHighestBidder()).thenReturn(winner);

        when(auction.getSeller()).thenReturn(seller);
        
        when(auction.getCurrentPrice()).thenReturn(amount);

        winner.getWallet().lock(amount);

        auctionPaymentService.settleAuction(auction);

        ArgumentCaptor<Money> payCaptor = ArgumentCaptor.forClass(Money.class);

        verify(walletService).paySeller(eq(winner), eq(seller), payCaptor.capture());

        Money paidAmount = payCaptor.getValue();

        assertEquals(970L, paidAmount.getValue().longValue());

        ArgumentCaptor<Money> refundCaptor = ArgumentCaptor.forClass(Money.class);

        verify(walletService).refundBid(eq(winner), refundCaptor.capture());

        Money refundAmount = refundCaptor.getValue();

        assertEquals(30L, refundAmount.getValue().longValue());

        verify(transactionService).createTransaction(winner.getWallet(), paidAmount, TransactionType.PAYMENT);

        verify(transactionService).createTransaction(seller.getWallet(), paidAmount, TransactionType.RECEIVE);

        verify(transactionService).createTransaction(winner.getWallet(), refundAmount, TransactionType.REFUND);

        verify(autoBidService).disableAuctionAutoBids(auction.getAuctionId());

        verify(auctionNotifyService).notifyAuctionFinished(auction);

        verify(auctionRepository).save(auction);

        verify(auction).setPaid(true);
    }
}
