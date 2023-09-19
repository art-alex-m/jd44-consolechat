package ru.netology.consolechat.common;

import java.net.Socket;

public class Connection {
    private final String user;
    private final Socket socket;
    private ConnectionStatus status;


    public Connection(String user, Socket socket) {
        this.user = user;
        this.socket = socket;
        this.status = ConnectionStatus.ESTABLISHED;
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

    public synchronized Connection setStatus(ConnectionStatus status) {
        this.status = status;

        return this;
    }
}
