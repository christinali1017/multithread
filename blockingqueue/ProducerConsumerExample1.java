import java.util.ArrayList;
import java.util.List;


class Producer implements Runnable {
    private final MyBlockingQueueSuportOfferList<Integer> queue;
    Producer(final MyBlockingQueueSuportOfferList<Integer> queue) {
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
                List<Integer> list = new ArrayList<>();
                list.add(10);
                list.add(11);
                queue.insertList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

class Consumer implements Runnable {
    private final MyBlockingQueueSuportOfferList<Integer> queue;
    Consumer(final MyBlockingQueueSuportOfferList<Integer> queue) {
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
        MyBlockingQueueSuportOfferList<Integer> queue = new MyBlockingQueueSuportOfferList<>();
        queue.init(3);
        Producer p = new Producer(queue);
        Consumer c1 = new Consumer(queue);
        Consumer c2 = new Consumer(queue);
        new Thread(p).start();
        new Thread(c1).start();
        new Thread(c2).start();
        
    }
}
