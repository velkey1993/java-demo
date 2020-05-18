package pluralsight.consumer_producer;

public class SharedObjectHolder {

    public static final Object LOCK = new Object();
    private static Integer BUFFER = 0;

    public static Integer getBUFFER() {
        return BUFFER;
    }

    public static Integer incrementBUFFER() {
        return ++BUFFER;
    }

    public static Integer decrementBUFFER() {
        return --BUFFER;
    }
}
