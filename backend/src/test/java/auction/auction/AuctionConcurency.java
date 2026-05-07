package auction.auction;

import org.junit.jupiter.api.Test;

import com.app.domain.exception.base.AppException;
import com.app.domain.model.Auction;
import com.app.domain.model.Bid;
import com.app.domain.model.Money;
import com.app.domain.service.AuctionService;
import com.app.domain.strategy.PercentBidStrategy;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionConcurency {
    @Test
    void shouldHandleConcurrentBids() throws AppException, InterruptedException{
        Auction auction = new Auction("Airpods","AUC123", new Money(100), new PercentBidStrategy(1.05));
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
