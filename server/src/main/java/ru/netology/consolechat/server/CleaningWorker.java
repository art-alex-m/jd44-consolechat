package ru.netology.consolechat.server;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CleaningWorker implements Runnable {

    private final ConcurrentLinkedQueue<Connection> connectionsQueue;

    public CleaningWorker(ConcurrentLinkedQueue<Connection> connectionsQueue) {
        this.connectionsQueue = connectionsQueue;
    }

    @Override
    public void run() {
        if (connectionsQueue.isEmpty()) return;
        connectionsQueue.removeIf(c -> c.getStatus() == ConnectionStatus.CLOSED);
    }
}
