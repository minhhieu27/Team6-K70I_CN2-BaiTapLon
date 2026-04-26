package users.model.user;

import users.enums.Role;
import users.enums.VIPLevel;
import users.model.wallet.Wallet;

public class User {
    private String id;

    private UserAccount account;
    private UserRole role;
    private UserProfile profile;
    private Wallet wallet;
    private VIPInfo vipInfo;

    public User(String id, UserAccount account){
        this.id = id;
        this.account = account;
        this.role = new UserRole();
        this.profile = new UserProfile();
        this.wallet = new Wallet();
    }

    public UserRole getRole(){
        return role;
    }

    public UserAccount getAccount(){
        return account;
    }

    public Wallet getWallet(){
        return wallet;
    }

    public VIPInfo getVipInfo(){
        return vipInfo;
    }

    public void setVipInfo(VIPInfo vipInfo){
        this.vipInfo = vipInfo;
    }

    public boolean isActive(){
        return account.isActive();
    }

    public boolean isLocked() {
        return !this.account.isActive();
    }

    public String getEmail() {
        return this.account.getEmail();
    }

    public boolean hasRole(Role role) {
        return this.role.hasRole(role);
    }

    public boolean hasAnyRole(Role... role){
        return this.role.hasAnyRole(role);
    }

    public void setVIPLevel(VIPLevel level) {
        this.vipInfo.setVIPLevel(level);
    }
}
    