package auction.auction;

import auction.exception.base.AppException;
import auction.model.Auction;
import auction.model.Bid;
import auction.model.Money;
import auction.service.AuctionService;
import auction.strategy.PercentBidStrategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionConcurency {
    @Test
    void shouldHandleConcurrentBids() throws AppException, InterruptedException{
        Auction auction = new Auction("Airpods", new Money(100), new PercentBidStrategy(1.05));
        AuctionService auctionService = new AuctionService(null);

        Thread t1 = new Thread(()-> {
            try {
                auctionService.placeBid(auction, new Bid("A09", new Money(150)));

            } catch (AppException e){
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(()-> {
            try {
                auctionService.placeBid(auction, new Bid("E85", new Money(200)));

            } catch (AppException e){
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        Money highest = auction.getCurrentPrice();

        assertTrue(highest.getAmount().doubleValue() == 150 || highest.getAmount().doubleValue() == 200);
    }    
}
