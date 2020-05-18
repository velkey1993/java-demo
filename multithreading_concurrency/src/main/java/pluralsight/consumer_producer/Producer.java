package pluralsight.consumer_producer;

import static pluralsight.consumer_producer.SharedObjectHolder.*;

public class Producer {

    public void produce() {
        synchronized (LOCK) {
            if (isFull(getBUFFER())) {
                try {
                    LOCK.wait(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("produce with thread: " + Thread.currentThread().getName());
            System.out.println("number: " + incrementBUFFER());
            LOCK.notify();
        }
    }

    private boolean isFull(Integer number) {
        return number > 30;
    }
}
