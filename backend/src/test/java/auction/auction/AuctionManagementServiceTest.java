package auction.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.auction.CreateElectronicsAuctionRequest;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.item.Electronics;
import com.app.entity.item.ItemEntity;
import com.app.entity.user.UserEntity;
import com.app.factory.ItemFactoryManager;
import com.app.mapper.AuctionMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.UserRepository;
import com.app.service.auction.AuctionManagementService;
import com.app.service.auction.AuctionNotifyService;
import com.app.service.auction.AuctionPaymentService;
import com.app.service.auction.AuctionQuerryService;


@ExtendWith(MockitoExtension.class)
public class AuctionManagementServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuctionMapper auctionMapper;

    @Mock
    private ItemFactoryManager itemFactoryManager;

    @Mock
    private AuctionQuerryService auctionQuerryService;

    @Mock
    private AuctionPaymentService auctionPaymentService;

    @Mock
    private AuctionNotifyService auctionNotifyService;

    @InjectMocks
    private AuctionManagementService auctionManagementService;;

    private UserEntity seller;

    @BeforeEach
        void setup(){
           
            seller = new UserEntity("seller", "seller@gmail.com", "0123456789", "12345678");
        }

    @Test
    void shouldCreateAuctionSuccessfully(){

        CreateElectronicsAuctionRequest req = new CreateElectronicsAuctionRequest();

        req.setTitle("IPhone 16 Pro Max");

        req.setItemType(ItemType.ELECTRONICS);

        req.setItemName("IPhone 16 prm");

        req.setDescription("abcxyz");

        req.setStartPrice(BigDecimal.valueOf(13500000));

        req.setBrand("IPhone");

        req.setColor("Blue");

        req.setStorage("256GB");

        req.setWarrantyMonths(8);

        req.setConditionStatus("New");

        req.setImageUrls(List.of("http://img1.jpg", "http://img2.jpg"));

        ItemEntity item = new Electronics(req.getItemName(), req.getDescription(), new Money(req.getStartPrice()), req.getBrand(), req.getModel(), req.getColor(), req.getStorage(), req.getConditionStatus(), req.getWarrantyMonths());

        when(itemFactoryManager.createItem(req.getItemType(), req)).thenReturn(item);

        when(userRepository.findByUserId(seller.getUserId())).thenReturn(Optional.of(seller));

        when(auctionRepository.save(any(AuctionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        auctionManagementService.createAuction(req, seller.getUserId());

        ArgumentCaptor<AuctionEntity> captor = ArgumentCaptor.forClass(AuctionEntity.class);

        verify(auctionRepository).save(captor.capture());

        AuctionEntity auction = captor.getValue();

        assertEquals("IPhone 16 Pro Max", auction.getTitle());

        assertEquals(seller, auction.getSeller());

        assertEquals(BigDecimal.valueOf(13500000), auction.getCurrentPrice().getValue());

        assertEquals(2, auction.getImages().size());

        verify(auctionNotifyService).notifyNewAuction(auction);
    }

    @Test
    void shouldUpdateStatusToScheduled(){

        AuctionEntity auction = new AuctionEntity();

        auction.setStartTime(LocalDateTime.now().plusMinutes(10));

        auction.setEndTime(LocalDateTime.now().plusMinutes(40));

        auctionManagementService.updateStatus(auction);

        assertEquals(AuctionStatus.SCHEDULED, auction.getStatus());
    }

    @Test
    void shouldUpdateStatusToOpen(){

        AuctionEntity auction = new AuctionEntity();

        auction.setStartTime(LocalDateTime.now().minusMinutes(5));

        auction.setEndTime(LocalDateTime.now().plusMinutes(10));

        auctionManagementService.updateStatus(auction);

        assertEquals(AuctionStatus.OPEN, auction.getStatus());
    }

    @Test
    void shouldUpdateStatusToFinished(){
        
        AuctionEntity auction = new AuctionEntity();

        auction.setStartTime(LocalDateTime.now().minusMinutes(10));

        auction.setEndTime(LocalDateTime.now().minusMinutes(1));

        auctionManagementService.updateStatus(auction);

        assertEquals(AuctionStatus.FINISHED, auction.getStatus());
    }

    @Test
    void shouldExtendAuctionTime(){
        
        AuctionEntity auction = new AuctionEntity();

        auction.setEndTime(LocalDateTime.now().plusSeconds(20));

        auctionManagementService.extendTime(auction);

        verify(auctionRepository).save(auction);

        verify(auctionNotifyService).notifyAuctionExtend(auction);
    }

    @Test
    void shouldFinishAuctionSuccessfully(){

        AuctionEntity auction = new AuctionEntity();

        auction.setStartTime(LocalDateTime.now().minusMinutes(10));

        auction.setEndTime(LocalDateTime.now().minusMinutes(1));

        when(auctionQuerryService.getEntityByAuctionId("A1")).thenReturn(auction);

        auctionManagementService.finishAuction("A1");

        verify(auctionPaymentService).settleAuction(auction);
    }

    @Test
    void shouldNotFinishAuctionWhenNotFinishedStatus(){

        AuctionEntity auction = new AuctionEntity();

        auction.setStartTime(LocalDateTime.now().minusMinutes(5));

        auction.setEndTime(LocalDateTime.now().plusMinutes(10));

        when(auctionQuerryService.getEntityByAuctionId("A1")).thenReturn(auction);

        auctionManagementService.finishAuction("A1");

        verify(auctionPaymentService, never()).settleAuction(auction);
    }
}