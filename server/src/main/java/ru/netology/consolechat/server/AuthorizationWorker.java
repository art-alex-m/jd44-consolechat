package ru.netology.consolechat.server;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;
import ru.netology.consolechat.common.Message;
import ru.netology.consolechat.common.ProtocolReader;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AuthorizationWorker implements Runnable {
    private final ConcurrentLinkedQueue<Connection> connectionsQueue;
    private final BlockingQueue<Socket> clientSocketQueue;
    private final ProtocolReader protocolReader;

    public AuthorizationWorker(ConcurrentLinkedQueue<Connection> connectionsQueue, BlockingQueue<Socket> clientSocketQueue,
                               ProtocolReader protocolReader) {
        this.connectionsQueue = connectionsQueue;
        this.clientSocketQueue = clientSocketQueue;
        this.protocolReader = protocolReader;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            Socket clentSocket;
            try {
                clentSocket = clientSocketQueue.take();
            } catch (InterruptedException e) {
                break;
            }
            Message message = null;
            try {
                int timeout = 0;
                while (message == null && timeout < 500) {
                    message = protocolReader.read(clentSocket.getInputStream());
                    Thread.sleep(10);
                    timeout += 10;
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                break;
            }

            if (message == null) {
                try {
                    clentSocket.close();
                } catch (IOException ignored) {
                }
                continue;
            }

            Connection connection = new Connection(message.getContent(), clentSocket);
            connection.setStatus(ConnectionStatus.AUTHORIZED);
            connectionsQueue.add(connection);
        }
    }
}
