package kata.concurrency.testframework.internal.service;

import java.util.ArrayList;
import java.util.List;

public class TestClassInitializer {

    public List<Object> getTestClassInstances(Class<?>[] testClasses) throws InstantiationException, IllegalAccessException {
        List<Object> instances = new ArrayList<>();
        for (Class<?> testClass : testClasses) {
            instances.add(testClass.newInstance());
        }
        return instances;
    }
}
