package auction.users;

import org.junit.jupiter.api.Test;

import com.app.common.enums.Role;
import com.app.common.enums.UserStatus;
import com.app.common.enums.VIPLevel;
import com.app.common.money.Money;
import com.app.entity.user.UserEntity;

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

        assertEquals(UserStatus.ACTIVE, user.getStatus());

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

        assertEquals(UserStatus.LOCKED, user.getStatus());
    }

    // ====== UNLOCK ACCOUNT ======
    @Test
    void shouldUnlockAccount(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.lockAccount();
        user.unlockAccount();

        assertEquals(UserStatus.ACTIVE, user.getStatus());
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

        user.setVipLevel(VIPLevel.NORMAL);

        user.getWallet().deposit(new Money(10000000));

        Money originalAmount = new Money(7000000);

        user.getWallet().lock(originalAmount);

        int discountPercent = user.getVipLevel().getDiscountPercent();

        Money discount = originalAmount.percentage(discountPercent);

        Money finalAmount = originalAmount.subtract(discount);

        user.getWallet().consumeLocked(finalAmount);

        user.getWallet().addSpent(finalAmount);

        user.upgradeVIP();

        assertEquals(VIPLevel.BRONZE, user.getVipLevel());
    }

    // ====== UPGRADE SILVER LEVEL ======
    @Test
    void shouldUpgradeToSilverLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

       user.setVipLevel(VIPLevel.BRONZE);

        user.getWallet().deposit(new Money(100000000));

        Money originalAmount = new Money(70000000);

        user.getWallet().lock(originalAmount);

        int discountPercent = user.getVipLevel().getDiscountPercent();

        Money discount = originalAmount.percentage(discountPercent);

        Money finalAmount = originalAmount.subtract(discount);

        user.getWallet().consumeLocked(finalAmount);

        user.getWallet().addSpent(finalAmount);

        user.upgradeVIP();

        assertEquals(VIPLevel.SILVER, user.getVipLevel());
    }

    // ====== UPGRADE GOLD LEVEL ======
    @Test
    void shouldUpgradeToGoldLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.setVipLevel(VIPLevel.SILVER);

        user.getWallet().deposit(new Money(700000000));

        Money originalAmount = new Money(650000000);

        user.getWallet().lock(originalAmount);

        int discountPercent = user.getVipLevel().getDiscountPercent();

        Money discount = originalAmount.percentage(discountPercent);

        Money finalAmount = originalAmount.subtract(discount);

        user.getWallet().consumeLocked(finalAmount);

        user.getWallet().addSpent(finalAmount);

        user.upgradeVIP();

        assertEquals(VIPLevel.GOLD, user.getVipLevel());
    }

    // ====== UPGRADE DIAMOND LEVEL ======
    @Test
    void shouldUpgradeToDiamondLevel(){
        // User
        UserEntity user = new UserEntity("hieu", "abc123@gmail.com", "0123456788", "12345678");

        user.setVipLevel(VIPLevel.GOLD);

        user.getWallet().deposit(new Money(10000000000L));

        Money originalAmount = new Money(7000000000L);

        user.getWallet().lock(originalAmount);

        int discountPercent = user.getVipLevel().getDiscountPercent();

        Money discount = originalAmount.percentage(discountPercent);

        Money finalAmount = originalAmount.subtract(discount);

        user.getWallet().consumeLocked(finalAmount);

        user.getWallet().addSpent(finalAmount);

        user.upgradeVIP();

        assertEquals(VIPLevel.DIAMOND, user.getVipLevel());
    }
}