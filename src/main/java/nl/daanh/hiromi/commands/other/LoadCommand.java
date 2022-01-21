package nl.daanh.hiromi.commands.other;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.models.commands.annotations.UserPermission;

import java.util.List;
import java.util.stream.Collectors;

@CommandInvoke("load")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@SelfPermission(Permission.USE_SLASH_COMMANDS)
@UserPermission(Permission.ADMINISTRATOR)
public class LoadCommand implements ICommand {
    private final CommandManager commandManager;

    public LoadCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void handle(ICommandContext ctx) {
        try {
            CommandListUpdateAction commands = ctx.getGuild().updateCommands();
            final List<CommandData> newCommands = commandManager.getSlashCommands().stream().map(ISlashCommand::getCommandDefinition).collect(Collectors.toList());
            commands.addCommands(newCommands).queue();
            ctx.replyInstant("Commands loaded");
        } catch (Exception exception) {
            ctx.replyInstant("I failed to create load the commands for this server. Please try reinviting me!");
        }
    }
}
