package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.model.InvocationContext;
import kata.concurrency.testframework.internal.model.TestResult;

import java.util.concurrent.Callable;

public class TestCallable implements Callable<TestResult> {

    private final InvocationContext context;

    public TestCallable(InvocationContext context) {
        this.context = context;
    }

    @Override
    public TestResult call() {
        TestResult testResult = new TestResult(context.getInstance().getClass().getSimpleName(), context.getMethod().getName());
        try {
            Test.init();
            context.getMethod().invoke(context.getInstance());
        } catch (Exception exception) {
            testResult.setRunStatus(TestResult.RunStatus.FAILURE);
            testResult.setException(exception);
        } finally {
            Test.destroy();
        }
        return testResult;
    }
}
