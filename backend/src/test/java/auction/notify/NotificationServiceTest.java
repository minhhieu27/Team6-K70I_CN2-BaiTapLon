package auction.notify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.app.dto.response.message.MessageResponse;
import com.app.dto.response.notification.NotificationResponse;
import com.app.entity.notification.NotificationEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.notification.NotificationNotFoundException;
import com.app.mapper.NotificationMapper;
import com.app.repository.NotificationRepository;
import com.app.repository.UserRepository;
import com.app.service.notification.NotificationService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    // ====== CREATE NOTIFICATION ======
    @Test
    void shouldCreateNotification(){

        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        notificationService.notifyUser(user, "Có bid mới");

        verify(notificationRepository).save(any(NotificationEntity.class));
    }

    // ====== GET USER NOTIFICATIONS ======
    @Test
    void shouldGetUserNotifications(){

        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        NotificationEntity notification1 = new NotificationEntity("Có bid mới", user);

        NotificationEntity notification2 = new NotificationEntity("Đấu giá sắp kết thúc", user);

        List<NotificationEntity> notifications = List.of(notification1, notification2);

        Page<NotificationEntity> page = new PageImpl<>(notifications);

        NotificationResponse response = new NotificationResponse();

        when(notificationMapper.toResponse(any(NotificationEntity.class))).thenReturn(response);

        when(notificationRepository.findByUser_UserIdOrderByCreateNotifyAtDesc(eq(user.getUserId()), any(Pageable.class))).thenReturn(page);

        Page<NotificationResponse> result = notificationService.getUserNotifications(user.getUserId(), 0, 10);

        assertEquals(2, result.getContent().size());
    }

    // ====== MARK AS READ ======
    @Test
    void shouldMarkNotificationAsRead(){

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

        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        when(userRepository.findByUserId("U1")).thenReturn(Optional.of(user));

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

        List<NotificationEntity> notifications = new ArrayList<>();

        notifications.add(notification1);
        notifications.add(notification2);

        when(notificationRepository.findByUser_UserId("U1")).thenReturn(notifications);

        MessageResponse response = notificationService.readAll("U1");

        assertTrue(notification1.isRead());
        assertTrue(notification2.isRead());

        assertEquals("Đã đọc toàn bộ thông báo", response.getMessage());

        verify(notificationRepository).saveAll(notifications);
    }
}
