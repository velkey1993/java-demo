package kata.concurrency.testframework.internal.service;

import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPreDestroyHandler {

    private static final Logger LOGGER = Logger.getLogger(TestPreDestroyHandler.class.getName());

    private static final TestPreDestroyHandler TEST_PRE_DESTROY_HANDLER = new TestPreDestroyHandler();

    private TestPreDestroyHandler() {
    }

    public static TestPreDestroyHandler getInstance() {
        return TEST_PRE_DESTROY_HANDLER;
    }

    public void handle(List<Object> testClassInstances) {
        TestCaseProvider.getInstance().provide(testClassInstances, PreDestroy.class)
                .forEach(context -> {
                    try {
                        context.getMethod().invoke(context.getInstance());
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        LOGGER.log(Level.WARNING, exception.getMessage(), exception);
                    }
                });
    }
}
