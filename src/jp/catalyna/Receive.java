package jp.catalyna;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ishida on 2016/08/24.
 */
public class Receive {
    private final static String QUEUE_NAME = "hello";
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    private final static String RMQ_HOSTNAME = "dev-mongo01.webpush.internal";

    private final static int MAX_THREADS = 10;

    public static void main(String[] argv)
            throws java.io.IOException,
            java.lang.InterruptedException {

        int numOfThreads = MAX_THREADS;
        try {
            numOfThreads = Integer.valueOf(argv[0]);
        } catch (Exception e) {

        }

        log("Thread pool created with " + numOfThreads + "threads");
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RMQ_HOSTNAME);
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection(executor);
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                log("Received '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public static void log(String str) {
        Date date = new Date();
        System.out.println(dateFormat.format(date) +":" + str);
    }
}
