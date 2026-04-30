package auction.model;

public class Bid {
    private final String userId;
    private final Money amount;

    public Bid(String userId, Money amount){
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId(){
        return userId;
    }

    public Money getAmount(){
        return amount;
    }

    
}
