package multithreading;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerAndConsumer {
    private BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

    private void producer() throws InterruptedException {
        Random random = new Random();
        while (true) {
            //put will wait until queue is not full
            queue.put(random.nextInt(100));
        }
    }

    private void consumer() throws InterruptedException {
        Random random = new Random();
        while (true) {
            Thread.sleep(100);
            if (random.nextInt(10) == 0) {
                //take will wait until queue is not empty.
                Integer value = queue.take();
                System.out.println("Taken value: " + value + ", queue size is: " + queue.size());
            }
        }
    }
    
    public static void main(String[] args) {
        ProducerAndConsumer p = new ProducerAndConsumer();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p.producer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p.consumer();
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
