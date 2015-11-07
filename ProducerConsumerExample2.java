package multithreading;

import java.util.LinkedList;
import java.util.Random;

public class ProducerConsumerExample2 {
    private LinkedList<Integer> list = new LinkedList<>();
    private final int limit = 10;
    private Object lock = new Object();
    
    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                while (list.size() == limit) {
                    lock.wait();
                }
                list.add(value++);
                lock.notify();
            }
        }
    }

    public void consume() throws InterruptedException {
        Random random = new Random();
        while (true) {
            synchronized (lock) {
                while (list.size() == 0) {
                    lock.wait();
                }
                System.out.println("List size is " + list.size());
                int value = list.removeFirst();
                System.out.println("value is " + value);
                lock.notify(); //notify will wake threads lock on this object. 
            }
            Thread.sleep(random.nextInt(10));
        }
    }
    
    public static void main(String[] args) {
        ProducerConsumerExample2 p = new ProducerConsumerExample2();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
