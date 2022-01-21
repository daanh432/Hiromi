package nl.daanh.hiromi.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import org.jetbrains.annotations.NotNull;

public class ReadyShutdownListener extends BaseListener {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        final JDA jda = event.getJDA();
        LOGGER.info("Logged in as {} (Shard {})", jda.getSelfUser().getAsTag(), jda.getShardInfo().getShardId());
    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        final JDA jda = event.getJDA();
        LOGGER.info("Reconnected as {} (Shard {})", jda.getSelfUser().getAsTag(), jda.getShardInfo().getShardId());
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        final JDA jda = event.getJDA();
        LOGGER.info("Logged out as {} (Shard {})", jda.getSelfUser().getAsTag(), jda.getShardInfo().getShardId());
    }
}
