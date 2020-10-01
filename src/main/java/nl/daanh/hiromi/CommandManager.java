package nl.daanh.hiromi;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromi.commands.HelpCommand;
import nl.daanh.hiromi.commands.PingCommand;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;
import nl.daanh.hiromi.commands.music.JoinCommand;
import nl.daanh.hiromi.commands.music.PauseCommand;
import nl.daanh.hiromi.commands.music.PlayCommand;
import nl.daanh.hiromi.commands.music.ResumeCommand;
import nl.daanh.hiromi.database.DatabaseManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final HashMap<String, CommandInterface> commands = new HashMap<>();

    public CommandManager() {
        this.addCommand(new PingCommand());
        this.addCommand(new HelpCommand(this));
        this.addCommand(new PlayCommand());
        this.addCommand(new JoinCommand());
        this.addCommand(new PauseCommand());
        this.addCommand(new ResumeCommand());
    }

    private void addCommand(CommandInterface command) {
        if (command.getClass().getAnnotation(CommandCategory.class) == null) throw new RuntimeException("Command category is required.");
        if (command.getClass().getAnnotation(CommandHelp.class) == null) throw new RuntimeException("Command help is required.");
        if (command.getClass().getAnnotationsByType(SelfPermission.class).length == 0) throw new RuntimeException("Command self permissions are required.");

        for (CommandInvoke annotation : command.getClass().getAnnotationsByType(CommandInvoke.class)) {
            if (this.commands.containsKey(annotation.value())) {
                throw new RuntimeException("Invoke has already been defined!");
            }
            this.commands.put(annotation.value(), command);
        }
    }

    public List<CommandInterface> getCommands() {
        List<CommandInterface> commands = new ArrayList<>();
        this.commands.forEach((s, commandInterface) -> {
            if (!commands.contains(commandInterface)) commands.add(commandInterface);
        });
        return commands;
    }

    @Nullable
    public CommandInterface getCommand(String invoke) {
        String invokeLowerCase = invoke.toLowerCase();

        return this.commands.getOrDefault(invokeLowerCase, null);
    }

    public void handle(GuildMessageReceivedEvent event, final String prefix) {
        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

        CommandInterface command = this.getCommand(splitMessage[0].toLowerCase());

        if (command == null) return;

        CommandContext ctx = new CommandContext(event, args);

        boolean enabled = DatabaseManager.instance.getEnabledCategories(event.getGuild())
                .stream()
                .anyMatch((category -> category == command.getClass().getAnnotation(CommandCategory.class).value()));

        if (!enabled) return;

        boolean hasPermissions = command.checkPermissions(ctx);

        if (!hasPermissions) return;

        try {
            command.handle(ctx);
        } catch (Exception exception) {
            // handle this? send the user error message?
            event.getChannel().sendMessage(
                    String.format("Oops, it looks like something went wrong...\n*%s: %s*",
                            exception.getClass(),
                            exception.getMessage()
                    )
            ).queue();
        }
    }

}
