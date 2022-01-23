package nl.daanh.hiromi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.listeners.GuildMessageListener;
import nl.daanh.hiromi.listeners.ReadyShutdownListener;
import nl.daanh.hiromi.listeners.SlashCommandListener;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager implements IEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    private final List<EventListener> listeners = new ArrayList<>();

    private final ExecutorService eventExecutor = Executors.newSingleThreadExecutor((r) -> {
        final Thread thread = new Thread(r, "Hiromi-Event-Thread");
        thread.setDaemon(true);

        return thread;
    });

    public EventManager(IHiromiConfig config) {
        final CommandManager commandManager = new CommandManager(config);
        this.listeners.add(new ReadyShutdownListener());
        this.listeners.add(new GuildMessageListener(commandManager));
        this.listeners.add(new SlashCommandListener(commandManager));
    }

    @Override
    public void register(@NotNull Object listener) {
        throw new IllegalArgumentException();
    }

    @Override
    public void unregister(@NotNull Object listener) {
        throw new IllegalArgumentException();
    }

    @Override
    public void handle(@NotNull GenericEvent event) {
//        final JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        for (final EventListener listener : this.listeners) {
            eventExecutor.submit(() -> {
                try {
                    listener.onEvent(event);
                } catch (Throwable thr) {
                    LOGGER.error(String.format("Error while handling event at %s(%s); %s",
                            event.getClass().getName(),
                            listener.getClass().getSimpleName(),
                            thr.getMessage()
                    ), thr);
                }
            });
        }
    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    public void shutdown() {
        final ShardManager shardManager = Hiromi.getShardManager();
        LOGGER.warn("Shutting down by request!");
        for (JDA jda : shardManager.getShards()) {
            jda.shutdown();
        }

        shardManager.shutdown();
    }

    public void restart(int shardId) {
        final ShardManager shardManager = Hiromi.getShardManager();
        LOGGER.warn("Restarting shard {}, exiting!", shardId);
        shardManager.shutdown(shardId);
        shardManager.start(shardId);
        LOGGER.warn("Restarting shard {}, started!", shardId);
    }
}
