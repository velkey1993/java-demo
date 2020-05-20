package kata.concurrency.testframework.internal.service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPostConstructHandler {

    private static final Logger LOGGER = Logger.getLogger(TestPostConstructHandler.class.getName());

    private static final TestPostConstructHandler TEST_POST_CONSTRUCT_HANDLER = new TestPostConstructHandler();

    private TestPostConstructHandler() {
    }

    public static TestPostConstructHandler getInstance() {
        return TEST_POST_CONSTRUCT_HANDLER;
    }

    public void handle(List<Object> testClassInstances) {
        TestCaseProvider.getInstance().provide(testClassInstances, PostConstruct.class)
                .forEach(context -> {
                    try {
                        context.getMethod().invoke(context.getInstance());
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        LOGGER.log(Level.WARNING, exception.getCause().getMessage(), exception);
                    }
                });
    }
}
