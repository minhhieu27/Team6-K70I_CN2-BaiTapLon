package model;
public class BidTransaction {
    public User user;
    public double price;

    public BidTransaction(User user, double price) {
        this.user = user;
        this.price = price;
    }
}
