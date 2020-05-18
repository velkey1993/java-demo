import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    private static Unsafe unsafe;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        getGCProperties();
//        getAddresses();
//        weakReference();
//        weakHashMap();
//        referenceQueue();
        phantomReference();
    }

    private static void getGCProperties() {
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        garbageCollectorMXBeans.forEach(bean -> {
            System.out.println("Name: " + bean.getName());
            System.out.println("Collection Count: " + bean.getCollectionCount());
            System.out.println("Collection Time: " + bean.getCollectionTime());
            System.out.println("Pool Names: ");
            Arrays.stream(bean.getMemoryPoolNames()).forEach(x -> System.out.println("    - " + x));
            System.out.println();
        });
    }

    private static void getAddresses() {
        IntStream.range(0, 32000).forEach(number -> {
            Object gcMe = new GCMe();
            long address = addressOf(gcMe);
            System.out.println(address);
        });
    }

    private static long addressOf(Object o) {
        Object[] objects = {o};
        int baseOffset = unsafe.arrayBaseOffset(Object[].class);
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4: // 32 bits
                objectAddress = unsafe.getInt(objects, baseOffset);
                break;
            case 8:
                objectAddress = unsafe.getLong(objects, baseOffset);
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }
        return objectAddress;
    }

    private static void weakReference() {
        Person person = new Person();
        System.out.println(person);
        WeakReference<Person> weakReference = new WeakReference<>(person);
        System.out.println(weakReference);
        Person p1 = weakReference.get();
        System.out.println(p1);

        person = null;
        p1 = null;

        Person p2 = weakReference.get();
        System.out.println(p2);

        p2 = null;
        System.gc();

        Person p3 = weakReference.get();
        System.out.println(p3);
    }

    private static void weakHashMap() {
        WeakHashMap<Person, PersonMetaData> weakHashMap = new WeakHashMap<>();
        Person person = new Person();
        weakHashMap.put(person, new PersonMetaData());
        System.out.println(weakHashMap);
        person = null;
        System.gc();
        System.out.println(weakHashMap);
    }

    private static void referenceQueue() {
        Person person = new Person();
        ReferenceQueue<Person> referenceQueue = new ReferenceQueue<>();
        PersonCleaner personCleaner = new PersonCleaner();
        PersonWeakReference weakReference = new PersonWeakReference(person, referenceQueue, personCleaner);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Thread.sleep(2000);
                PersonWeakReference personWeakReference = (PersonWeakReference) referenceQueue.remove();
                personWeakReference.clean();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        person = null;
        System.gc();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press any key to continue!");
        try {
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    private static void phantomReference() {
        ReferenceQueue<Person> queue = new ReferenceQueue<>();
        ArrayList<FinalizePerson> finalizePeople = new ArrayList<>();
        ArrayList<Person> people = new ArrayList<>();

        Stream.generate(Person::new)
                .limit(10)
                .peek(people::add)
                .forEach(person -> finalizePeople.add(new FinalizePerson(person, queue)));

        people = null;
        System.gc();

        System.out.println(finalizePeople.size());

        Reference<? extends Person> finalizePerson;
        while ((finalizePerson = queue.poll()) != null) {
            ((FinalizePerson) finalizePerson).clean();
        }
    }
}

final class Person {

}

class PersonMetaData {

    final Date date;

    PersonMetaData() {
        this.date = new Date();
    }

    @Override
    public String toString() {
        return "PersonMetaData{" +
                "date=" + date +
                '}';
    }
}

class PersonCleaner {

    public void clean() {
        System.out.println("Clean.");
    }
}

class PersonWeakReference extends WeakReference<Person> {

    final PersonCleaner personCleaner;

    public PersonWeakReference(Person referent, ReferenceQueue<? super Person> q, PersonCleaner personCleaner) {
        super(referent, q);
        this.personCleaner = personCleaner;
    }

    public void clean() {
        personCleaner.clean();
    }
}

class FinalizePerson extends PhantomReference<Person> {

    /**
     * Creates a new phantom reference that refers to the given object and
     * is registered with the given queue.
     *
     * <p> It is possible to create a phantom reference with a {@code null}
     * queue, but such a reference is completely useless: Its {@code get}
     * method will always return {@code null} and, since it does not have a queue,
     * it will never be enqueued.
     *
     * @param referent the object the new phantom reference will refer to
     * @param q        the queue with which the reference is to be registered,
     *                 or {@code null} if registration is not required
     */
    public FinalizePerson(Person referent, ReferenceQueue<? super Person> q) {
        super(referent, q);
    }

    public void clean() {
        System.out.println("Clean.");
    }
}
