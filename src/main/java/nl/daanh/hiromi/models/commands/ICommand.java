package nl.daanh.hiromi.models.commands;

import nl.daanh.hiromi.models.commandcontext.ICommandContext;

public interface ICommand extends IBaseCommand {
    void handle(ICommandContext ctx);
}
