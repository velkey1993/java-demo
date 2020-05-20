package kata.concurrency.testframework.internal.exception;

public class WrongTestOrderException extends RuntimeException {

    public WrongTestOrderException() {
        super("Test order is wrong!");
    }
}
