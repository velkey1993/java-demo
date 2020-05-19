package kata.concurrency.testframework.internal.model;

public class TestContext {

    private final TestGivenContext givenContext;
    private final TestWhenContext whenContext;

    private TestStatus testStatus = TestStatus.GIVEN;

    public TestContext() {
        this.givenContext = new TestGivenContext();
        this.whenContext = new TestWhenContext();
    }

    public TestGivenContext getGivenContext() {
        return givenContext;
    }

    public TestWhenContext getWhenContext() {
        return whenContext;
    }

    public TestStatus getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
    }
}
