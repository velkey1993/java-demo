package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.exception.WrongTestOrderException;
import kata.concurrency.testframework.internal.model.TestContext;
import kata.concurrency.testframework.internal.model.TestGivenContext;
import kata.concurrency.testframework.internal.model.TestStatus;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Test {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final ThreadLocal<TestContext> TEST_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    protected static void init() {
        TEST_CONTEXT_THREAD_LOCAL.set(new TestContext());
    }

    protected static void destroy() {
        TEST_CONTEXT_THREAD_LOCAL.remove();
    }

    public static void addQueryParam(String key, String value) {
        checkTestState(TestStatus.GIVEN);
        TEST_CONTEXT_THREAD_LOCAL.get().getGivenContext().addQueryParameter(key, value);
    }

    public static void addPath(String path) {
        checkTestState(TestStatus.GIVEN);
        TEST_CONTEXT_THREAD_LOCAL.get().getGivenContext().setPath(path);
    }

    public static void setBaseUrl(String url) {
        checkTestState(TestStatus.GIVEN);
        TEST_CONTEXT_THREAD_LOCAL.get().getGivenContext().setBaseUrl(url);
    }

    public static <T> void setResponseDeserializer(Function<String, T> deserializer) throws IOException {
        checkTestState(TestStatus.GIVEN, TestStatus.WHEN);
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        URL url = buildURL(testContext.getGivenContext());
        ResponseBody body = OK_HTTP_CLIENT.newCall(new Request.Builder().get().url(url).build()).execute().body();
        T object = deserializer.apply(Objects.requireNonNull(body).string());
        testContext.getWhenContext().setResponse(object);
        testContext.setTestStatus(TestStatus.WHEN);
    }

    public static <T> void assertThat(Consumer<T> assertion) {
        checkTestState(TestStatus.WHEN, TestStatus.THEN);
        TestContext testContext = TEST_CONTEXT_THREAD_LOCAL.get();
        T response = (T) testContext.getWhenContext().getResponse();
        assertion.accept(response);
        testContext.setTestStatus(TestStatus.THEN);
    }

    private static void checkTestState(TestStatus... expectedTestStatuses) {
        TestStatus actualTestStatus = TEST_CONTEXT_THREAD_LOCAL.get().getTestStatus();
        if (!Arrays.asList(expectedTestStatuses).contains(actualTestStatus)) {
            throw new WrongTestOrderException();
        }
    }

    private static URL buildURL(TestGivenContext testGivenContext) {
        HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder()
                .port(1080)
                .scheme("http")
                .host(testGivenContext.getBaseUrl())
                .addPathSegments(testGivenContext.getPath());
        testGivenContext.getQueryParameterMap().forEach(httpUrlBuilder::addQueryParameter);
        return httpUrlBuilder.build().url();
    }
}
