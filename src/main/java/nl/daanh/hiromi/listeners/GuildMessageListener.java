package nl.daanh.hiromi.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.Hiromi;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildMessageListener extends BaseListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMessageListener.class);
    private final CommandManager commandManager;

    public GuildMessageListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!shouldAct(event.getAuthor())) return;
        // TODO Implement received logging

        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;
        final IHiromiConfig config = Hiromi.getConfig();
        final IDatabaseManager databaseManager = config.getDatabaseManager();

        final String prefix = databaseManager.getPrefix(event.getGuild()) != null ? databaseManager.getPrefix(event.getGuild()) : config.getGlobalPrefix();
        this.commandManager.handle(event, prefix);
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        if (!shouldAct(event.getAuthor())) return;
        // TODO Implement update logging
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        // TODO Implement delete logging
    }
}
