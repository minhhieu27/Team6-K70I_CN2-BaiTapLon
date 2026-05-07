package com.app.domain.manager;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import com.app.domain.exception.base.AppException;
import com.app.domain.model.*;
import com.app.domain.observer.Observer;
import com.app.domain.service.AuctionService;
import com.app.domain.tool.FormatUtil;;

public class AuctionManager {
    
    private final AuctionService service;
    private final List<Observer> observers = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public AuctionManager(AuctionService service){
        this.service = service;
    }

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

    public void placeBid(Auction auction, Bid bid) throws AppException {
        
        lock.lock();
        try{
            service.placeBid(auction, bid);
            notifyObservers(FormatUtil.formatBid(bid));
        } finally {
            lock.unlock();
        }
    }
}
