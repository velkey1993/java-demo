package winterbe.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

import static winterbe.util.ConcurrentUtils.stop;

public class AtomicVariablesAndConcurrentMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtomicVariablesAndConcurrentMap.class);

    public void all() {
        atomicIncrementAndGet();
        atomicUpdateAndGet();
        atomicAccumulateAndGet();
        longAdder();
        longAccumulator();
        concurrentMap();
        concurrentHashMap();
    }

    public void atomicIncrementAndGet() {
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> executor.submit(atomicInt::incrementAndGet));

        stop(executor);

        LOGGER.info(String.valueOf(atomicInt.get()));
    }

    public void atomicUpdateAndGet() {
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> executor.submit(() -> atomicInt.updateAndGet(n -> n + 2)));

        stop(executor);

        LOGGER.info(String.valueOf(atomicInt.get()));
    }

    public void atomicAccumulateAndGet() {
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> executor.submit(() -> atomicInt.accumulateAndGet(i, Integer::sum)));

        stop(executor);

        LOGGER.info(String.valueOf(atomicInt.get()));
    }

    public void longAdder() {
        LongAdder adder = new LongAdder();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> executor.submit(adder::increment));

        stop(executor);

        LOGGER.info(String.valueOf(adder.sumThenReset()));
    }

    public void longAccumulator() {
        LongBinaryOperator op = (x, y) -> 2 * x + y;
        LongAccumulator accumulator = new LongAccumulator(op, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 10)
                .forEach(i -> executor.submit(() -> accumulator.accumulate(i)));

        stop(executor);

        LOGGER.info(String.valueOf(accumulator.getThenReset()));
    }

    public void concurrentMap() {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("foo", "bar");
        map.put("han", "solo");
        map.put("r2", "d2");
        map.put("c3", "p0");

        map.forEach((key, value) -> LOGGER.info("{} = {}", key, value));

        String value1 = map.putIfAbsent("c3", "p1");
        LOGGER.info(value1);

        String value2 = map.getOrDefault("hi", "there");
        LOGGER.info(value2);

        map.replaceAll((key, value) -> "r2".equals(key) ? "d3" : value);
        LOGGER.info(map.get("r2"));

        map.compute("foo", (key, value) -> value + value);
        LOGGER.info(map.get("foo"));

        map.merge("foo", "boo", (oldVal, newVal) -> newVal + " was " + oldVal);
        LOGGER.info(map.get("foo"));
    }

    public void concurrentHashMap() {
        LOGGER.info(String.valueOf(ForkJoinPool.getCommonPoolParallelism())); // 8 CPU cores -> parallelism of 7

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("foo", "bar");
        map.put("han", "solo");
        map.put("r2", "d2");
        map.put("c3", "p0");
        map.put("c4", "p0");
        map.put("c5", "p0");
        map.put("c6", "p0");
        map.put("c7", "p0");
        map.put("c8", "p0");

        LOGGER.info("FOREACH");
        map.forEach(3, (key, value) ->
                LOGGER.info("key: {}; value: {}; thread: {}",
                        key, value, Thread.currentThread().getName()));

        LOGGER.info("SEARCH");
        String searchResult = map.search(2, (key, value) -> {
            LOGGER.info(Thread.currentThread().getName());
            if ("foo".equals(key)) {
                return value;
            }
            return null;
        });
        LOGGER.info(searchResult);

        LOGGER.info("REDUCE");
        String reduceResult = map.reduce(2,
                (key, value) -> {
                    LOGGER.info("Transform: " + Thread.currentThread().getName());
                    return key + "=" + value;
                },
                (s1, s2) -> {
                    LOGGER.info("Reduce: " + Thread.currentThread().getName());
                    return s1 + ", " + s2;
                });

        LOGGER.info("Result: " + reduceResult);
    }
}
