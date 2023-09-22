package ru.netology.consolechat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ServerWorker implements Runnable {
    private final int port;
    private final BlockingQueue<Socket> clientSocketQueue;

    public ServerWorker(int port, BlockingQueue<Socket> clientSocketQueue) {
        this.port = port;
        this.clientSocketQueue = clientSocketQueue;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port, 50)) {
            System.out.println("Ready to accept connections on port " + port);
            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();
                clientSocketQueue.put(clientSocket);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
