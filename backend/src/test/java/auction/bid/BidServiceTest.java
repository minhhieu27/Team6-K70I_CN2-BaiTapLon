package auction.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.ItemType;
import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.dto.response.bid.BidResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.BidEntity;
import com.app.entity.item.Electronics;
import com.app.entity.item.ItemEntity;
import com.app.entity.user.UserEntity;
import com.app.mapper.BidMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;
import com.app.service.auction.AuctionManagementService;
import com.app.service.auction.AuctionNotifyService;
import com.app.service.auction.AuctionQuerryService;
import com.app.service.bid.AutoBidService;
import com.app.service.bid.BidCoreService;
import com.app.service.bid.BidService;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest{

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionManagementService auctionManagementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private BidMapper bidMapper;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AutoBidService autoBidService;

    @Mock
    private AuctionQuerryService auctionQuerryService;

    @Mock
    private AuctionNotifyService auctionNotifyService;

    @InjectMocks
    private BidCoreService bidCoreService;

    private BidService bidService;

    @BeforeEach
    void setUp(){
        bidService = new BidService(bidCoreService, bidMapper);
    }

    @Test
    void shouldRefundOldHighestBidder(){

        UserEntity user1 = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");
        user1.setUserId("U1");

        UserEntity user2 = new UserEntity("abcd", "1234@gmail.com", "0987654322", "12345678");

        user2.setUserId("U2");

        user2.getWallet().deposit(new Money(20000000));

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        // Item
        ItemEntity item = new Electronics(ItemType.ELECTRONICS,"Iphone 16 prm", "abcxyz", new Money(17500000), "Smart phone", "IPhone 16", "new", "Blue", "256GB", 6);

        // Auction 
        AuctionEntity auction = new AuctionEntity("IPhone 16 promax",item, seller, LocalDateTime.now());
        auction.setAuctionId("A1");
        auction.setStatus(AuctionStatus.OPEN);

        BidEntity oldBid = new BidEntity(user1, new Money(18500000));

        BidEntity bid = new BidEntity(user2, new Money(19500000));

        when(auctionQuerryService.getEntityByAuctionId("A1")).thenReturn(auction);

        when(userRepository.findByUserId(user1.getUserId())).thenReturn(Optional.of(user1));

        when(userRepository.findByUserId(user2.getUserId())).thenReturn(Optional.of(user2));

        when(bidRepository.findTopByUserAndAuctionOrderByAmount_ValueDesc(any(), any())).thenReturn(Optional.of(oldBid));

        bidService.placeBid(auction.getAuctionId(), oldBid.getAmount(), user1.getUserId());
        bidService.placeBid(auction.getAuctionId(), bid.getAmount(), user2.getUserId());

        verify(walletService).refundBid(user1, oldBid.getAmount());

        verify(transactionService).createTransaction(user1.getWallet(), oldBid.getAmount(), TransactionType.REFUND);
    }

    @Test
    void shouldPlaceBidSuccessfully(){

        UserEntity user = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");
        user.setUserId("U1");

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        // Item
        ItemEntity item = new Electronics(ItemType.ELECTRONICS,"Iphone 16 prm", "abcxyz", new Money(17500000), "Smart phone", "IPhone 16", "new", "Blue", "256GB", 6);

        // Auction 
        AuctionEntity auction = new AuctionEntity("IPhone 16 promax",item, seller, LocalDateTime.now());
        auction.setAuctionId("A1");
        auction.setStatus(AuctionStatus.OPEN);

        when(auctionQuerryService.getEntityByAuctionId("A1")).thenReturn(auction);

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

        user.getWallet().deposit(new Money(50000000));

        BidResponse bidResponse = new BidResponse();
        bidResponse.setAmount(new BigDecimal(20000000));

        when(bidMapper.toResponse(any(BidEntity.class))).thenReturn(bidResponse);

        BidResponse response = bidService.placeBid("A1", new Money(20000000), "U1");

        assertEquals(BigDecimal.valueOf(20000000), response.getAmount());

        verify(bidRepository).save(any(BidEntity.class));
    }
}