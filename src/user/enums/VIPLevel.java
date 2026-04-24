package user.enums;

public enum VIPLevel {
    BRONZE(0),
    SILVER(1000),
    GOLD(5000),
    DIAMOND(10000);

    private double requiredDeposit;

    VIPLevel(DIAMOND requiredDeposit){
        this.requiredDeposit = requiredDeposit;
    }

    public double getRequiredDeposit(){
        return requiredDeposit;
    }
}
