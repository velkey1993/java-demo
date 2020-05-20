package kata.concurrency.testframework.internal.service;

import kata.concurrency.testframework.internal.model.InvocationContext;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

public class InvocationContextProvider {

    public Stream<InvocationContext> provide(List<Object> testClassInstances, Class<? extends Annotation> annotationClass) {
        return testClassInstances.stream()
                .flatMap(instance -> getInvocationContextStream(instance, annotationClass));
    }

    private Stream<InvocationContext> getInvocationContextStream(Object instance, Class<? extends Annotation> annotationClass) {
        return MethodUtils.getMethodsListWithAnnotation(instance.getClass(), annotationClass).stream()
                .map(method -> new InvocationContext(instance, method));
    }
}
