package kata.concurrency.testframework.internal.service;

import kata.concurrency.testframework.internal.model.TestCase;

import java.util.List;

public class TestCaseManager {

    private static final TestCaseManager TEST_CASE_MANAGER = new TestCaseManager();

    private TestCaseManager() {
    }

    public static TestCaseManager getInstance() {
        return TEST_CASE_MANAGER;
    }

    public List<TestCase> prepareTestCases(Class<?>... testClasses) throws IllegalAccessException, InstantiationException {
        List<Object> testClassInstances = TestClassInitializer.getInstance().getTestClassInstances(testClasses);
        return TestCaseProvider.getInstance().provide(testClassInstances, kata.concurrency.testframework.api.TestCase.class);
    }
}
