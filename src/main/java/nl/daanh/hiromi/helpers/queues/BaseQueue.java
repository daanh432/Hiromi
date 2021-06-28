package nl.daanh.hiromi.helpers.queues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nl.daanh.hiromi.Main;
import nl.daanh.hiromi.helpers.queues.exceptions.HiromiBaseQueueException;
import nl.daanh.hiromi.helpers.queues.exceptions.HiromiQueueIOException;
import nl.daanh.hiromi.models.configuration.IConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.util.concurrent.TimeoutException;

public abstract class BaseQueue {
    protected final ConnectionFactory factory;
    protected static Connection connection;
    protected Channel channel;

    public BaseQueue() {
        final IConfiguration configuration = Main.getConfiguration();

        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

            SSLContext c = SSLContext.getInstance("TLSv1.3");
            c.init(null, tmf.getTrustManagers(), null);

            factory = new ConnectionFactory();
            factory.setHost(configuration.getRabbitHost());
            factory.setVirtualHost(configuration.getRabbitVirtualHost());
            factory.setUsername(configuration.getRabbitUsername());
            factory.setPassword(configuration.getRabbitPassword());
            factory.useSslProtocol(c);
            factory.enableHostnameVerification();
        } catch (Exception e) {
            throw new HiromiBaseQueueException(e.getMessage(), e);
        }
    }

    public abstract void call(String message);

    public void connect() {
        try {
            if (connection != null && connection.isOpen()) {
                channel = connection.createChannel();
                return;
            }

            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new HiromiQueueIOException(e.getMessage(), e);
        }
    }

    public void close() {
        try {
            connection.close(60);
        } catch (IOException e) {
            throw new HiromiQueueIOException(e.getMessage(), e);
        }
    }
}