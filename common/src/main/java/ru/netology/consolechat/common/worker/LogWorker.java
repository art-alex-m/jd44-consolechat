package ru.netology.consolechat.common.worker;

import ru.netology.consolechat.common.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class LogWorker implements Runnable {
    private final static int BUFFER_SIZE = 10;
    private final File logfile;

    private final BlockingQueue<Message> messages;

    public LogWorker(File logfile, BlockingQueue<Message> messages) {
        this.logfile = logfile;
        this.messages = messages;
    }

    @Override
    public void run() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(logfile, true), BUFFER_SIZE)) {
            while (!Thread.interrupted()) {
                try {
                    Message message = messages.take();
                    out.write(messageToString(message));
                    if (messages.isEmpty()) {
                        out.flush();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String messageToString(Message message) {
        return String.format("%s [%s] [%s] %s%n",
                message.getUser(), message.getCreatedAt(), message.getReceivedAt(), message.getContent());
    }
}
