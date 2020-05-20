package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.model.TestCase;
import kata.concurrency.testframework.internal.model.TestCaseResult;
import kata.concurrency.testframework.internal.service.TestCaseExecutor;
import kata.concurrency.testframework.internal.service.TestCaseManager;
import kata.concurrency.testframework.internal.service.TestResultReporter;

import java.util.List;

public class TestRunner {

    private final TestCaseManager testCaseManager;
    private final TestCaseExecutor testCaseExecutor;
    private final TestResultReporter testResultReporter;

    public TestRunner(int maxThreadCount) {
        this.testCaseManager = TestCaseManager.getInstance();
        this.testCaseExecutor = new TestCaseExecutor(maxThreadCount);
        this.testResultReporter = TestResultReporter.getInstance();
    }

    public void run(Class<?>... testClasses) throws IllegalAccessException, InstantiationException {
        List<TestCase> testCases = testCaseManager.prepareTestCases(testClasses);
        List<TestCaseResult> results = testCaseExecutor.execute(testCases);
        testResultReporter.report(results);
    }
}
