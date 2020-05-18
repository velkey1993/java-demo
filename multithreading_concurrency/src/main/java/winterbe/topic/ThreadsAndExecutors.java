package winterbe.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ThreadsAndExecutors {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadsAndExecutors.class);

    public void all() {
        task();
        sleep();
        executor();
        executorInterrupted();
        executorShutdownCorrectly();
        callable();
        invokeAll();
        invokeAny();
        scheduledExecutor();
        scheduledExecutorAtFixedRate();
        scheduledExecutorWithFixedDelay();
    }

    public void task() {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            LOGGER.info("task() - " + "Hello " + threadName);
        };

        task.run();

        Thread thread = new Thread(task);
        thread.start();
    }

    public void sleep() {
        Runnable runnable = () -> {
            try {
                String name = Thread.currentThread().getName();
                LOGGER.info("sleep() - " + "Foo " + name);
                long start = System.currentTimeMillis();
                LOGGER.info("sleep() - " + "Current time: " + start);
                TimeUnit.SECONDS.sleep(1);
                LOGGER.info("sleep() - " + "Bar " + name);
                long end = System.currentTimeMillis();
                LOGGER.info("sleep() - " + "Current time: " + end);
                LOGGER.info("sleep() - " + "Sleep duration: " + (end - start) + " ms");
            } catch (InterruptedException e) {
                System.err.println("sleep() - Interrupted");
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void executor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                TimeUnit.SECONDS.sleep(5);
                LOGGER.info("executor() - " + "Hello " + threadName);
            } catch (InterruptedException e) {
                System.err.println("executor() - Interrupted");
            }
        });
        executor.shutdown();
    }

    public void executorInterrupted() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                TimeUnit.SECONDS.sleep(5);
                LOGGER.info("executorInterrupted() - " + "Hello " + threadName);
            } catch (InterruptedException e) {
                System.err.println("executorInterrupted() - Interrupted");
            }
        });
        executor.shutdownNow();
    }

    public void executorShutdownCorrectly() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                TimeUnit.SECONDS.sleep(5);
                LOGGER.info("executorShutdownCorrectly() - " + "Hello " + threadName);
            } catch (InterruptedException e) {
                System.err.println("executorShutdownCorrectly() - Interrupted");
            }
        });
        try {
            System.err.println("executorShutdownCorrectly() - " + "Terminated: " + executor.isTerminated());
            System.err.println("executorShutdownCorrectly() - " + "Shutdown: " + executor.isShutdown());
            LOGGER.info("executorShutdownCorrectly() - " + "attempt to shutdown executor");
            executor.shutdown();
            LOGGER.info("executorShutdownCorrectly() - " + "attempt to terminate executor for 4 seconds");
            executor.awaitTermination(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("executorShutdownCorrectly() - " + "tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("executorShutdownCorrectly() - " + "cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.err.println("executorShutdownCorrectly() - " + "Terminated: " + executor.isTerminated());
            System.err.println("executorShutdownCorrectly() - " + "Shutdown: " + executor.isShutdown());
            LOGGER.info("executorShutdownCorrectly() - " + "shutdown finished");
        }
    }

    public void callable() {
        Callable<Integer> task = () -> {
            try {
                LOGGER.info("callable() - " + "attempt to sleep one second");
                TimeUnit.SECONDS.sleep(2);
                return 123;
            } catch (InterruptedException e) {
                throw new IllegalStateException("callable() - " + "task interrupted", e);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(task);

        LOGGER.info("callable() - " + "future done? " + future.isDone());

        Integer result = null;
        try {
            result = future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("callable() - " + "Interrupted");
        } catch (ExecutionException e) {
            System.err.println("callable() - " + "Exception");
        } catch (TimeoutException e) {
            System.err.println("callable() - " + "Timeout exception");
        }

        LOGGER.info("callable() - " + "future done? " + future.isDone());
        LOGGER.info("callable() - " + "result: " + result);

        executor.shutdown();
    }

    public void invokeAll() {
        ExecutorService executor = Executors.newWorkStealingPool();

        List<Callable<String>> callables = Arrays.asList(
                () -> "invokeAll() - " + "task1",
                () -> "invokeAll() - " + "task2",
                () -> "invokeAll() - " + "task3");

        try {
            executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .forEach(System.out::println);
        } catch (InterruptedException e) {
            System.err.println("invokeAll() - " + "Interrupted");
        }
    }

    public void invokeAny() {
        ExecutorService executor = Executors.newWorkStealingPool();

        List<Callable<String>> callables = Arrays.asList(
                callableEntity("invokeAny() - " + "task1", 2),
                callableEntity("invokeAny() - " + "task2", 1),
                callableEntity("invokeAny() - " + "task3", 3));

        String result = null;
        try {
            result = executor.invokeAny(callables);
        } catch (InterruptedException e) {
            System.err.println("invokeAny() - " + "Interrupted");
        } catch (ExecutionException e) {
            System.err.println("invokeAny() - " + "Execution exception");
        }
        LOGGER.info("invokeAny() - " + result);
    }

    public Callable<String> callableEntity(String result, long sleepSeconds) {
        return () -> {
            TimeUnit.SECONDS.sleep(sleepSeconds);
            return result;
        };
    }

    public void scheduledExecutor() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> LOGGER.info("scheduledExecutor() - " + "Scheduling: " + System.nanoTime());
        ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

        try {
            TimeUnit.MILLISECONDS.sleep(1337);
        } catch (InterruptedException e) {
            System.err.println("scheduledExecutor() - " + "Interrupted");
        }

        long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.printf("scheduledExecutor() - " + "Remaining Delay: %sms", remainingDelay);
    }

    public void scheduledExecutorAtFixedRate() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> LOGGER.info("scheduledExecutorAtFixedRate() - " + "Scheduling: " + System.nanoTime());

        int initialDelay = 0;
        int period = 1;
        executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }

    public void scheduledExecutorWithFixedDelay() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                LOGGER.info("scheduledExecutorWithFixedDelay() - " + "Scheduling: " + System.nanoTime());
            } catch (InterruptedException e) {
                System.err.println("scheduledExecutorWithFixedDelay() - " + "task interrupted");
            }
        };

        executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
    }
}
