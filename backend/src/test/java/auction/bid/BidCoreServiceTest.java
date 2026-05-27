package auction.bid;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.item.Electronics;
import com.app.entity.item.ItemEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.wallet.InvalidBidException;
import com.app.repository.AuctionRepository;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;
import com.app.service.auction.AuctionManagementService;
import com.app.service.auction.AuctionNotifyService;
import com.app.service.auction.AuctionQuerryService;
import com.app.service.bid.BidCoreService;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;

@ExtendWith(MockitoExtension.class)
public class BidCoreServiceTest {
    
    @Mock
    private AuctionQuerryService auctionQuerryService;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock 
    private AuctionManagementService auctionManagementService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionNotifyService auctionNotifyService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BidCoreService bidCoreService;

    @Test
    void sellerCannotBidOwnAuction(){

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        // Item
        ItemEntity item = new Electronics(ItemType.ELECTRONICS,"Iphone 16 prm", "abcxyz", new Money(17500000), "Smart phone", "IPhone 16", "new", "Blue", "256GB", 6);

        // Auction 
        AuctionEntity auction = new AuctionEntity("IPhone 16 promax",item, seller, LocalDateTime.now());

        seller.setUserId("U1");

        auction.setSeller(seller);

        auction.setAuctionId("A1");

        auction.setStatus(AuctionStatus.OPEN);

        when(auctionQuerryService.getEntityByAuctionId("A1")).thenReturn(auction);

        assertThrows(InvalidBidException.class, () -> bidCoreService.excecuteBid("A1", new Money(1000), "U1"));
    }

    @Test
    void cannotBidClosedAuction(){

        UserEntity user = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        // Item
        ItemEntity item = new Electronics(ItemType.ELECTRONICS,"Iphone 16 prm", "abcxyz", new Money(17500000), "Smart phone", "IPhone 16", "new", "Blue", "256GB", 6);

        // Auction 
        AuctionEntity auction = new AuctionEntity("IPhone 16 promax",item, seller, LocalDateTime.now());

        user.setUserId("U1");

        auction.setAuctionId("A1");

        auction.setStatus(AuctionStatus.FINISHED);

        when(auctionQuerryService.getEntityByAuctionId("A1")).thenReturn(auction);

        assertThrows(AuctionClosedException.class, () -> bidCoreService.excecuteBid("A1", new Money(1000), "U1"));
    }
}
