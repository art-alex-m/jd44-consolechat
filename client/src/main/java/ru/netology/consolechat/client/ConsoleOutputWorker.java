package ru.netology.consolechat.client;

import ru.netology.consolechat.common.Message;

import java.util.concurrent.BlockingQueue;

public class ConsoleOutputWorker implements Runnable {

    private final BlockingQueue<Message> messages;

    public ConsoleOutputWorker(BlockingQueue<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            doWork();
        }
    }

    public void doWork() {
        try {
            Message message = messages.take();
            System.out.print(messageToScreen(message));
        } catch (InterruptedException ignored) {
        }
    }

    private String messageToScreen(Message message) {
        return String.format("\r%s [%s]%n%s%n", message.getUser(), message.getCreatedAt(), message.getContent());
    }
}
