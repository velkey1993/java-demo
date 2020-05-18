package pluralsight.singleton;

public class MonoCoreSingleton {

    private static MonoCoreSingleton instance;

    private MonoCoreSingleton() {
    }

    public static synchronized MonoCoreSingleton getInstance() {
        if (instance == null) {
            instance = new MonoCoreSingleton();
        }
        return instance;
    }
}
