package auction.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.dto.response.BidResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.wallet.InvalidBidException;
import com.app.mapper.BidMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;
import com.app.service.AuctionService;
import com.app.service.AutoBidService;
import com.app.service.BidService;
import com.app.service.WalletService;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest{

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionService auctionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private BidMapper bidMapper;

    @Mock
    private AutoBidService autoBidService;

    @InjectMocks
    private BidService bidService;

    @Test
    void sellerCannotBidOwnAuction(){

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        seller.setUserId("U1");

        auction.setSeller(seller);

        auction.setAuctionId("A1");

        auction.setStatus(AuctionStatus.OPEN);

        when(auctionService.getEntityByAuctionId("A1")).thenReturn(auction);

        assertThrows(InvalidBidException.class, () -> bidService.placeBid("A1", new Money(1000), "U1"));
    }

    @Test
    void cannotBidClosedAuction(){

        UserEntity user = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        user.setUserId("U1");

        auction.setAuctionId("A1");

        auction.setStatus(AuctionStatus.FINISHED);

        when(auctionService.getEntityByAuctionId("A1")).thenReturn(auction);

        assertThrows(AuctionClosedException.class, () -> bidService.placeBid("A1", new Money(1000), "U1"));
    }

    @Test
    void shouldRefundOldHighestBidder(){

        UserEntity user1 = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");
        user1.setUserId("U1");

        UserEntity user2 = new UserEntity("abcd", "1234@gmail.com", "0987654322", "12345678");

        user2.setUserId("U2");

        user2.getWallet().deposit(new Money(5000));

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());
        auction.setAuctionId("A1");
        auction.setStatus(AuctionStatus.OPEN);

        auction.setHighestBidder(user1);
        auction.setCurrentPrice(new Money(1000));

        when(auctionService.getEntityByAuctionId("A1")).thenReturn(auction);

        when(userRepository.findByUserId("U2")).thenReturn(Optional.of(user2));

        when(bidRepository.findTopByUserAndAuctionOrderByAmount_ValueDesc(any(), any())).thenReturn(Optional.empty());

        doNothing().when(autoBidService).processAutoBid(any(AuctionEntity.class));

        bidService.placeBid("A1", new Money(2000), "U2");

        verify(walletService).refundBid(any(UserEntity.class), eq(new Money(1000)));
    }

    @Test
    void shouldTriggerAutoBid(){

        UserEntity user = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");
        user.setUserId("U1");

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());
        auction.setAuctionId("A1");
        auction.setStatus(AuctionStatus.OPEN);

        when(auctionService.getEntityByAuctionId("A1")).thenReturn(auction);

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

        user.getWallet().deposit(new Money(2000));

        bidService.placeBid("A1", new Money(2000), "U1");

        verify(autoBidService).processAutoBid(auction);
    }


    @Test
    void shouldPlaceBidSuccessfully(){

        UserEntity user = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");
        user.setUserId("U1");

        UserEntity seller = new UserEntity("abc", "123@gmail.com", "0987654321", "12345678");

        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());
        auction.setAuctionId("A1");
        auction.setStatus(AuctionStatus.OPEN);

        when(auctionService.getEntityByAuctionId("A1")).thenReturn(auction);

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

        user.getWallet().deposit(new Money(5000));

        BidResponse bidResponse = new BidResponse();
        bidResponse.setAmount(new BigDecimal(2000));

        when(bidMapper.toResponse(any(BidEntity.class))).thenReturn(bidResponse);

        BidResponse response = bidService.placeBid("A1", new Money(2000), "U1");

        assertEquals(BigDecimal.valueOf(2000), response.getAmount());

        verify(bidRepository).save(any(BidEntity.class));
    }
}