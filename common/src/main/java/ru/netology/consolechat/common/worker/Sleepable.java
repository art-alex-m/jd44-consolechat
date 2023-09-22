package ru.netology.consolechat.common.worker;

public interface Sleepable {
    default boolean sleep(int millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
