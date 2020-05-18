import pluralsight.consumer_producer.Consumer;
import pluralsight.consumer_producer.Producer;
import pluralsight.consumer_producer.SharedObjectHolder;
import winterbe.topic.AtomicVariablesAndConcurrentMap;
import winterbe.topic.SynchronizationAndLocks;
import winterbe.topic.ThreadsAndExecutors;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main.main() - " + "Start!");

        ThreadsAndExecutors threadsAndExecutors = new ThreadsAndExecutors();
        SynchronizationAndLocks synchronizationAndLocks = new SynchronizationAndLocks();
        AtomicVariablesAndConcurrentMap atomicVariablesAndConcurrentMap = new AtomicVariablesAndConcurrentMap();

        producerConsumer();

        System.out.println("Main.main() - " + "Done!");
    }

    private static void producerConsumer() throws InterruptedException {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        List<Thread> producerThreads = IntStream.range(0, 200)
                .mapToObj(num -> new Thread(producer::produce))
                .collect(Collectors.toList());

        List<Thread> consumerThreads = IntStream.range(0, 40)
                .mapToObj(num -> new Thread(consumer::consume))
                .collect(Collectors.toList());

        Stream.concat(producerThreads.stream(), consumerThreads.stream())
                .unordered()
                .forEach(Thread::start);

        Thread.sleep(3000);

        System.out.println(SharedObjectHolder.getBUFFER());
    }
}
