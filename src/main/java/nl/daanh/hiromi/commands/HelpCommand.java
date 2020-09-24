package nl.daanh.hiromi.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;

import java.util.List;

@CommandInvoke("help")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@CommandHelp("This command can help you use other commands!")
@SelfPermission(Permission.MESSAGE_WRITE)
public class HelpCommand implements CommandInterface {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        // TODO Implement help and argument autofill with annotations

        List<CommandInterface> commands = this.commandManager.getCommands();
        StringBuilder output = new StringBuilder();

        commands.forEach(commandInterface -> {
            for (CommandInvoke invoke : commandInterface.getClass().getAnnotationsByType(CommandInvoke.class)) {
                output.append(invoke.value()).append(" - ").append(commandInterface.getClass().getAnnotation(CommandHelp.class).value()).append("\n");
            }
        });

        channel.sendMessage(output.toString()).queue();
    }
}
