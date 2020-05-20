package kata.concurrency.testframework.internal.service;

import kata.concurrency.testframework.internal.model.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class TestResultReporter {

    private static final Logger LOGGER = Logger.getLogger(TestResultReporter.class.getName());

    public void report(List<Future<TestResult>> futures) throws ExecutionException, InterruptedException {
        List<TestResult> testResults = new ArrayList<>();
        for (Future<TestResult> future : futures) {
            testResults.add(future.get());
        }
        testResults.forEach(testResult -> {
            LOGGER.info(testResult.toString());
            testResult.printStackTraceIfPresent();
        });
    }
}
