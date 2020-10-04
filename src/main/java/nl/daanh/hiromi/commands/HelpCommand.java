package nl.daanh.hiromi.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromi.CommandManager;
import nl.daanh.hiromi.commands.annotations.*;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;

import java.util.List;

@CommandInvoke("help")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@CommandHelp("This command can help you use other commands!")
@CommandArgument(value = "Command Name", type = CommandArgument.TYPE.STRING)
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

        if (args.size() == 1) {
            CommandInterface command = this.commandManager.getCommand(args.get(0));

            if (command == null) {
                channel.sendMessage("I couldn't find that command. I guess this would be a 404?").queue();
                return;
            }

            StringBuilder output = new StringBuilder();

            String helpMessage = command.getClass().getAnnotation(CommandHelp.class).value();
            StringBuilder arguments = new StringBuilder();

            if (command.getClass().getAnnotationsByType(CommandArgument.class).length > 0) arguments.append("**This command takes the following arguments:**\n");
            for (CommandArgument argument : command.getClass().getAnnotationsByType(CommandArgument.class)) {
                arguments.append(String.format("  ``%s`` of the type ``%s``\n", argument.value(), argument.type()));
            }

            output.append(String.format("**Information about the command:** ``%s``\n**Description:** %s\n\n%s", args.get(0).toLowerCase(), helpMessage, arguments.toString()));

            channel.sendMessage(output.toString()).queue();
            return;
        }

        List<CommandInterface> commands = this.commandManager.getCommands();
        StringBuilder output = new StringBuilder();

        commands.forEach(commandInterface -> {
            String helpMessage = commandInterface.getClass().getAnnotation(CommandHelp.class).value();
            StringBuilder arguments = new StringBuilder();

            if (commandInterface.getClass().getAnnotationsByType(CommandArgument.class).length > 0) arguments.append(" ");
            for (CommandArgument argument : commandInterface.getClass().getAnnotationsByType(CommandArgument.class)) {
                arguments.append(String.format("<%s> (%s)", argument.value(), argument.type()));
            }

            for (CommandInvoke invoke : commandInterface.getClass().getAnnotationsByType(CommandInvoke.class)) {
                output.append(String.format("``%s`` - %s\n", invoke.value() + arguments.toString(), helpMessage));
            }
        });

        channel.sendMessage(output.toString()).queue();
    }
}
