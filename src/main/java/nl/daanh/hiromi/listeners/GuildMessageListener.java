package nl.daanh.hiromi.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.models.configuration.IConfiguration;
import org.jetbrains.annotations.NotNull;

public class GuildMessageListener extends ListenerAdapter {
    private final IConfiguration configuration;
    private final CommandManager commandManager;

    public GuildMessageListener(IConfiguration configuration, CommandManager commandManager) {
        this.configuration = configuration;
        this.commandManager = commandManager;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;

        final String prefix = this.configuration.getDatabaseManager().getPrefix(event.getGuild()) != null ? this.configuration.getDatabaseManager().getPrefix(event.getGuild()) : this.configuration.getGlobalPrefix();
        this.commandManager.handle(event, prefix);
    }
}
