package auction.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.common.strategy.FixedBidStrategy;
import com.app.dto.response.message.MessageResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.AutoBidEntity;
import com.app.entity.item.Electronics;
import com.app.entity.item.ItemEntity;
import com.app.entity.user.UserEntity;
import com.app.repository.AuctionRepository;
import com.app.repository.AutoBidRepository;
import com.app.repository.UserRepository;
import com.app.service.bid.AutoBidService;
import com.app.service.bid.BidService;


@ExtendWith(MockitoExtension.class)
public class AutoBidTest {

    @Mock
    private AutoBidRepository autoBidRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private BidService bidService;

    @Mock
    private FixedBidStrategy fixedBidStrategy;

    @InjectMocks
    private AutoBidService autoBidService;
    
    @Test
    void shouldCreateAutoBid(){

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        UserEntity seller = new UserEntity("seller", "123@gmail.com","0223456789", "12345678");

        user.setUserId("U1");

        // Item
        ItemEntity item = new Electronics(ItemType.ELECTRONICS,"Iphone 16 prm", "abcxyz", new Money(17500000), "Smart phone", "IPhone 16", "new", "Blue", "256GB", 6);

        // Auction 
        AuctionEntity auction = new AuctionEntity("IPhone 16 promax",item, seller, LocalDateTime.now());

        auction.setAuctionId("A1");

        when(autoBidRepository.findByUser_UserIdAndAuction_AuctionId("U1", "A1")).thenReturn(Optional.empty());

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

        when(auctionRepository.findByAuctionId("A1")).thenReturn(Optional.of(auction));

        MessageResponse response = autoBidService.createAutoBid("U1", "A1", new Money(1000));

        assertEquals("Tạo auto bid thành công", response.getMessage());

        verify(autoBidRepository).save(any(AutoBidEntity.class));
    }
}
