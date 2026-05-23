package auction.notify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dto.response.MessageResponse;
import com.app.entity.NotificationEntity;
import com.app.entity.UserEntity;
import com.app.exception.notification.NotificationNotFoundException;
import com.app.repository.NotificationRepository;
import com.app.repository.UserRepository;
import com.app.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    // ====== CREATE NOTIFICATION ======
    @Test
    void shouldCreateNotification(){

        NotificationRepository notificationRepository = mock(NotificationRepository.class);

        NotificationService notificationService = new NotificationService(notificationRepository);

        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        notificationService.notifyUser(user, "Có bid mới");

        verify(notificationRepository).save(any(NotificationEntity.class));
    }

    // ====== GET USER NOTIFICATIONS ======
    @Test
    void shouldGetUserNotifications(){

        NotificationRepository notificationRepository = mock(NotificationRepository.class);

        NotificationService notificationService = new NotificationService(notificationRepository);

        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        NotificationEntity notification1 = new NotificationEntity("Có bid mới", user);
        NotificationEntity notification2 = new NotificationEntity("Đấu giá sắp kết thúc", user);

        List<NotificationEntity> notifications = List.of(notification1, notification2);

        when(notificationRepository.findByUser_UserIdOrderByCreateAtDesc(user.getUserId())).thenReturn(notifications);

        List<NotificationEntity> result = notificationService.getUserNotifications(user.getUserId());

        assertEquals(2, result.size());

        assertEquals("Có bid mới", result.get(0).getMessage());
    }

    // ====== MARK AS READ ======
    @Test
    void shouldMarkNotificationAsRead(){

        NotificationRepository notificationRepository = mock(NotificationRepository.class);

        NotificationService notificationService = new NotificationService(notificationRepository);

        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");
        
        NotificationEntity notification = new NotificationEntity("Có bid mới", user);

        when(notificationRepository.findById("n001")).thenReturn(Optional.of(notification));

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

        notificationService.markAsRead("U1","n001");

        assertTrue(notification.isRead());

        verify(notificationRepository).save(notification);
    }

    // ====== NOTIFICATION NOT FOUND ======
    @Test
    void shouldThrowNotificationNotFound(){

        NotificationRepository notificationRepository = mock(NotificationRepository.class);

        NotificationService notificationService = new NotificationService(notificationRepository);

        when(notificationRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsRead("U1","invalid"));
    }

    // ====== READ ALL ======
    @Test
    void shouldReadAllNotifications(){

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        NotificationEntity notification1 = new NotificationEntity("Có bid mới", user);
        NotificationEntity notification2 = new NotificationEntity("Đấu giá sắp kết thúc", user);

        notification1.setRead(false);
        notification2.setRead(false);

        List<NotificationEntity> notifications = List.of(notification1, notification2);

        when(notificationRepository.findByUser_UserId("U1")).thenReturn(notifications);

        MessageResponse response = notificationService.readAll("U1");

        assertTrue(notification1.isRead());
        assertTrue(notification2.isRead());

        assertEquals("Đã đọc toàn bộ thông báo", response.getMessage());

        verify(notificationRepository).saveAll(notifications);
    }
}
