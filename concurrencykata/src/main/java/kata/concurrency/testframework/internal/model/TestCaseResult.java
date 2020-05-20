package kata.concurrency.testframework.internal.model;

public class TestCaseResult {

    private final String className;
    private final String methodName;
    private RunStatus runStatus = RunStatus.SUCCESS;
    private Exception exception;

    public TestCaseResult(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void printStackTraceIfPresent() {
        if (exception != null) exception.getCause().printStackTrace();
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", runStatus=" + runStatus.getMessage() +
                '}';
    }

    public enum RunStatus {

        SUCCESS("Test ran successfully!"),
        FAILURE("Exception occurred during method invocation!");

        final String message;

        RunStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
