package users.enums;

public enum VIPLevel {
    BRONZE(0),
    SILVER(1000),
    GOLD(5000),
    DIAMOND(10000);

    private double requiredDeposit;

    VIPLevel(double requiredDeposit){
        this.requiredDeposit = requiredDeposit;
    }

    public double getRequiredDeposit(){
        return requiredDeposit;
    }
}
