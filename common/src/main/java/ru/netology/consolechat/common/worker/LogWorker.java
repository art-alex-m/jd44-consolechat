package ru.netology.consolechat.common.worker;

import ru.netology.consolechat.common.Message;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class LogWorker implements Runnable {
    private final File logfile;

    private final BlockingQueue<Message> messages;

    public LogWorker(File logfile, BlockingQueue<Message> messages) {
        this.logfile = logfile;
        this.messages = messages;
    }

    @Override
    public void run() {
        try(FileWriter out = new FileWriter(logfile, true)) {
            while (!Thread.interrupted()) {
                try {
                    Message message = messages.poll(10, TimeUnit.MILLISECONDS);
                    if (message == null) continue;
                    out.write(messageToString(message));
                } catch (InterruptedException e) {
                    return;
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