package ru.netology.consolechat.server;

import ru.netology.consolechat.common.Connection;
import ru.netology.consolechat.common.ConnectionStatus;
import ru.netology.consolechat.common.Message;
import ru.netology.consolechat.common.ProtocolReader;
import ru.netology.consolechat.common.worker.Sleepable;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AuthorizationWorker implements Sleepable, Runnable {
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
            doWork();
        }
    }

    public void doWork() {
        Socket clientSocket;
        try {
            clientSocket = clientSocketQueue.take();
        } catch (InterruptedException e) {
            return;
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
            sleep(10);
            timeout += 10;
        }

        if (message == null) {
            closeSocket(clientSocket);
            return;
        }

        try {
            Connection connection = new Connection(message.getContent(), clientSocket);
            connection.setStatus(ConnectionStatus.AUTHORIZED);
            connectionsQueue.add(connection);
            System.out.printf("Client %s:%s authorized%n", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
        } catch (IOException e) {
            closeSocket(clientSocket);
        }
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {

        }
    }
}
