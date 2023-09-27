package ru.netology.consolechat.server;

import ru.netology.consolechat.common.*;
import ru.netology.consolechat.common.worker.LogWorker;
import ru.netology.consolechat.common.worker.ReceiveWorker;
import ru.netology.consolechat.common.worker.SendWorker;
import ru.netology.consolechat.common.worker.Sleepable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsolechatServer implements Sleepable, Runnable {
    private final static int SLEEP_TIME = 200;
    private final static int CLEANING_SCHEDULE_DELAY = 200;
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

        /// init server application
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
        IdentificationWorker identificationWorker = new IdentificationWorker(connectionsQueue, clientSocketQueue, protocolReader);
        CleaningWorker cleaningWorker = new CleaningWorker(connectionsQueue);

        List<Runnable> workers = List.of(logWorker, sendWorker, receiveWorker, serverWorker, identificationWorker);
        ExecutorService executorService = Executors.newFixedThreadPool(workers.size());
        workers.forEach(executorService::submit);
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(cleaningWorker, CLEANING_SCHEDULE_DELAY, CLEANING_SCHEDULE_DELAY, TimeUnit.MILLISECONDS);

        while (!Thread.interrupted() && sleep(SLEEP_TIME)) ;
    }
}
