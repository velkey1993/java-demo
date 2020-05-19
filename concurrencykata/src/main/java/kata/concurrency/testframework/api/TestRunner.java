package kata.concurrency.testframework.api;

import kata.concurrency.testframework.internal.model.InvocationContext;
import kata.concurrency.testframework.internal.model.TestResult;

import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestRunner {

    private static final Logger LOGGER = Logger.getLogger(TestRunner.class.getName());

    private final int maxThreadCount;

    public TestRunner(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public void run(Class<?>... testClasses) throws IllegalAccessException, InstantiationException,
            ExecutionException, InterruptedException, InvocationTargetException {

        ExecutorService executorService = Executors.newFixedThreadPool(maxThreadCount);

        List<Object> testClassInstances = getTestClassInstances(testClasses).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<InvocationContext> invocationContexts = testClassInstances.stream()
                .flatMap(this::getInvocationContextStream)
                .collect(Collectors.toList());

        List<Future<TestResult>> futures = filterContextByAnnotation(invocationContexts, TestCase.class).stream()
                .map(TestRunnable::new)
                .map(executorService::submit)
                .collect(Collectors.toList());

        logResults(futures);

        executorService.shutdown();

        preDestroyTestClasses(invocationContexts);
    }

    private List<Object> getTestClassInstances(Class<?>[] testClasses) throws InstantiationException, IllegalAccessException {
        List<Object> instances = new ArrayList<>();
        for (Class<?> testClass : testClasses) {
            instances.add(testClass.newInstance());
        }
        return instances;
    }

    private Stream<InvocationContext> getInvocationContextStream(Object instance) {
        return Arrays.stream(instance.getClass().getDeclaredMethods())
                .map(method -> new InvocationContext(instance, method));
    }

    private void logResults(List<Future<TestResult>> futures) throws ExecutionException, InterruptedException {
        List<TestResult> testResults = new ArrayList<>();
        for (Future<TestResult> future : futures) {
            testResults.add(future.get());
        }
        testResults.forEach(testResult -> {
            LOGGER.info(testResult.toString());
            testResult.printStackTraceIfPresent();
        });
    }

    private void preDestroyTestClasses(List<InvocationContext> invocationContexts) throws InvocationTargetException, IllegalAccessException {
        List<InvocationContext> filteredInvocationContexts = filterContextByAnnotation(invocationContexts, PreDestroy.class);
        for (InvocationContext context : filteredInvocationContexts) {
            context.getMethod().invoke(context.getInstance());
        }
    }

    private List<InvocationContext> filterContextByAnnotation(List<InvocationContext> invocationContexts, Class<? extends Annotation> annotation) {
        return invocationContexts.stream()
                .filter(context -> context.getMethod().isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private static class TestRunnable implements Callable<TestResult> {

        private final InvocationContext context;

        public TestRunnable(InvocationContext context) {
            this.context = context;
        }

        @Override
        public TestResult call() {
            TestResult testResult = new TestResult(context.getInstance().getClass().getSimpleName(), context.getMethod().getName());
            try {
                Test.init();
                context.getMethod().invoke(context.getInstance());
            } catch (Exception exception) {
                testResult.setRunStatus(TestResult.RunStatus.FAILURE);
                testResult.setException(exception);
            } finally {
                Test.destroy();
            }
            return testResult;
        }
    }
}
