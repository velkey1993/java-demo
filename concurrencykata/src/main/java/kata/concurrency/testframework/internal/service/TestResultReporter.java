package kata.concurrency.testframework.internal.service;

import kata.concurrency.testframework.internal.model.TestCaseResult;

import java.util.List;
import java.util.logging.Logger;

public class TestResultReporter {

    private static final Logger LOGGER = Logger.getLogger(TestResultReporter.class.getName());

    private static final TestResultReporter TEST_RESULT_REPORTER = new TestResultReporter();

    private TestResultReporter() {
    }

    public static TestResultReporter getInstance() {
        return TEST_RESULT_REPORTER;
    }

    public void report(List<TestCaseResult> testCaseResults) {
        testCaseResults.forEach(testCaseResult -> {
            LOGGER.info(testCaseResult.toString());
            testCaseResult.printStackTraceIfPresent();
        });
    }
}
