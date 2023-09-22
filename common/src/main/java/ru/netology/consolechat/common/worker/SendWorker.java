package ru.netology.consolechat.common.worker;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;
import ru.netology.consolechat.common.Message;
import ru.netology.consolechat.common.ProtocolWriter;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SendWorker implements Sleepable, Runnable {
    private final BlockingQueue<Message> senderQueue;
    private final ConcurrentLinkedQueue<Connection> connectionsQueue;
    private final ProtocolWriter writer;

    public SendWorker(BlockingQueue<Message> senderQueue, ConcurrentLinkedQueue<Connection> connectionsQueue,
                      ProtocolWriter writer) {
        this.senderQueue = senderQueue;
        this.connectionsQueue = connectionsQueue;
        this.writer = writer;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (connectionsQueue.isEmpty()) {
                sleep(20);
                continue;
            }

            doWork();
        }
    }

    public void doWork() {
        Message message;
        try {
            message = senderQueue.take();
        } catch (InterruptedException e) {
            return;
        }

        for (Connection connection : connectionsQueue) {
            try {
                writer.write(connection.getOutputStream(), message);
            } catch (IOException e) {
                connection.setStatus(ConnectionStatus.CLOSED);
            }
        }
    }
}
