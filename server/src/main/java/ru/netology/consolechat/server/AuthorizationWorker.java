package ru.netology.consolechat.server;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;
import ru.netology.consolechat.common.Message;
import ru.netology.consolechat.common.ProtocolReader;

import java.io.DataInputStream;
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
        while (!Thread.interrupted()) {
            Socket clientSocket;
            try {
                clientSocket = clientSocketQueue.take();
            } catch (InterruptedException e) {
                continue;
            }
            System.out.println("New client socket received");
            Message message = null;
            int timeout = 0;
            while (message == null && timeout < 1000) {
                try {
                    message = protocolReader.read(new DataInputStream(clientSocket.getInputStream()));
                } catch (IOException | NullPointerException e) {
                    closeSocket(clientSocket);
                    break;
                } catch (ClassNotFoundException e) {
                    closeSocket(clientSocket);
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
                timeout += 10;
            }

            if (message == null) {
                closeSocket(clientSocket);
                continue;
            }

            try {
                Connection connection = new Connection(message.getContent(), clientSocket);
                connection.setStatus(ConnectionStatus.AUTHORIZED);
                connectionsQueue.add(connection);
            } catch (IOException e) {
                closeSocket(clientSocket);
            }
        }
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {

        }
    }
}
