package nl.daanh.hiromi.listeners;

import com.google.protobuf.InvalidProtocolBufferException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.helpers.MessageQueue;
import nl.daanh.hiromi.models.configuration.IConfiguration;
import nl.daanh.hiromi.models.proto.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildMessageListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMessageListener.class);
    private final IConfiguration configuration;
    private final CommandManager commandManager;
    private MessageQueue[] messageQueues;

    public GuildMessageListener(IConfiguration configuration, CommandManager commandManager) {
        this.configuration = configuration;
        this.commandManager = commandManager;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (messageQueues == null) {
            LOGGER.debug("Creating message queue array");
            final int shardsTotal = event.getJDA().getShardManager().getShardsTotal();
            messageQueues = new MessageQueue[shardsTotal];
        }
        final int shardId = event.getJDA().getShardInfo().getShardId();
        if (messageQueues[shardId] == null) {
            LOGGER.debug("Creating message queue for shard: " + shardId);
            messageQueues[shardId] = new MessageQueue();
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;
        MessageQueue messageQueue = messageQueues[event.getJDA().getShardInfo().getShardId()];
        messageQueue.callRPC(event, (consumerTag, delivery) -> {
            try {
                Response response = Response.parseFrom(delivery.getBody());

                final Guild guild = event.getJDA().getGuildById(response.getGuildId());
                if (guild != null) {
                    final TextChannel channel = guild.getTextChannelById(response.getChannelId());
                    if (channel != null) {
                        channel.sendMessage(response.getResponseMessage()).queue();
                    }
                }
            } catch (InvalidProtocolBufferException ignore) {
                // ignore
            }
        });

        final String prefix = this.configuration.getDatabaseManager().getPrefix(event.getGuild()) != null ? this.configuration.getDatabaseManager().getPrefix(event.getGuild()) : this.configuration.getGlobalPrefix();
        this.commandManager.handle(event, prefix);
    }
}
