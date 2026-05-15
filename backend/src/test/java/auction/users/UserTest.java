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
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        assertNotNull(user.getUserId());
        assertEquals("abc", user.getUsername());
        assertEquals("123@gmail.com", user.getProfile().getEmail());

        assertTrue(user.isActive());

        assertEquals(VIPLevel.NORMAL, user.getVipLevel());
    }

    // ====== DEFAULT ROLE ======
    @Test
    void shouldHaveDefaultUserRole(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        assertTrue(user.hasRole(Role.ROLE_USER));
    }

    // ====== ADD ROLE ======
    @Test
    void shouldAddSellerRole(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.addRole(Role.ROLE_SELLER);

        assertTrue(user.hasRole(Role.ROLE_SELLER));
    }

    // ====== REMOVE ROLE ======
    @Test
    void shouldRemoveRole(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.addRole(Role.ROLE_SELLER);
        user.removeRole(Role.ROLE_SELLER);

        assertFalse(user.hasRole(Role.ROLE_SELLER));
    }

    // ====== LOCK ACCOUNT ======
    @Test
    void shouldLockAccount(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.lockAccount();

        assertFalse(user.isActive());
    }

    // ====== UNLOCK ACCOUNT ======
    @Test
    void shouldUnlockAccount(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.lockAccount();
        user.unlockAccount();

        assertTrue(user.isActive());
    }

    // ====== HAS ANY ROLE ======
    @Test
    void shouldReturnTrueWhenHasAnyRole(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.addRole(Role.ROLE_SELLER);
        user.addRole(Role.ROLE_ADMIN);

        assertTrue(user.hasAnyRole(Role.ROLE_ADMIN, Role.ROLE_SELLER));
    }

    // ====== BECOME SELLER ======
    @Test
    void shouldBecomeSeller(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.becomeSeller();

        assertTrue(user.hasRole(Role.ROLE_SELLER));
    }

    // ====== UPGRADE BRONZE LEVEL ======
    @Test
    void shouldUpgradeToBronzeLevel(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.getWallet().deposit(new Money(2000));
        user.getWallet().withdraw(new Money(1000));

        user.upgradeVIP();

        assertEquals(VIPLevel.BRONZE, user.getVipLevel());
    }

    // ====== UPGRADE SILVER LEVEL ======
    @Test
    void shouldUpgradeToSilverLevel(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.getWallet().deposit(new Money(6000));
        user.getWallet().withdraw(new Money(5000));

        user.upgradeVIP();

        assertEquals(VIPLevel.SILVER, user.getVipLevel());
    }

    // ====== UPGRADE GOLD LEVEL ======
    @Test
    void shouldUpgradeToGoldLevel(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.getWallet().deposit(new Money(12000));
        user.getWallet().withdraw(new Money(10000));

        user.upgradeVIP();

        assertEquals(VIPLevel.GOLD, user.getVipLevel());
    }

    // ====== UPGRADE DIAMOND LEVEL ======
    @Test
    void shouldUpgradeToDiamondLevel(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");

        user.getWallet().deposit(new Money(60000));
        user.getWallet().withdraw(new Money(50000));

        user.upgradeVIP();

        assertEquals(VIPLevel.DIAMOND, user.getVipLevel());
    }
}