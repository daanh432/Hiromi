package nl.daanh.hiromi.models.commands;

import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.models.commands.annotations.UserPermission;

public interface ICommand extends IBaseCommand {
    void handle(ICommandContext ctx);
}
