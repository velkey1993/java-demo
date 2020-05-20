package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.model.TestResult;
import kata.concurrency.testframework.internal.service.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TestRunner {

    private final int maxThreadCount;
    private final TestClassInitializer testClassInitializer;
    private final TestPostConstructHandler testPostConstructHandler;
    private final InvocationContextProvider invocationContextProvider;
    private final TestResultReporter testResultReporter;
    private final TestPreDestroyHandler testPreDestroyHandler;

    public TestRunner(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        this.testClassInitializer = new TestClassInitializer();
        this.testPostConstructHandler = new TestPostConstructHandler();
        this.invocationContextProvider = new InvocationContextProvider();
        this.testResultReporter = new TestResultReporter();
        this.testPreDestroyHandler = new TestPreDestroyHandler();
    }

    public void run(Class<?>... testClasses)
            throws IllegalAccessException, InstantiationException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(maxThreadCount);
        List<Object> testClassInstances = testClassInitializer.getTestClassInstances(testClasses);
        testPostConstructHandler.handle(invocationContextProvider, testClassInstances);
        List<Future<TestResult>> futures = runTestCases(executorService, testClassInstances);
        testResultReporter.report(futures);
        testPreDestroyHandler.handle(invocationContextProvider, testClassInstances);
        executorService.shutdown();
    }

    private List<Future<TestResult>> runTestCases(ExecutorService executorService, List<Object> testClassInstances) {
        return invocationContextProvider.provide(testClassInstances, TestCase.class)
                .map(TestCallable::new)
                .map(executorService::submit)
                .collect(Collectors.toList());
    }
}
