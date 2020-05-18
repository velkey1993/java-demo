###### Threads and Runnables
* **Processes** are instances of programs which typically run independent to each other.
* Inside those processes we can utilize **threads** to execute code concurrently.
* Before starting a new **thread** you have to specify the code to be executed by this thread, often called the task.
* The task is done by implementing **Runnable** - a functional interface defining a single void no-args method run().

###### Executors
* **Executors** are capable of running asynchronous tasks and typically manage a pool of threads.
* The java process _never_ stops! - Executors _have to be stopped explicitly_ - otherwise they keep listening for new tasks.
* _submit()_ doesn't wait until the task completes
* _shutdown()_ waits for currently running tasks to finish
* _shutdownNow()_ interrupts all running tasks and shut the executor down immediately
* _awaitTermination()_ blocks the current thread and waits until the pre-defined time period

###### Callables and Futures
* **Callables** are functional interfaces just like **Runnables** but instead of being void they return a value.
* **Callables** can be submitted to executor services just like **Runnables**.
* Calling the method _get()_ blocks the current thread and waits until the callable completes before returning the actual result.
* _newFixedThreadPool(1)_ ~= _newSingleThreadExecutor()_, **BUT** thread pool can be increased later on.
* Simply counteract _get()_'s blocking by passing a timeout.
* **Executors** support batch submitting of multiple **Callables** at once via _invokeAll()_.
* _newWorkStealingPool()_ is part of Java 8 and returns an executor of type ForkJoinPool. Instead of using a fixed size thread-pool ForkJoinPools are created for a given parallelism size which per default is the number of available cores of the hosts CPU.

###### Scheduled Executors
* A **ScheduledExecutorService** is capable of scheduling tasks to run either periodically or once after a certain amount of time has elapsed.
* Scheduling a task produces a specialized future of type **ScheduledFuture** which - in addition to **Future** - provides the method _getDelay()_ to retrieve the remaining delay.
* _scheduleAtFixedRate()_ doesn't take into account the actual duration of the task
* _scheduleWithFixedDelay()_ - the wait time period applies between the end of a task and the start of the next task

###### Synchronized
* No synchronization - Race condition
* Java supports thread-synchronization since the early days via the _synchronized_ keyword.
* The _synchronized_ keyword is also available as a block statement.
* Internally Java uses a so called monitor also known as monitor lock or intrinsic lock in order to manage synchronization. (this monitor is bound to an object)
* All implicit monitors implement the reentrant characteristics. Reentrant means that locks are bound to the current thread.
* Deadlocks (e.g. a synchronized method calls another synchronized method on the same object)

###### Locks
* Concurrency API supports various explicit locks specified by the Lock interface.
* **ReentrantLock** is a mutual exclusion lock with the same basic behavior as the implicit monitors accessed via the synchronized keyword but with extended capabilities.
* _tryLock()_ as an alternative to _lock()_ tries to acquire the lock without pausing the current thread
* **ReadWriteLock** specifies another type of lock maintaining a pair of locks for read and write access.
* Usually safe to read mutable variables concurrently as long as nobody is writing to this variable - the read-lock can be held simultaneously by multiple threads as long as no threads hold the write-lock.
* **StampedLock** returns a stamp represented by a long value. (supports read and write locks and optimistic locking) - stamped locks don't implement reentrant characteristics
* Each call to **StampedLock** returns a new stamp and blocks if no lock is available even if the same thread already holds a lock.
* An optimistic read lock is acquired by calling _tryOptimisticRead()_ which always returns a stamp without blocking the current thread, no matter if the lock is actually available. f there's already a write lock active the returned stamp equals zero.
* _lock.validate(stamp)_ - stamp is valid

###### Semaphores
* Concurrency API also supports counting semaphores.
* **Locks** usually grant exclusive access to variables or resources.
* **Semaphore** is capable of maintaining whole sets of permits.
* Useful in different scenarios where you have to limit the amount concurrent access to certain parts of your application.

###### AtomicInteger
* The package _java.concurrent.atomic_ contains many useful classes to perform atomic operations.
* An operation is atomic when you can safely perform the operation in parallel on multiple threads without using the synchronized keyword or locks.
* Advice is to prefer atomic classes over locks in case you just have to change a single mutable variable concurrently.
* **AtomicInteger** / **AtomicBoolean** / **AtomicLong** / **AtomicReference**

###### LongAdder
* An alternative to **AtomicLong** can be used to consecutively add values to a number.
* This class is usually preferable over atomic numbers when updates from multiple threads are more common than reads.
* The drawback of **LongAdder** is higher memory consumption because a set of variables is held in-memory.

###### LongAccumulator
* **LongAccumulator** is a more generalized version of **LongAdder**.
* **LongAccumulator** builds around a lambda expression of type **LongBinaryOperator**.
* **LongAccumulator** just like **LongAdder** maintains a set of variables internally to reduce contention over threads.

###### ConcurrentMap
* **ConcurrentMap** extends the map interface and defines one of the most useful concurrent collection types.

###### ConcurrentHashMap
* **ConcurrentHashMap** has been further enhanced with a couple of new methods to perform parallel operations upon the map.
* Just like parallel streams those methods use a special **ForkJoinPool** available via _ForkJoinPool.commonPool()_ in Java 8. This pool uses a preset parallelism which depends on the number of available cores.
* 8 CPU cores are available on my machine which results in a parallelism of 7. This value can be decreased or increased by setting the following JVM parameter: _-Djava.winterbe.util.concurrent.ForkJoinPool.common.parallelism=5_
* Java 8 introduces three kinds of parallel operations: _forEach_, _search_ and _reduce_. All of those methods use a common first argument called _parallelismThreshold_. E.g. if you pass a threshold of 500 and the actual size of the map is 499 the operation will be performed sequentially on a single thread.
