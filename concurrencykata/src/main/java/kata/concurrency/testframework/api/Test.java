package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.model.TestContext;
import kata.concurrency.testframework.internal.model.TestStatus;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Test {

    private static final ThreadLocal<TestContext> TEST_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    protected static void init() {
        TEST_CONTEXT_THREAD_LOCAL.set(new TestContext());
    }

    protected static void destroy() {
        TEST_CONTEXT_THREAD_LOCAL.remove();
    }

    public static void addQueryParam(String key, String value) {
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        testContext.setTestStatus(TestStatus.GIVEN);
        testContext.getGivenContext().addQueryParameter(key, value);
    }

    public static void addPath(String path) {
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        testContext.setTestStatus(TestStatus.GIVEN);
        testContext.getGivenContext().setPath(path);
    }

    public static void setBaseUrl(String url) {
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        testContext.setTestStatus(TestStatus.GIVEN);
        testContext.getGivenContext().setBaseUrl(url);
    }

    public static <T> void setResponseDeserializer(Function<String, T> deserializer) throws IOException {
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        testContext.setTestStatus(TestStatus.WHEN);
        testContext.getWhenContext().setDeserializer(deserializer);
        testContext.executeCall();
    }

    public static <T> void assertThat(Consumer<T> assertion) {
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        testContext.setTestStatus(TestStatus.THEN);
        testContext.evaluateAssertion(assertion);
    }
}
