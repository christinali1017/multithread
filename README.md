

###Basic
---

Examples:

- Computer games are the best examples for applying multithreading.
- Download files. Suppose we have several files to download. 


Process and threads:

- A office word opened on your computer can be considered as a process.A error check can be considered as a thread.
- Even when you do not create threads there is a main thread execute the program
- Os schedules the threads to be processed by the process.


Creating threads:

```java
Thread t = new Thread();
```

At this point, you just creates a thread object. You need to call 

```java
t.start();
```

But there is no guarantee that the thread willl start immediately when start is invoked.

At this point, java.lang.Thread has no idea about what we want to do. **Thus we need to extends Thread and override the Run method**. Or **implements Runnable interface.**

Example:

Extends Thread class:


```java
class Downloader extends Thread {
    private String url;
    public Downloader(String url) {
        this.url = url;
    }
    
    public void run() {
        FileDowndloader fd = new FileDownloader();
        fd.download(this.url);
    }
}
```

Implement Runnable:

```java
Class Downloader implements Runnanle {
    private String url;
    public Downloader(String url) {
        this.url = url;
    }
    
    public void run() {
        FileDowndloader fd = new FileDownloader();
        fd.download(this.url);
    }
}


//When use it:
Downloader d = new Downloader();
Thread t = new Thread(d);
t.start();
```

###Thread constructor
---

You can specified Runnable target, thread name, thread group, etc.

- Thread()

- Thread(Runnable target)

- Thread(Runnable target, String name)

- Thread(String name)

- Thread(ThreadGroup group, Runnable target)

- Thread(ThreadGroup group, Runnable target, String name)

- Thread(ThreadGroup group, Runnable target, String name, long stackSize)

- Thread(ThreadGroup group, String name)


###Thread states
---

- **New**  Thread t = new Thread()
- **Runnable**   t.start()
- **Running**   the thread is actually executeing the code.
- **Sleep / waiting / blocked**
- **Dead**   cannot start again on this object once it is dead.


###Thread priority
---

- All threads in java carry normal priority unless specified.
- Priority specified 1 to 10
- Priority should be set before start is called

**Yield**:

Yield method tells the currently running thread to give chance to other threads with equal priority in the thread pool. 


**Join**:

Waits for this thread to die.

So t1.join() is called to wait for the t1 thread to finish. Then t2.join() is called to wait for the t2 thread to finish. The 2 threads have been running in parallel but the thread that started them (probably the main thread) needs to wait for them to finish before continuing. That's a common pattern. When the main thread calls t1.join() it will stop running and wait for the t1 thread to finish.


**Wait**: wait (and notify) must happen in a block synchronized on the monitor object. **wait will release the lock**.

http://stackoverflow.com/questions/15956231/what-does-this-thread-join-code-mean


###Thread safety
---

- Remember to keep the instance variables marked as private in multithread environment to avoid manipulating them by threads.
- Unnecessary code synchronization will affect the application's performance.

Simple example of **Synchronized**

```java
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

```

In the above method, **if we add synchronized on a method, then it synchronized on "this" class object.**


**Multi lock example**:

In the following example, we have two threads to add element to two list. If we add synchronized to stageOne and stageTwo method, it's thread safe. But it takes around 5 seconds to finish adding. 

Because we add synchronized on the two methods. Thus the two method can not be executed on the same time. Because they both have lock on "class object". 

```java
package multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultilockExample {
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    private Random random = new Random();
    
    public synchronized void stageOne() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list1.add(random.nextInt(100));
    }

    public synchronized void stageTwo() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list2.add(random.nextInt(100));
    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public static void main(String[] args) {
        MultilockExample m = new MultilockExample();
        System.out.println("Starting...");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                m.process();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                m.process();
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
        long end = System.currentTimeMillis();
        System.out.println("Time take " + (end - start));
        System.out.println("List 1: " + m.list1.size() + "; list 2 : " + m.list2.size());
    }
}
```

Run result:

```
Starting...
Time take 5417
List 1: 2000; list 2 : 2000```

How to make it faster? What we can do to execute the two method at the same time? 

We can use synchronized block. See the following example:

```java
package multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultilockExample {
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    private Random random = new Random();
    
    public void stageOne() {
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            list1.add(random.nextInt(100));
        }
    }

    public void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    
            list2.add(random.nextInt(100));
        }
    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public static void main(String[] args) {
        MultilockExample m = new MultilockExample();
        System.out.println("Starting...");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                m.process();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                m.process();
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
        long end = System.currentTimeMillis();
        System.out.println("Time take " + (end - start));
        System.out.println("List 1: " + m.list1.size() + "; list 2 : " + m.list2.size());
    }
}

```

This time it takes around 2 seconds to run the result. 

```
Starting...
Time take 2728
List 1: 2000; list 2 : 2000
```

###Thread pool
---

Use Executors.newFixedThreadPool(num) to create thread pool.

See the following example:

```java
package multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Processor implements Runnable{

    private int id;

    public Processor(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Starting: " + id);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        System.out.println("Completed: " + id);
    }

}

public class ThreadPool {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 5; i++) {
            executor.submit(new Processor(i));
        }
        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All tasks submitted");
    }
}


```


###CountDownLatch
---

A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
A CountDownLatch is initialized with a given count. **The await methods block until the current count reaches zero due to invocations of the countDown() method, after which all waiting threads are released and any subsequent invocations of await return immediately**. This is a one-shot phenomenon -- the count cannot be reset. If you need a version that resets the count, consider using a CyclicBarrier.

A CountDownLatch is a versatile synchronization tool and can be used for a number of purposes. A CountDownLatch initialized with a count of one serves as a simple on/off latch, or gate: all threads invoking await wait at the gate until it is opened by a thread invoking countDown(). A CountDownLatch initialized to N can be used to make one thread wait until N threads have completed some action, or some action has been completed N times.

A useful property of a CountDownLatch is that it doesn't require that threads calling countDown wait for the count to reach zero before proceeding, it simply prevents any thread from proceeding past an await until all threads could pass.

Example:


```java
package multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Process implements Runnable {
    private CountDownLatch latch;

    public Process(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println("Started.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch.countDown();
    }
    
}

public class CountDownLatchExample {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executor.submit(new Process(latch));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Completed");
        executor.shutdown();
    }
}

```

Result:

```java
Started.
Started.
Started.
Completed
```

It will sleep 3 seconds, then countDown, then completed is printed.


###Producer and Consumer
---

The below example use java BlockingQueue libary.

```java
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

```

Example of producer and consumer use wait and nofity:

```java
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

```


###ReentrantLock
---
A reentrant **mutual exclusion Lock** with the same basic behavior and semantics as the implicit monitor lock accessed using synchronized methods and statements, but with extended capabilities.

A ReentrantLock is owned by the thread last successfully locking, but not yet unlocking it. A thread invoking lock will return, successfully acquiring the lock, when the lock is not owned by another thread. **The method will return immediately if the current thread already owns the lock. This can be checked using methods isHeldByCurrentThread(), and getHoldCount()**.

**The constructor for this class accepts an optional fairness parameter. When set true, under contention, locks favor granting access to the longest-waiting thread.**

**It is recommended practice to always immediately follow a call to lock with a try block, most typically in a before/after construction such as:
** Otherwise, if there is an exception, the lock might not successfully unlocked.

```java
 class X {
   private final ReentrantLock lock = new ReentrantLock();
   // ...

   public void m() {
     lock.lock();  // block until condition holds
     try {
       // ... method body
     } finally {
       lock.unlock()
     }
   }
 }
 ```
 
 

Example:
```java
package multithreading;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {

        private int count = 0;
        private Lock lock = new ReentrantLock();
        private Condition cond = lock.newCondition();

        private void increment() {
            for (int i = 0; i < 10000; i++) {
                count++;
            }
        }

        public void firstThread() throws InterruptedException {
            lock.lock();
            
            System.out.println("Waiting ....");
            cond.await();
            
            System.out.println("Woken up!");

            try {
                increment();
            } finally {
                lock.unlock();
            }
        }

        public void secondThread() throws InterruptedException {
            
            Thread.sleep(1000);
            lock.lock();
            
            System.out.println("Press the return key!");
            new Scanner(System.in).nextLine();
            System.out.println("Got return key!");
            
            cond.signal();

            try {
                increment();
            } finally {
                lock.unlock();
            }
        }

        public void finished() {
            System.out.println("Count is: " + count);
        }
        public static void main(String[] args) {
            ReentrantLockExample p = new ReentrantLockExample();
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        p.firstThread();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
            });

            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        p.secondThread();
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
                p.finished();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
```

Result:

```java
Waiting ....
Press the return key!

Got return key!
Woken up!
Count is: 20000
```

###DeadLock and how to avoid
---
**Deadlock** is a situation or condition when two or more processes are holding some resources and trying to acquire some more resources, and they can not release the resources until they finish there execution.

**Necessary conditions**:
- **Mutual Exclusion**: There is s resource that cannot be shared.
- **Hold and Wait**: A process is holding at least one resource and waiting for another resource which is with some other process.
- **No Preemption**: The operating system is not allowed to take a resource back from a process until process gives it back.
- **Circular Wait**:  A set of processes are waiting for each other in circular form.


```java
package multithreading;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockExample {

    private Account acc1 = new Account();
    private Account acc2 = new Account();

    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();

    public void firstThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            lock1.lock();
            lock2.lock();
            try {
                Account.transfer(acc2, acc1, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void secondThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            lock2.lock();
            lock1.lock();
            try {
                Account.transfer(acc2, acc1, random.nextInt(100));
            } finally {
                lock2.unlock();
                lock1.unlock();
            }
        }
    }

    public void finished() {
        System.out.println("Account 1 balance: " + acc1.getBalance());
        System.out.println("Account 2 balance: " + acc2.getBalance());
        System.out.println("Total balance: "
                + (acc1.getBalance() + acc2.getBalance()));
    }

    public static void main(String[] args) throws Exception {
        
        final DeadLockExample deadlock = new DeadLockExample();
        
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    deadlock.firstThread();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    deadlock.secondThread();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        deadlock.finished();
    }
}

```

Account class:

```java
class Account {
    private int balance = 10000;

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }

    public static void transfer(Account acc1, Account acc2, int amount) {
        acc1.withdraw(amount);
        acc2.deposit(amount);
    }
}
```


**How to avoid deadlock**:

Soltion : Use trylock of ReentrantLock

When use reentrantlock, it returns immediately if the current thread already owns the lock.

**Here we use tryLock to check if current thread has aquired the lock.**

Example code of aquiring two locks and avoid deadlock use tryLock.

```java
    private void acquireLocks(Lock firstLock, Lock secondLock) throws InterruptedException {
        while(true) {
            // Acquire locks
            
            boolean gotFirstLock = false;
            boolean gotSecondLock = false;
            
            try {
                gotFirstLock = firstLock.tryLock();
                gotSecondLock = secondLock.tryLock();
            }
            finally {
                if(gotFirstLock && gotSecondLock) {
                    return;
                }
                
                if(gotFirstLock) {
                    firstLock.unlock();
                }
                
                if(gotSecondLock) {
                    secondLock.unlock();
                }
            }
            
            // Locks not acquired
            Thread.sleep(1);
        }
    }
```


Whole example code:

```java
package multithreading;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockAvoidDeadLock {

    private Account acc1 = new Account();
    private Account acc2 = new Account();

    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();
    
    private void acquireLocks(Lock firstLock, Lock secondLock) throws InterruptedException {
        while(true) {
            // Acquire locks
            
            boolean gotFirstLock = false;
            boolean gotSecondLock = false;
            
            try {
                gotFirstLock = firstLock.tryLock();
                gotSecondLock = secondLock.tryLock();
            }
            finally {
                if(gotFirstLock && gotSecondLock) {
                    return;
                }
                
                if(gotFirstLock) {
                    firstLock.unlock();
                }
                
                if(gotSecondLock) {
                    secondLock.unlock();
                }
            }
            
            // Locks not acquired
            Thread.sleep(1);
        }
    }

    public void firstThread() throws InterruptedException {

        Random random = new Random();

        for (int i = 0; i < 10000; i++) {

            acquireLocks(lock1, lock2);

            try {
                Account.transfer(acc1, acc2, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void secondThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            
            acquireLocks(lock2, lock1);

            try {
                Account.transfer(acc2, acc1, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void finished() {
        System.out.println("Account 1 balance: " + acc1.getBalance());
        System.out.println("Account 2 balance: " + acc2.getBalance());
        System.out.println("Total balance: "
                + (acc1.getBalance() + acc2.getBalance()));
    }
    public static void main(String[] args) {
        TryLockAvoidDeadLock p = new TryLockAvoidDeadLock();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p.firstThread();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p.secondThread();
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
            p.finished();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```


###Semaphore
---

**a semaphore maintains a set of permits. Each acquire() blocks if necessary until a permit is available, and then takes it**. Each release() adds a permit, potentially releasing a blocking acquirer. However, no actual permit objects are used; the Semaphore just keeps a count of the number available and acts accordingly.

**Semaphores are often used to restrict the number of threads than can access some (physical or logical) resource**. For example, here is a class that uses a semaphore to control access to a pool of items:

Example:

```java
package multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class Connection {
    private int connections = 0;
    private Semaphore sem = new Semaphore(10, true);
    private static Connection instance = new Connection();

    private void Connection() {
    }

    public static Connection getInstance() {
        return instance;
    }
    public void connect() {
        try {
            sem.acquire();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        try {
            doConnect();
        } finally {
            
            sem.release();
        }
    }


    public void doConnect() {

        synchronized (this) {
            connections++;
            System.out.println("Current connections: " + connections);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        synchronized (this) {
            connections--;
        }

    }
}

public class SemaphoreExample {

   public static void main(String[] args) throws Exception {
        
        ExecutorService executor = Executors.newCachedThreadPool();
        
        for(int i=0; i < 200; i++) {
            executor.submit(new Runnable() {
                public void run() {
                    Connection.getInstance().connect();
                }
            });
        }
        
        executor.shutdown();
        
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
}


```

###Callable and Future
---

A Future represents the result of an asynchronous computation.** Methods are provided to check if the computation is complete, to wait for its completion, and to retrieve the result of the computation.** 

The result can only be retrieved using method **get when the computation has completed, blocking if necessary until it is ready**. Cancellation is performed by the cancel method.

Additional methods are provided to determine if the task completed normally or was **cancelled**. Once a computation has completed, the computation cannot be cancelled. If you would like to use a Future for the sake of cancellability but not provide a usable result, you can declare types of the form Future<?> and return null as a result of the underlying task.


Example:


```java
package multithreading;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        Future<Integer> future = executor.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                Random random = new Random();
                int duration = random.nextInt(4000);
                
                if (duration > 2000) {
                    throw new IOException("Sleeping for too long.");
                }
                
                System.out.println("Starting ...");
                
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println("Finished.");
                
                return duration;
            }
            
        });
        
        executor.shutdown();
        
        try {
            System.out.println("Result is: " + future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            IOException ex = (IOException) e.getCause();
            System.out.println(ex.getMessage());
        }
    }

}

```

Material refer:

https://www.caveofprogramming.com
https://www.youtube.com/watch?v=lotAYC3hLVo&list=PLBB24CFB073F1048E&index=3
https://www.youtube.com/watch?v=O_Ojfq-OIpM
https://docs.oracle.com/javase/7/docs/api















