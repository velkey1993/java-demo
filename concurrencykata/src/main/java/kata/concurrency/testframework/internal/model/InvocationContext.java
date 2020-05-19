package kata.concurrency.testframework.internal.model;

import java.lang.reflect.Method;

public class InvocationContext {

    private final Object instance;
    private final Method method;

    public InvocationContext(Object instance, Method method) {
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
