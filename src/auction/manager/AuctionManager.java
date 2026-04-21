package auction.manager;

import auction.exception.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import auction.model.*;
import auction.observer.Observer;
import auction.service.AuctionService;
import auction.strategy.PercentBidStrategy;;

public class AuctionManager {
    
   private final AuctionService service = new AuctionService(new PercentBidStrategy(1.1));
   private final List<Observer> observers = new ArrayList<>();
   private final ReentrantLock lock = new ReentrantLock();

    public void addObserver(Observer o){
        observers.add(o);
    }

    public void removeObserver(Observer o){
        observers.remove(o);
    }

    private void notifyObservers(String msg){
        for (Observer o : observers){
            o.update(msg);
        }
    }

    public void placeBid(Auction auction, Bid bid) throws InvalidBidException, AuctionClosedException{
        lock.lock();
        try{
            service.placeBid(auction, bid);
            notifyObservers("New bid: " + bid.getAmount() + " by " + bid.getBidder());
        } finally {
            lock.unlock();
        }
    }
}
