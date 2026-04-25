package user.model.user;

import user.enums.VIPLevel;

public class VIPInfo {
    private VIPLevel level;

    public VIPInfo(){
        this.level = VIPLevel.BRONZE;
    }

    public VIPLevel getLevel(){
        return level;
    }

    public void upgrade(VIPLevel newLevel){
        if (newLevel.ordinal() > level.ordinal()){
            level = newLevel;
        }
    }
}
