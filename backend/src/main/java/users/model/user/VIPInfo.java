package users.model.user;

import users.enums.VIPLevel;

public class VIPInfo {
    private VIPLevel level;

    public VIPInfo(){
        this.level = VIPLevel.NORMAL;
    }

    public VIPLevel getLevel(){
        return level;
    }

    public void setVIPLevel(VIPLevel level){
        this.level = level;
    }

    public void upgrade(VIPLevel newLevel){
        if (newLevel.ordinal() > level.ordinal()){
            level = newLevel;
        }
    }
}
