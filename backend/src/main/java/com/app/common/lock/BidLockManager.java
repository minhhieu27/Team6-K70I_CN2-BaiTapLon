package com.app.common.lock;

import java.util.concurrent.locks.ReentrantLock;

import com.app.exception.auction.BidConflictException;

public class BidLockManager {
    
    private final ReentrantLock lock = new ReentrantLock();

    public void executeWithLock(Runnable task) throws BidConflictException {

        try {
            lock.lock();
            task.run();

        } catch (Exception e) {
            throw new BidConflictException("Error");

        } finally {
            lock.unlock();
        }
    }
}
