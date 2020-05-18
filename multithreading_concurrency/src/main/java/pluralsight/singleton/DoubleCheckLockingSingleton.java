package pluralsight.singleton;

public class DoubleCheckLockingSingleton {

    private static final Object KEY = new Object();

    private static volatile DoubleCheckLockingSingleton instance;

    private DoubleCheckLockingSingleton() {
    }

    public static synchronized DoubleCheckLockingSingleton getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (KEY) {
            if (instance == null) {
                instance = new DoubleCheckLockingSingleton();
            }
            return instance;
        }
    }
}
