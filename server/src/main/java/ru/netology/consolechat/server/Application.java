package ru.netology.consolechat.server;

import ru.netology.consolechat.common.*;
import ru.netology.consolechat.common.worker.LogWorker;
import ru.netology.consolechat.common.worker.ReceiveWorker;
import ru.netology.consolechat.common.worker.SendWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Application implements Runnable {
    private final static File CONF_FILE = new File("etc/server.conf");

    @Override
    public void run() {
        /// load configuration
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONF_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Consolechat Server is starting");

        int messageQueueCapacity = Integer.parseInt(properties.getProperty("messageQueueCapacity", "100"));
        BlockingQueue<Message> senderQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        BlockingQueue<Message> loggerQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        ConcurrentLinkedQueue<Connection> connectionsQueue = new ConcurrentLinkedQueue<>();
        BlockingQueue<Socket> clientSocketQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        ProtocolWriter protocolWriter = new MessageWriter();
        ProtocolReader protocolReader = new MessageReader();

        LogWorker logWorker = new LogWorker(new File(properties.getProperty("logfile")), loggerQueue);
        SendWorker sendWorker = new SendWorker(senderQueue, connectionsQueue, protocolWriter);
        ReceiveWorker receiveWorker = new ReceiveWorker(List.of(senderQueue, loggerQueue), connectionsQueue, protocolReader);
        ServerWorker serverWorker = new ServerWorker(Integer.parseInt(properties.getProperty("port", "8088")), clientSocketQueue);
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
