package nl.daanh.hiromi.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseListener extends ListenerAdapter {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseListener.class);
    protected final ExecutorService handlerThread = Executors.newCachedThreadPool((r) -> {
        final Thread thread = new Thread(r, "Listener-handle-thread");
        thread.setDaemon(true);
        return thread;
    });

    protected boolean shouldAct(@NotNull User event) {
        return !event.isBot() && !event.isSystem();
    }
}
