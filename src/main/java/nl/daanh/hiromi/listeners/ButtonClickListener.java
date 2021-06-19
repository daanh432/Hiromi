package nl.daanh.hiromi.listeners;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromi.CommandManager;
import org.jetbrains.annotations.NotNull;

public class ButtonClickListener extends ListenerAdapter {
    private final CommandManager commandManager;

    public ButtonClickListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        this.commandManager.handle(event);
    }
}
