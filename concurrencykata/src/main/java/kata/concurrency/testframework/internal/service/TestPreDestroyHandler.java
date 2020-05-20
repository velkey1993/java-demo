package kata.concurrency.testframework.internal.service;

import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPreDestroyHandler {

    private static final Logger LOGGER = Logger.getLogger(TestPreDestroyHandler.class.getName());

    public void handle(InvocationContextProvider invocationContextProvider, List<Object> testClassInstances) {
        invocationContextProvider.provide(testClassInstances, PreDestroy.class)
                .forEach(context -> {
                    try {
                        context.getMethod().invoke(context.getInstance());
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        LOGGER.log(Level.WARNING, exception.getMessage(), exception);
                    }
                });
    }
}
