package kata.concurrency.testframework.internal.model;

import kata.concurrency.testframework.internal.exception.WrongTestOrderException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class TestContext {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    private final TestGivenContext givenContext;
    private final TestWhenContext whenContext;
    private final TestThenContext thenContext;

    private TestStatus testStatus = TestStatus.GIVEN;

    public TestContext() {
        this.givenContext = new TestGivenContext();
        this.whenContext = new TestWhenContext();
        this.thenContext = new TestThenContext();
    }

    public TestGivenContext getGivenContext() {
        return givenContext;
    }

    public TestWhenContext getWhenContext() {
        return whenContext;
    }

    public TestThenContext getThenContext() {
        return thenContext;
    }

    public TestStatus getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestStatus testStatus) {
        if (isValidTestStatus(testStatus)) {
            this.testStatus = testStatus;
        } else {
            throw new WrongTestOrderException();
        }
    }

    public <T> void evaluateAssertion(Consumer<T> assertion) {
        T object = (T) thenContext.getResponse();
        assertion.accept(object);
    }

    public void executeCall() throws IOException {
        if (thenContext.getResponse() == null) {
            URL url = givenContext.buildURL();
            Request request = new Request.Builder().get().url(url).build();
            ResponseBody responseBody = OK_HTTP_CLIENT.newCall(request).execute().body();
            Object object = whenContext.getDeserializer().apply(Objects.requireNonNull(responseBody).string());
            thenContext.setResponse(object);
        }
    }

    private boolean isValidTestStatus(TestStatus testStatus) {
        return (testStatus.equals(TestStatus.GIVEN) && this.testStatus.equals(TestStatus.GIVEN))
                || (testStatus.equals(TestStatus.WHEN) && this.testStatus.equals(TestStatus.GIVEN))
                || (testStatus.equals(TestStatus.THEN) && Arrays.asList(TestStatus.WHEN, TestStatus.THEN).contains(this.testStatus));
    }
}
