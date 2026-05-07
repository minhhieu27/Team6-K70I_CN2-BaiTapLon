package com.app.domain.user;

import com.app.domain.enums.Role;
import com.app.domain.enums.VIPLevel;
import com.app.domain.observer.Observer;
import com.app.domain.tool.IDGenerator;
import com.app.domain.wallet.Wallet;

public class User implements Observer {
    private final String id;

    private UserAccount account;
    private UserRole role;
    private UserProfile profile;
    private Wallet wallet;
    private VIPInfo vipInfo;
    
    public User(String id, UserAccount account){
        this.id = IDGenerator.generateUserId();
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

    // ====== Observer ======
    @Override
    public void update(String msg){
        System.out.println();
    }
}
    