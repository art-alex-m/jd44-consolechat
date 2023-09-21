package ru.netology.consolechat.server;

import ru.netology.consolechat.common.*;
import ru.netology.consolechat.common.worker.LogWorker;
import ru.netology.consolechat.common.worker.ReceiveWorker;
import ru.netology.consolechat.common.worker.SendWorker;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Application implements Runnable {
    @Override
    public void run() {
        int port = 8080;
        int messageQueueCapacity = 1000;
        File logfile = new File("messages-server-log.txt");


        BlockingQueue<Message> senderQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        BlockingQueue<Message> loggerQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        ConcurrentLinkedQueue<Connection> connectionsQueue = new ConcurrentLinkedQueue<>();
        BlockingQueue<Socket> clientSocketQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        ProtocolWriter protocolWriter;
        ProtocolReader protocolReader;
        try {
            protocolWriter = new MessageWriter();
            protocolReader = new MessageReader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LogWorker logWorker = new LogWorker(logfile, loggerQueue);
        SendWorker sendWorker = new SendWorker(senderQueue, connectionsQueue, protocolWriter);
        ReceiveWorker receiveWorker = new ReceiveWorker(List.of(senderQueue, loggerQueue), connectionsQueue, protocolReader);
        ServerWorker serverWorker = new ServerWorker(port, clientSocketQueue);
        AuthorizationWorker authorizationWorker = new AuthorizationWorker(connectionsQueue, clientSocketQueue, protocolReader);
        CleaningWorker cleaningWorker = new CleaningWorker(connectionsQueue);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(cleaningWorker, 200, 200, TimeUnit.MILLISECONDS);
        Stream.of(logWorker, sendWorker, receiveWorker, serverWorker, authorizationWorker).forEach(executorService::submit);

        while (!Thread.interrupted()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
