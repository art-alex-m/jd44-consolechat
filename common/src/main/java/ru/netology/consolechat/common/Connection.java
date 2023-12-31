package ru.netology.consolechat.common;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements Closeable {
    private final String user;
    private final Socket socket;
    private ConnectionStatus status;

    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;


    public Connection(String user, Socket socket) throws IOException {
        this.user = user;
        this.socket = socket;
        this.status = ConnectionStatus.ESTABLISHED;
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    public String getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    @Override
    public synchronized void close() throws IOException {
        socket.close();
    }

    public boolean isClosed() {
        return status == ConnectionStatus.CLOSED;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }
}
