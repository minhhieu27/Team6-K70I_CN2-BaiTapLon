package auction.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dto.response.message.MessageResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.user.UserNotFoundException;
import com.app.repository.UserRepository;
import com.app.service.auction.AuctionFollowService;
import com.app.service.auction.AuctionQuerryService;

@ExtendWith (MockitoExtension.class)
public class AuctionFollowServiceTest {
    
    @Mock
    private AuctionQuerryService auctionQuerryService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuctionFollowService auctionFollowService;

    @Test
    void shouldFollowAuctionSuccessfull(){

        String auctionId = "AUC-001";
        String userId = "USR-001";

        AuctionEntity auction = mock(AuctionEntity.class);

        UserEntity user = new UserEntity("user", "abc123@gmail.com", "0123456789", "12345678");

        when(auctionQuerryService.getEntityByAuctionId(auctionId)).thenReturn(auction);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        MessageResponse response = auctionFollowService.followAuction(auctionId, userId);

        verify(auction).addFollower(user);

        assertEquals("Đã theo dõi phiên đấu giá", response.getMessage());
    }

    @Test
    void shouldUnfollowAuctionSuccessfully(){

        String auctionId = "AUC-001";
        String userId = "USR-001";

        AuctionEntity auction = mock(AuctionEntity.class);

        UserEntity user = new UserEntity("user", "abc123@gmail.com", "0123456789", "12345678");

        when(auctionQuerryService.getEntityByAuctionId(auctionId)).thenReturn(auction);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        MessageResponse response = auctionFollowService.unfollowAuction(auctionId, userId);

        verify(auction).removeFollower(user);

        assertEquals("Đã bỏ theo dõi phiên đấu giá", response.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){

        String auctionId = "AUC-001";
        String userId = "USR-001";

        AuctionEntity auction = mock(AuctionEntity.class);

        UserEntity user = new UserEntity("user", "abc123@gmail.com", "0123456789", "12345678");

        when(auctionQuerryService.getEntityByAuctionId(auctionId)).thenReturn(auction);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> auctionFollowService.followAuction(auctionId, userId));
    }
}
