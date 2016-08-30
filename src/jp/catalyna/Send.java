package jp.catalyna;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class Send {

    private final static String QUEUE_NAME = "hello";
    private final static int NUM_OF_MSGS = 10000;
    private final static int MAX_THREADS = 10;
    private final static int NUM_OF_CHUNK = 10;
    private final static String RMQ_HOSTNAME = "dev-mongo01.webpush.internal";

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final static Logger logger = Logger.getLogger("RBMQSend");
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    public static void main(String[] args) throws java.io.IOException {
        int numOfThreads = MAX_THREADS;
        try {
            numOfThreads = Integer.valueOf(args[0]);
        } catch (Exception e) {

        }


        log("Thread pool created with " + numOfThreads + "threads");
        //System.out.println("Thread pool created with " + numOfThreads + "threads");
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

        for (int i = 0; i < NUM_OF_CHUNK ; i++) {
            Worker worker = new Worker(NUM_OF_MSGS/NUM_OF_CHUNK);
            executor.execute(worker);
        }
        log("done");
    }

    public Send() throws java.io.IOException {
        factory = new ConnectionFactory();
        factory.setHost(RMQ_HOSTNAME);
        factory.setUsername("admin");
        factory.setPassword("admin");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    @Override
    protected void finalize() throws Throwable {
        channel.close();
        connection.close();
        super.finalize();
    }

    public void send(int n) throws java.io.IOException {
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        String message = Integer.toString(n);
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        //logger.info(" [x] Sent '" + message + "'" + " by " + Thread.currentThread().getName());
        //System.out.println(" [x] Sent '" + message + "'" + " by " + Thread.currentThread().getName());
    }

    public static void log(String str) {
        Date date = new Date();
        System.out.println(dateFormat.format(date) +":" + str);
    }
}
