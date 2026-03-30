import auction.model.Auction;
import auction.service.AuctionService;

public class Test {
    public static void main(String[] args) {
        Auction auction = new Auction("Laptop", 1000);

        AuctionService service = new AuctionService(auction);

        service.placeBid("Minh", 1000);
        service.placeBid("Nguyen", 1200);
        service.placeBid("Van", 1500);

        System.out.println(auction);
        
        System.out.println("Bid History: ");
        auction.printBidHistory();
    }
}
