package nl.daanh.hiromi.listeners;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

public class GuildMessageListener extends ListenerAdapter {
    private final CommandManager commandManager;
    private final Dotenv dotenv = Dotenv.load();

    public GuildMessageListener(EventWaiter eventWaiter) {
        this.commandManager = new CommandManager(eventWaiter);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;

        final String prefix = DatabaseManager.instance.getPrefix(event.getGuild()) != null ? DatabaseManager.instance.getPrefix(event.getGuild()) : this.dotenv.get("GLOBAL_PREFIX");
        this.commandManager.handle(event, prefix);
    }
}
