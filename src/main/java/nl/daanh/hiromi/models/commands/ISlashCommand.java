package nl.daanh.hiromi.models.commands;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.models.commandcontext.IButtonCommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;

public interface ISlashCommand extends IBaseCommand {
    void handle(ISlashCommandContext ctx);

    default void handle(String subtype, IButtonCommandContext ctx) {
    }

    CommandData getCommandDefinition();

    default String getInvoke() {
        return getCommandDefinition().getName();
    }
}
