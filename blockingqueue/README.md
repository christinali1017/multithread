####BlockingQueue

BlockingQueue is a queue which can be used to safely insert and retrieve element from it. Also, when queue is full the insert request is blocked and when the queue is empty the retrieve request is blocked.

Also, you can provide time parameter to stop waiting when timeout.

Here MyBlockingQueue and MyBlockingQueueSuportOfferList is two simple BlockingQueue examples which supports insert/retrieve/insert list of elements.


####BlockingQueue in java

- interface

- under java.util.concurrent

- implementing class: ArrayBlockingQueue, DelayQueue, LinkedBlockingDeque, LinkedBlockingQueue, LinkedTransferQueue, PriorityBlockingQueue, SynchronousQueue

- Insert/remove both supports 4 oprations
 - Throws exception: add(e)
 - Special value: offer(e)
 - Blocks: put(e)
 - Times out: offer(e, time, unit)
