package auction.users;

import org.junit.jupiter.api.Test;

import com.app.common.enums.Role;
import com.app.common.enums.VIPLevel;
import com.app.common.money.Money;
import com.app.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {

    // ====== CREATE USER ======
    @Test
    void shouldCreateUser(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        assertNotNull(user.getUserId());
        assertEquals("hieu", user.getUsername());
        assertEquals("abc123@gmail.com", user.getUserProfile().getEmail());

        assertTrue(user.isActive());

        assertEquals(VIPLevel.NORMAL, user.getVipLevel());
    }

    // ====== DEFAULT ROLE ======
    @Test
    void shouldHaveDefaultUserRole(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        assertTrue(user.hasRole(Role.ROLE_USER));
    }

    // ====== ADD ROLE ======
    @Test
    void shouldAddSellerRole(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.addRole(Role.ROLE_SELLER);

        assertTrue(user.hasRole(Role.ROLE_SELLER));
    }

    // ====== REMOVE ROLE ======
    @Test
    void shouldRemoveRole(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.addRole(Role.ROLE_SELLER);
        user.removeRole(Role.ROLE_SELLER);

        assertFalse(user.hasRole(Role.ROLE_SELLER));
    }

    // ====== LOCK ACCOUNT ======
    @Test
    void shouldLockAccount(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.lockAccount();

        assertFalse(user.isActive());
    }

    // ====== UNLOCK ACCOUNT ======
    @Test
    void shouldUnlockAccount(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.lockAccount();
        user.unlockAccount();

        assertTrue(user.isActive());
    }

    // ====== HAS ANY ROLE ======
    @Test
    void shouldReturnTrueWhenHasAnyRole(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.addRole(Role.ROLE_SELLER);
        user.addRole(Role.ROLE_ADMIN);

        assertTrue(user.hasAnyRole(Role.ROLE_ADMIN, Role.ROLE_SELLER));
    }

    // ====== BECOME SELLER ======
    @Test
    void shouldBecomeSeller(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.becomeSeller();

        assertTrue(user.hasRole(Role.ROLE_SELLER));
    }

    // ====== UPGRADE BRONZE LEVEL ======
    @Test
    void shouldUpgradeToBronzeLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.getWallet().deposit(new Money(2000));
        user.getWallet().lock(new Money(1000));
        user.getWallet().consumeLocked(new Money(1000));

        user.upgradeVIP();

        assertEquals(VIPLevel.BRONZE, user.getVipLevel());
    }

    // ====== UPGRADE SILVER LEVEL ======
    @Test
    void shouldUpgradeToSilverLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.getWallet().deposit(new Money(6000));
        user.getWallet().lock(new Money(5000));
        user.getWallet().consumeLocked(new Money(5000));

        user.upgradeVIP();

        assertEquals(VIPLevel.SILVER, user.getVipLevel());
    }

    // ====== UPGRADE GOLD LEVEL ======
    @Test
    void shouldUpgradeToGoldLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.getWallet().deposit(new Money(12000));
        user.getWallet().lock(new Money(10000));
        user.getWallet().consumeLocked(new Money(10000));

        user.upgradeVIP();

        assertEquals(VIPLevel.GOLD, user.getVipLevel());
    }

    // ====== UPGRADE DIAMOND LEVEL ======
    @Test
    void shouldUpgradeToDiamondLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.getWallet().deposit(new Money(60000));
        user.getWallet().lock(new Money(50000));
        user.getWallet().consumeLocked(new Money(50000));

        user.upgradeVIP();

        assertEquals(VIPLevel.DIAMOND, user.getVipLevel());
    }
}