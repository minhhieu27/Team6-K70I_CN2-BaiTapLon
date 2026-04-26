package auction.service;

import java.util.concurrent.locks.ReentrantLock;

import auction.exception.ConcurrencyException;

public class BidLockManager {
    
    private final ReentrantLock lock = new ReentrantLock();

    public void executeWithLock(Runnable task) throws ConcurrencyException {

        try {
            lock.lock();
            task.run();

        } catch (Exception e) {
            throw new ConcurrencyException("Concurrent error");

        } finally {
            lock.unlock();
        }
    }
}
