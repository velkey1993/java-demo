package kata.concurrency.testframework.internal.service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPostConstructHandler {

    private static final Logger LOGGER = Logger.getLogger(TestPostConstructHandler.class.getName());

    public void handle(InvocationContextProvider invocationContextProvider, List<Object> testClassInstances) {
        invocationContextProvider.provide(testClassInstances, PostConstruct.class)
                .forEach(context -> {
                    try {
                        context.getMethod().invoke(context.getInstance());
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        LOGGER.log(Level.WARNING, exception.getMessage(), exception);
                    }
                });
    }
}
