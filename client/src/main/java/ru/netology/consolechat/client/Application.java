package ru.netology.consolechat.client;

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
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Application implements Runnable {
    private final static String EXIT_MESSAGE = "/exit";
    private final static File CONF_FILE = new File("etc/client.conf");

    @Override
    public void run() {
        /// load configuration
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONF_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /// greeting user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в чат");
        System.out.println("Введите имя пользователя");
        String name = scanner.nextLine();
        Message greetingMessage = new Message(name, name);

        /// start application workers
        int messageQueueCapacity = Integer.parseInt(properties.getProperty("messageQueueCapacity"));
        BlockingQueue<Message> senderQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        BlockingQueue<Message> loggerQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        BlockingQueue<Message> consoleQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
        ConcurrentLinkedQueue<Connection> connectionsQueue = new ConcurrentLinkedQueue<>();
        ProtocolWriter protocolWriter = new MessageWriter();
        ProtocolReader protocolReader = new MessageReader();

        LogWorker logWorker = new LogWorker(new File(properties.getProperty("logfile")), loggerQueue);
        SendWorker sendWorker = new SendWorker(senderQueue, connectionsQueue, protocolWriter);
        ReceiveWorker receiveWorker = new ReceiveWorker(List.of(loggerQueue, consoleQueue), connectionsQueue, protocolReader);
        ConsoleOutputWorker consoleOutputWorker = new ConsoleOutputWorker(consoleQueue);

        ExecutorService executorService = Executors.newWorkStealingPool();
        Stream.of(logWorker, sendWorker, receiveWorker, consoleOutputWorker).forEach(executorService::submit);

        /// send greeting message to server
        Connection connection;
        try {
            Socket clientSocket = new Socket(properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
            connection = new Connection(name, clientSocket);
            connectionsQueue.add(connection);
            senderQueue.put(greetingMessage);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        /// start listening new messages from user
        System.out.println("Вводите новые сообщения");
        while (!Thread.interrupted()) {
            try {
                String content = scanner.nextLine();
                if (connection.isClosed()) {
                    System.out.println("Соединение закрыто сервером");
                    break;
                }
                if (EXIT_MESSAGE.equals(content)) {
                    System.out.println("Выход из программы");
                    break;
                }
                senderQueue.put(new Message(content, connection.getUser()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ///stop application
        receiveWorker.deactivate();
        try {
            connection.close();
        } catch (IOException ignored) {
        }
        while (!loggerQueue.isEmpty()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
