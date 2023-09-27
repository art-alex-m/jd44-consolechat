package ru.netology.consolechat.common.worker;

public interface Sleepable {
    int DEFAULT_SLEEP_TIME = 20;

    default boolean sleep(int millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    default boolean sleep() {
        return sleep(DEFAULT_SLEEP_TIME);
    }
}
