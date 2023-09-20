package ru.netology.consolechat.common.worker;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;
import ru.netology.consolechat.common.Message;
import ru.netology.consolechat.common.ProtocolReader;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiveWorker implements Runnable {
    private final List<BlockingQueue<Message>> consumerQueue;
    private final ConcurrentLinkedQueue<Connection> connectionsQueue;
    private final ProtocolReader reader;

    public ReceiveWorker(List<BlockingQueue<Message>> consumerQueue, ConcurrentLinkedQueue<Connection> connectionsQueue,
                         ProtocolReader reader) {
        this.consumerQueue = consumerQueue;
        this.connectionsQueue = connectionsQueue;
        this.reader = reader;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            for (Connection connection : connectionsQueue) {
                try {
                    Message message = reader.read(connection.getSocket().getInputStream());
                    if (message == null) continue;
                    for (BlockingQueue<Message> queue : consumerQueue) {
                        try {
                            queue.put(message);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } catch (IOException ex) {
                    connection.setStatus(ConnectionStatus.CLOSED);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
