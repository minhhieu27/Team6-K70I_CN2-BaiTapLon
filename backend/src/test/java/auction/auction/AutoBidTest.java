package auction.auction;

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

import com.app.common.money.Money;
import com.app.common.strategy.FixedBidStrategy;
import com.app.dto.response.MessageResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.AutoBidEntity;
import com.app.entity.UserEntity;
import com.app.repository.AuctionRepository;
import com.app.repository.AutoBidRepository;
import com.app.repository.UserRepository;
import com.app.service.AutoBidService;
import com.app.service.BidService;


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

        AuctionEntity auction = new AuctionEntity("Iphone", "IPhone 16prm", "256GB", new Money(300), seller, LocalDateTime.now());

        auction.setAuctionId("A1");

        when(autoBidRepository.findByUser_UserIdAndAuction_AuctionId("U1", "A1")).thenReturn(Optional.empty());

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

        when(auctionRepository.findByAuctionId("A1")).thenReturn(Optional.of(auction));

        MessageResponse response = autoBidService.createAutoBid("U1", "A1", new Money(1000));

        assertEquals("Tạo auto bid thành công", response.getMessage());

        verify(autoBidRepository).save(any(AutoBidEntity.class));
    }
}
