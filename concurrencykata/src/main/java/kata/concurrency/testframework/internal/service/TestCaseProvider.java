package kata.concurrency.testframework.internal.service;

import kata.concurrency.testframework.internal.model.TestCase;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestCaseProvider {

    private static final TestCaseProvider TEST_CASE_PROVIDER = new TestCaseProvider();

    private TestCaseProvider() {
    }

    public static TestCaseProvider getInstance() {
        return TEST_CASE_PROVIDER;
    }

    public List<TestCase> provide(List<Object> testClassInstances, Class<? extends Annotation> annotationClass) {
        return testClassInstances.stream()
                .flatMap(instance -> getInvocationContextStream(instance, annotationClass))
                .collect(Collectors.toList());
    }

    private Stream<TestCase> getInvocationContextStream(Object instance, Class<? extends Annotation> annotationClass) {
        return MethodUtils.getMethodsListWithAnnotation(instance.getClass(), annotationClass).stream()
                .map(method -> new TestCase(instance, method));
    }
}
