package winterbe.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import winterbe.util.ConcurrentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public class SynchronizationAndLocks {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationAndLocks.class);

    private static int count = 0;
    private static int countForWriteLock = 0;

    private static void increment() {
        count = count + 1;
    }

    private static synchronized void incrementSync() {
        count = count + 1;
    }

    public void all() {
        unSynchronized();
        synchronizedTest();
        reentrantLock();
        readWriteLock();
        stampedLock();
        optimisticLocking();
        convertToWriteLock();
        semaphores();
    }

    public void unSynchronized() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 10000)
                .forEach(i -> executor.submit(SynchronizationAndLocks::increment));

        ConcurrentUtils.stop(executor);

        LOGGER.info(String.valueOf(count));
    }

    public void synchronizedTest() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 10000)
                .forEach(i -> executor.submit(SynchronizationAndLocks::incrementSync));

        ConcurrentUtils.stop(executor);

        LOGGER.info(String.valueOf(count));
    }

    public void reentrantLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ReentrantLock lock = new ReentrantLock();

        executor.submit(() -> {
            lock.lock();
            try {
                ConcurrentUtils.sleep(1);
            } finally {
                lock.unlock();
            }
        });

        executor.submit(() -> {
            LOGGER.info("reentrantLock() - " + "Locked: " + lock.isLocked());
            LOGGER.info("reentrantLock() - " + "Held by me: " + lock.isHeldByCurrentThread());
            boolean locked = lock.tryLock();
            LOGGER.info("reentrantLock() - " + "Lock acquired: " + locked);
        });

        ConcurrentUtils.stop(executor);
    }

    public void readWriteLock() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Map<String, String> map = new HashMap<>();
        ReadWriteLock lock = new ReentrantReadWriteLock();

        executor.submit(() -> {
            lock.writeLock().lock();
            try {
                ConcurrentUtils.sleep(2);
                map.put("foo", "bar");
            } finally {
                lock.writeLock().unlock();
            }
        });

        Runnable readTask = () -> {
            lock.readLock().lock();
            try {
                LOGGER.info("readWriteLock() - " + map.get("foo"));
                ConcurrentUtils.sleep(1);
            } finally {
                lock.readLock().unlock();
            }
        };

        executor.submit(readTask);
        executor.submit(readTask);

        executor.submit(() -> {
            lock.writeLock().lock();
            try {
                ConcurrentUtils.sleep(1);
                map.put("foo", "bor");
            } finally {
                lock.writeLock().unlock();
            }
        });

        executor.submit(readTask);

        ConcurrentUtils.stop(executor);
    }

    public void stampedLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Map<String, String> map = new HashMap<>();
        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                ConcurrentUtils.sleep(1);
                map.put("foo", "bar");
            } finally {
                lock.unlockWrite(stamp);
            }
        });

        Runnable readTask = () -> {
            long stamp = lock.readLock();
            try {
                LOGGER.info("stampedLock() - " + map.get("foo"));
                ConcurrentUtils.sleep(1);
            } finally {
                lock.unlockRead(stamp);
            }
        };

        executor.submit(readTask);
        executor.submit(readTask);

        ConcurrentUtils.stop(executor);
    }

    public void optimisticLocking() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            long stamp = lock.tryOptimisticRead();
            try {
                LOGGER.info("optimisticLocking() - " + "Optimistic Lock Valid: " + stamp + " | " + lock.validate(stamp));
                ConcurrentUtils.sleep(1);
                LOGGER.info("optimisticLocking() - " + "Optimistic Lock Valid: " + stamp + " | " + lock.validate(stamp));
                ConcurrentUtils.sleep(2);
                LOGGER.info("optimisticLocking() - " + "Optimistic Lock Valid: " + stamp + " | " + lock.validate(stamp));
            } finally {
                lock.unlock(stamp);
            }
        });

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                LOGGER.info("optimisticLocking() - " + "Write Lock acquired: " + stamp);
                ConcurrentUtils.sleep(2);
            } finally {
                lock.unlock(stamp);
                LOGGER.info("optimisticLocking() - " + "Write done");
            }
        });

        ConcurrentUtils.stop(executor);
    }

    public void convertToWriteLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            long stamp = lock.tryOptimisticRead();
            LOGGER.info("convertToWriteLock() - " + "Read Lock acquired: " + stamp);
            ConcurrentUtils.sleep(1);
            try {
                if (countForWriteLock == 0) {
                    stamp = lock.tryConvertToWriteLock(stamp);
                    LOGGER.info("convertToWriteLock() - " + "Converted Write Lock acquired: " + stamp);
                    if (stamp == 0L) {
                        LOGGER.info("convertToWriteLock() - " + "Could not convert to write lock");
                        stamp = lock.writeLock();
                        LOGGER.info("convertToWriteLock() - " + "Write Lock acquired: " + stamp);
                    }
                    countForWriteLock = 23;
                }
                LOGGER.info("convertToWriteLock() - " + countForWriteLock);
            } finally {
                lock.unlock(stamp);
            }
        });

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                LOGGER.info("convertToWriteLock() - " + "Write Lock acquired: " + stamp);
                ConcurrentUtils.sleep(2);
            } finally {
                lock.unlock(stamp);
                LOGGER.info("convertToWriteLock() - " + "Write done");
            }
        });

        ConcurrentUtils.stop(executor);
    }

    public void semaphores() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Semaphore semaphore = new Semaphore(4);

        Runnable longRunningTask = () -> {
            boolean permit = false;
            try {
                permit = semaphore.tryAcquire(1, TimeUnit.SECONDS);
                if (permit) {
                    LOGGER.info("semaphores() - " + "Semaphore acquired");
                    ConcurrentUtils.sleep(5);
                } else {
                    LOGGER.info("semaphores() - " + "Could not acquire semaphore");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                if (permit) {
                    semaphore.release();
                }
            }
        };

        IntStream.range(0, 10)
                .forEach(i -> executor.submit(longRunningTask));

        ConcurrentUtils.stop(executor);
    }
}
