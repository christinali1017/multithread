
class Producer implements Runnable {
    private final MyBlockingQueue<Integer> queue;
    Producer(final MyBlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            int i = 0;
            while (true) {
                System.out.println("Produce product " + i);
                queue.insert(i++);
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

class Consumer implements Runnable {
    private final MyBlockingQueue<Integer> queue;
    Consumer(final MyBlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            while (true) {
                int res = queue.remove();
                System.out.println("Consume product " + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
public class ProducerConsumerExample {
    public static void main(String[] args) {
        MyBlockingQueue<Integer> queue = new MyBlockingQueue<>();
        queue.init(3);
        Producer p = new Producer(queue);
        Consumer c1 = new Consumer(queue);
        Consumer c2 = new Consumer(queue);
        new Thread(p).start();
        new Thread(c1).start();
        new Thread(c2).start();
        
    }
}
