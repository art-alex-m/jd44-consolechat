package ru.netology.consolechat.common.worker;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;
import ru.netology.consolechat.common.Message;
import ru.netology.consolechat.common.ProtocolReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiveWorker implements Sleepable, Runnable {
    private final List<BlockingQueue<Message>> consumerQueues;
    private final ConcurrentLinkedQueue<Connection> connectionsQueue;
    private final ProtocolReader reader;

    private boolean loop = true;

    public ReceiveWorker(List<BlockingQueue<Message>> consumerQueues, ConcurrentLinkedQueue<Connection> connectionsQueue,
                         ProtocolReader reader) {
        this.consumerQueues = consumerQueues;
        this.connectionsQueue = connectionsQueue;
        this.reader = reader;
    }

    public synchronized void deactivate() {
        loop = false;
    }

    @Override
    public void run() {
        while (loop && !Thread.interrupted()) {
            if (connectionsQueue.isEmpty()) {
                if (!sleep(20)) deactivate();
                continue;
            }
            doWork();
            sleep(10);
        }
    }

    public void doWork() {
        for (Connection connection : connectionsQueue) {
            try {
                Message message = reader.read(connection.getInputStream());
                if (message == null) continue;
                message.setReceivedAt(LocalDateTime.now());
                for (BlockingQueue<Message> queue : consumerQueues) {
                    try {
                        queue.put(message);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (IOException ex) {
                connection.setStatus(ConnectionStatus.CLOSED);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
