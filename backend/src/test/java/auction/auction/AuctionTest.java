package auction.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.wallet.InvalidBidException;
import com.app.repository.AuctionRepository;
import com.app.service.AuctionService;
import com.app.service.WalletService;

@ExtendWith(MockitoExtension.class)
public class AuctionTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private AuctionService auctionService;
    
    // ====== SUCCESS BID ======
    @Test
    void shouldAcceptValidBid(){
        
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        UserEntity seller = new UserEntity("seller", "123@gmail.com","0223456789", "12345678");
        // Auction 
        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        // Bid 
        BidEntity bid = new BidEntity(user, new Money(400));

        auction.addBid(bid);

        assertEquals(400, auction.getCurrentPrice().getValue().doubleValue());
    }

    // ====== INVALID BID ======
    @Test
    void shouldRejectLowBid(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        UserEntity seller = new UserEntity("seller", "123@gmail.com","0223456789", "12345678");
        // Auction 
        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        auction.setStatus(AuctionStatus.OPEN);

        BidEntity bid = new BidEntity(user, new Money(300));

        assertThrows(InvalidBidException.class, () -> auction.addBid(bid));
    }

    // ======= NULL BID ======
    @Test
    void shouldRejectNullBid(){


        UserEntity seller = new UserEntity("seller", "123@gmail.com","0223456789", "12345678");
        // Auction 
        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        auction.setStatus(AuctionStatus.OPEN);

        assertThrows(InvalidBidException.class, () -> auction.addBid(null));
    }

    // ====== MULTIPLE BID ======
    @Test
    void shouldAcceptHigherBid(){
        UserEntity user1 = new UserEntity("hieu", "abc123@gmail.com","0123456788", "123456");
         // User
        UserEntity user2 = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        UserEntity seller = new UserEntity("seller", "123@gmail.com","0223456789", "12345678");
        // Auction 
        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        auction.setStatus(AuctionStatus.OPEN);

        BidEntity bid1 = new BidEntity(user1, new Money(330));
        BidEntity bid2 = new BidEntity(user2, new Money(350));

        auction.addBid(bid1);
        auction.addBid(bid2);

        assertEquals(350, auction.getCurrentPrice().getValue().doubleValue());
    }

    // ====== AUTO EXTEND ======
    @Test
    void shouldExtendAuctionTime(){

        UserEntity seller = new UserEntity("seller", "123@gmail.com","0223456789", "12345678");
        // Auction 
        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        auction.setStatus(AuctionStatus.OPEN);

        auction.setEndTime(LocalDateTime.now().plusSeconds(20));

        LocalDateTime oldEndTime = auction.getEndTime();

        auctionService.extendTime(auction);

        assertTrue(auction.getEndTime().isAfter(oldEndTime));
    }
}