package rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import jarvis.listeners.EventConsumer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class RabbitConsumer extends DefaultConsumer {

  private EventConsumer mConsumer;
  private Channel mChannel;

  /**
   * Constructs a new instance and records its association to the passed-in channel.
   *
   * @param channel the channel to which this consumer is attached
   */
  public RabbitConsumer(Channel channel, EventConsumer consumer) {
    super(channel);
    mConsumer = consumer;
    mChannel = channel;
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope,
      AMQP.BasicProperties properties, byte[] body) {
    try {
      String message = new String(body, "UTF-8");
      mConsumer.consume(message);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
