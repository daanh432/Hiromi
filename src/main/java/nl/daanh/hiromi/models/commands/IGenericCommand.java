package nl.daanh.hiromi.models.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.models.commandcontext.IGenericCommandContext;

public interface IGenericCommand extends IBaseCommand {
    void handle(IGenericCommandContext ctx);

    CommandData getCommandDefinition();
}
