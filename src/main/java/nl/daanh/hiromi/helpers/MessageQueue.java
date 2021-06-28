package nl.daanh.hiromi.helpers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromi.helpers.queues.BaseQueue;
import nl.daanh.hiromi.helpers.queues.exceptions.HiromiQueueIOException;
import nl.daanh.hiromi.models.proto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MessageQueue extends BaseQueue {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MessageQueue.class);
    protected final String QUEUE_NAME = "messages";
    protected final String REPLY_QUEUE_NAME = "messages-reply";

    public MessageQueue() {
        super();
        connect();
    }

    private byte[] guildMessageToByte(GuildMessageReceivedEvent event) {
        if (event.getMember() == null) throw new RuntimeException("Member is null somehow");

        final Message message = Message.newBuilder()
                .setMessage(event.getMessage().getContentRaw())
                .setUsername(event.getMember().getEffectiveName())
                .setUserId(event.getMember().getId())
                .setChannelId(event.getChannel().getId())
                .setGuildId(event.getGuild().getId())
                .build();

        return message.toByteArray();
    }

    public void call(String message) {
        if (!channel.isOpen()) connect();

        try {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new HiromiQueueIOException(e.getMessage(), e);
        }
    }

    public void callRPC(GuildMessageReceivedEvent event, DeliverCallback callback) {
        if (!channel.isOpen()) connect();

        try {
            final String corrId = UUID.randomUUID().toString();

            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(REPLY_QUEUE_NAME)
                    .build();

            channel.basicPublish("", QUEUE_NAME, props, guildMessageToByte(event));

            channel.basicConsume(REPLY_QUEUE_NAME, true, (consumerTag, delivery) -> {
                callback.handle(consumerTag, delivery);
                channel.basicCancel(consumerTag);
            }, consumerTag -> {
                LOGGER.debug(String.format("Consumer %s cancelled", consumerTag));
            });
        } catch (IOException e) {
            throw new HiromiQueueIOException(e.getMessage(), e);
        }
    }
}
