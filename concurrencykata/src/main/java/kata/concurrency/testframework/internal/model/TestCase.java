package kata.concurrency.testframework.internal.model;

import java.lang.reflect.Method;

public class TestCase {

    private final Object instance;
    private final Method method;

    public TestCase(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }
}
