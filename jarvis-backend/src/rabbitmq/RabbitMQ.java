package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jarvis.util.AdminAlertUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    private static RabbitMQ instance;

    private Connection mConnection;

    private RabbitMQ() {}

    public static RabbitMQ getInstance() {
        if(instance == null) {
            instance = new RabbitMQ();
        }
        return instance;
    }

    public boolean init(String host, String username, String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        try {
            mConnection = factory.newConnection();
        } catch (IOException e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean finish() {
        if(mConnection != null) {
            try {
                mConnection.close();
                return true;
            } catch (IOException e) {
                AdminAlertUtil.alertUnexpectedException(e);
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean sendMessage(String queue, String message) {
        try {
            Channel channel = mConnection.createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes());
            channel.close();
        } catch (IOException e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
