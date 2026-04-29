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
        this.vipInfo = new VIPInfo();
    }
    
    // ====== ACCOUNT ======
    public String getEmail() {
        return account.getEmail();
    }

    public String getId(){   
        return id;
    }
    
    public boolean isLocked() {
        return !account.isActive();
    }

    public UserAccount getAccount(){
        return account;
    }

    public UserProfile getProfile(){
        return profile;
    }

    // ====== ROLE ======
    public UserRole getRole(){
        return role;
    }

    public void addRole(Role role){
        this.role.addRole(role);
    }

    public void removeRole(Role role){
        this.role.removeRole(role);
    }
    
    public boolean hasRole(Role role) {
        return this.role.hasRole(role);
    }

    // ====== WALLET ======
    public Wallet getWallet(){
        return wallet;
    }

    // ====== VIP ======
    public void upgrade(VIPLevel newLevel){
        vipInfo.upgrade(newLevel);
    }
    
    public VIPInfo getVipInfo(){
        return vipInfo;
    }

    public VIPLevel getVIPLevel(){
        return vipInfo.getLevel();
    }

    public void setVIPLevel(VIPLevel level) {
        this.vipInfo.setVIPLevel(level);
    }
}
    