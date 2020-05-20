package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.model.TestCase;
import kata.concurrency.testframework.internal.model.TestCaseResult;

import java.util.concurrent.Callable;

public class TestCallable implements Callable<TestCaseResult> {

    private final TestCase testCase;

    public TestCallable(TestCase testCase) {
        this.testCase = testCase;
    }

    @Override
    public TestCaseResult call() {
        TestCaseResult testCaseResult = new TestCaseResult(testCase.getInstance().getClass().getSimpleName(), testCase.getMethod().getName());
        try {
            Test.init();
            testCase.getMethod().invoke(testCase.getInstance());
        } catch (Exception exception) {
            testCaseResult.setRunStatus(TestCaseResult.RunStatus.FAILURE);
            testCaseResult.setException(exception);
        } finally {
            Test.destroy();
        }
        return testCaseResult;
    }
}
