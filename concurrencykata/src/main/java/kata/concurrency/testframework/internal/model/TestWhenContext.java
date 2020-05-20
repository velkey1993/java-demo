package kata.concurrency.testframework.internal.model;

import java.util.function.Function;

public class TestWhenContext {

    private Function<String, ?> deserializer;

    public Function<String, ?> getDeserializer() {
        return deserializer;
    }

    public <T> void setDeserializer(Function<String, T> deserializer) {
        this.deserializer = deserializer;
    }
}
