package kata.concurrency.testframework.internal.service;

import kata.concurrency.testframework.api.TestCallable;
import kata.concurrency.testframework.internal.model.TestCase;
import kata.concurrency.testframework.internal.model.TestCaseResult;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TestCaseExecutor {

    private static final Logger LOGGER = Logger.getLogger(TestCaseExecutor.class.getName());

    private final ExecutorService executorService;

    public TestCaseExecutor(int maxThreadCount) {
        this.executorService = Executors.newFixedThreadPool(maxThreadCount);
    }

    public List<TestCaseResult> execute(List<TestCase> testCases) {
        List<Object> testClassInstances = getTestClassInstances(testCases);
        TestPostConstructHandler.getInstance().handle(testClassInstances);
        List<Future<TestCaseResult>> futures = runTestCases(testCases);
        List<TestCaseResult> testCaseResults = getTestCaseResults(futures);
        executorService.shutdown();
        TestPreDestroyHandler.getInstance().handle(testClassInstances);
        return testCaseResults;
    }

    private List<Object> getTestClassInstances(List<TestCase> testCases) {
        return testCases.stream()
                .map(TestCase::getInstance)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Future<TestCaseResult>> runTestCases(List<TestCase> testCases) {
        return testCases.stream()
                .map(TestCallable::new)
                .map(executorService::submit)
                .collect(Collectors.toList());
    }

    private List<TestCaseResult> getTestCaseResults(List<Future<TestCaseResult>> futures) {
        return futures.stream()
                .map(testCaseResultFuture -> {
                    TestCaseResult testCaseResult = null;
                    try {
                        testCaseResult = testCaseResultFuture.get();
                    } catch (InterruptedException | ExecutionException exception) {
                        LOGGER.log(Level.WARNING, exception.getCause().getMessage(), exception);
                    }
                    return testCaseResult;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
