package pluralsight.consumer_producer;

import static pluralsight.consumer_producer.SharedObjectHolder.*;

public class Consumer {

    public void consume() {
        synchronized (LOCK) {
            if (isNull(getBUFFER())) {
                try {
                    LOCK.wait(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("consume with thread: " + Thread.currentThread().getName());
            System.out.println("number: " + (isNull(getBUFFER()) ? getBUFFER() : decrementBUFFER()));
            LOCK.notify();
        }
    }

    private boolean isNull(Integer number) {
        return number == 0;
    }
}
