package kata.concurrency.tests;

import kata.concurrency.testframework.api.TestRunner;
import kata.concurrency.tests.testcases.SampleTest;

public class SampleMain {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        new TestRunner(4).run(SampleTest.class);
    }
}
