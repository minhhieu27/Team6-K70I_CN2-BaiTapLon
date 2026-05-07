package auction.model;

import org.junit.jupiter.api.Test;

import com.app.domain.exception.BusinessError.InvalidBidException;
import com.app.domain.exception.base.AppException;
import com.app.domain.model.Auction;
import com.app.domain.model.Bid;
import com.app.domain.model.Money;
import com.app.domain.service.AuctionService;
import com.app.domain.strategy.PercentBidStrategy;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {
    
    @Test
    void shouldAcceptFirstBid() throws AppException{
        Auction auction = new Auction("Laptop","AUC-999", new Money(50), new PercentBidStrategy(1.1));

        AuctionService auctionService = new AuctionService(null);
        Bid bid = new Bid("A00", new Money(70));

        auctionService.placeBid(auction, bid);
        assertEquals(70, auction.getCurrentPrice().getAmount().doubleValue());
    }

    @Test
    void shouldAcceptHigherBid() throws AppException{
        Auction auction = new Auction("Iphone 15","AUC-666", new Money(70), new PercentBidStrategy(1.3));

        AuctionService auctionService = new AuctionService(null);

        Bid bid1 = new Bid("A01", new Money(100));
        Bid bid2 = new Bid("A02", new Money(130));

        auctionService.placeBid(auction, bid1);
        auctionService.placeBid(auction, bid2);

        assertEquals(130, auction.getCurrentPrice().getAmount().doubleValue());
    }

    @Test
    void shouldRejectEqualBid() throws AppException{
        Auction auction = new Auction("Buggatti","AUC-888", new Money(100000), new PercentBidStrategy(1.5));

        Bid bid = new Bid("B05", new Money(200000));
        AuctionService auctionService = new AuctionService(null);

        auctionService.placeBid(auction, bid);

        assertThrows(InvalidBidException
            .class, ()-> auctionService.placeBid(auction, new Bid("C05", new Money(200000))));
    }

    @Test
    void shouldRejectLowerBid() throws AppException{
        Auction auction = new Auction("Warm-up Barcelona shirt","AUC-456", new Money(250), new PercentBidStrategy(1.25));

        Bid bid = new Bid("D01", new Money(320));
        AuctionService auctionService = new AuctionService(null);
        auctionService.placeBid(auction, bid);

        assertThrows(InvalidBidException.class, ()-> auctionService.placeBid(auction, new Bid("E01", new Money(200))));
    }

    @Test
    void shouldRejectNegativeBid() throws AppException{
        Auction auction = new Auction("Key-Board","AUC-778", new Money(125), new PercentBidStrategy(1.15));
        AuctionService auctionService = new AuctionService(null);

        assertThrows(IllegalArgumentException.class, ()-> auctionService.placeBid(auction, new Bid("D05", new Money(-10))));
    }
}
