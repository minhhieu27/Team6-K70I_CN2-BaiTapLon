package user.model;

import user.enums.Role;

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
}
    