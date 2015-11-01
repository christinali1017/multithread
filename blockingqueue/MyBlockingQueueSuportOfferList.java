import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple blocking queue support insert and remove element and insert list of element.
 * When offer list of element, the list should be put atomically.
 * @param <E> Element type.
 */

public class MyBlockingQueueSuportOfferList<E> {
    private int capacity;

    private Queue<E> queue;

    private ReentrantLock lock = new ReentrantLock();

    private ReentrantLock offerLock = new ReentrantLock();

    private Condition notFull = this.lock.newCondition();

    private Condition notEmpty = this.lock.newCondition();

    public void init(final int capacity) {
        this.lock.lock();
        try {
            if (this.queue == null) {
                this.queue = new LinkedList<>();
                this.capacity = capacity;
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void insert(E obj) throws Exception {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (this.capacity == this.queue.size()) {
                this.notFull.await();
            }
            this.queue.add(obj);
            this.notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public E remove() throws Exception {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (queue.size() == 0) {
                this.notEmpty.await();
            }
            E result = this.queue.poll();
            this.notFull.signal();
            return result;
        } finally {
            lock.unlock();
        }
    }

    public void insertList(List<E> objs) throws Exception {
        this.offerLock.lock();
        this.lock.lock();
        try{
            for(E obj : objs){
                while(this.queue.size() == this.capacity) {
                    this.notFull.await();
                }
                this.queue.add(obj);
                this.notEmpty.signal();
            }
        }finally{
            this.lock.unlock();
            this.offerLock.unlock();
        }
    }
}
