package multithreading;

/**
 * In this example, for increment count, we use synchronized to maintain the atomic of increment.
 * 
 * If we just put count++ in run method, the result can not guarantee to be 20000. Because 
 * to do count++, it has 3 operations, we need first read count, then count + 1, then write 
 * back. 
 * 
 * Because these three operations can not guarantee atomic, thus there is possibility that 
 * the final result is < 20000.
 *
 */
public class SynchronizedExample {
    private int count =0;
    public synchronized void increment() {
        count++;
    }
    public static void main(String[] args) {
        SynchronizedExample e = new SynchronizedExample();
        e.doWork();
    }
    public void doWork() {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    increment();
                }
            }
        });
        
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    increment();
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
        
        System.out.println("Count is " + count);
    }
}
