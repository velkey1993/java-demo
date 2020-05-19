package kata.concurrency.tests;

import kata.concurrency.testframework.api.TestRunner;
import kata.concurrency.tests.testcases.SampleTest;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

public class SampleMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        new TestRunner(4).run(SampleTest.class);
    }
}
