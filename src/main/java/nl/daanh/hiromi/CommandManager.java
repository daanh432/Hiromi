package nl.daanh.hiromi;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromi.commands.currency.BalanceCommand;
import nl.daanh.hiromi.commands.currency.BankCommand;
import nl.daanh.hiromi.commands.other.LoadCommand;
import nl.daanh.hiromi.commands.other.PingCommand;
import nl.daanh.hiromi.commands.other.SettingsCommand;
import nl.daanh.hiromi.commands.other.StatusCommand;
import nl.daanh.hiromi.models.commandcontext.*;
import nl.daanh.hiromi.models.commands.IBaseCommand;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.models.configuration.IConfiguration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final IConfiguration configuration;
    private final HashMap<String, ICommand> commands = new HashMap<>();
    private final HashMap<String, ISlashCommand> slashCommands = new HashMap<>();

    public CommandManager(IConfiguration configuration) {
        this.configuration = configuration;

        // Miscellaneous
        addCommand(new PingCommand());
        addCommand(new LoadCommand(this));
        addCommand(new SettingsCommand());
        addCommand(new StatusCommand());

        // Currency system
        addCommand(new BankCommand());
        addCommand(new BalanceCommand());
    }

    private void addCommand(IBaseCommand command) {
        CommandCategory commandCategory = command.getClass().getAnnotation(CommandCategory.class);
        if (commandCategory == null)
            throw new RuntimeException("Command category is required.");
        if (command.getClass().getAnnotationsByType(SelfPermission.class).length == 0)
            throw new RuntimeException("Command self permissions are required.");

        if (commandCategory.value() == CommandCategory.CATEGORY.MUSIC && !configuration.getMusicEnabled())
            return;

        if (command instanceof ICommand)
            this.addGuildCommand((ICommand) command);

        if (command instanceof ISlashCommand)
            this.addSlashCommand((ISlashCommand) command);
    }

    private void addGuildCommand(ICommand command) {
        if (command.getClass().getAnnotationsByType(CommandInvoke.class).length == 0)
            throw new RuntimeException("Command invoke(s) are required.");

        for (CommandInvoke annotation : command.getClass().getAnnotationsByType(CommandInvoke.class)) {
            if (this.commands.containsKey(annotation.value())) {
                throw new RuntimeException("Invoke has already been defined!");
            }
            this.commands.put(annotation.value(), command);
        }
    }

    private void addSlashCommand(ISlashCommand command) {
        if (this.slashCommands.containsKey(command.getInvoke())) {
            throw new RuntimeException("Invoke has already been defined!");
        }
        this.slashCommands.put(command.getInvoke(), command);
    }

    public List<ICommand> getCommands() {
        List<ICommand> commands = new ArrayList<>();
        this.commands.forEach((s, command) -> {
            if (!commands.contains(command)) commands.add(command);
        });
        return commands;
    }

    public List<ISlashCommand> getSlashCommands() {
        List<ISlashCommand> slashCommands = new ArrayList<>();
        this.slashCommands.forEach((s, command) -> {
            if (!slashCommands.contains(command)) slashCommands.add(command);
        });
        return slashCommands;
    }

    @Nullable
    public ICommand getCommand(String invoke) {
        String invokeLowerCase = invoke.toLowerCase();

        return this.commands.getOrDefault(invokeLowerCase, null);
    }

    @Nullable
    public ISlashCommand getSlashCommand(String invoke) {
        String invokeLowerCase = invoke.toLowerCase();

        return this.slashCommands.getOrDefault(invokeLowerCase, null);
    }

    private boolean cantContinue(IBaseCommand command, IBaseCommandContext ctx) {
        CommandCategory.CATEGORY category = command.getClass().getAnnotation(CommandCategory.class).value();

        if (category != CommandCategory.CATEGORY.OTHER && !this.configuration.getDatabaseManager().getCategoryEnabled(ctx.getGuild(), category))
            return true;

        return !command.checkPermissions(ctx);
    }

    public void handle(GuildMessageReceivedEvent event, final String prefix) {
        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

        ICommand command = this.getCommand(splitMessage[0].toLowerCase());

        if (command == null) return;

        ICommandContext ctx = new GuildMessageCommandContext(event, args, this.configuration);

        if (cantContinue(command, ctx)) return;

        try {
            command.handle(ctx);
        } catch (Exception exception) {
            // TODO handle this? send the user error message?
            exception.printStackTrace();
            event.getChannel().sendMessage(
                    String.format("Oops, it looks like something went wrong...\n*%s: %s*",
                            exception.getClass(),
                            exception.getMessage()
                    )
            ).queue();
        }
    }

    public void handle(SlashCommandEvent event) {
        ISlashCommand command = this.getSlashCommand(event.getName());

        if (command == null) return;

        ISlashCommandContext ctx = new SlashCommandContext(event, this.configuration);

        if (cantContinue(command, ctx)) return;

        try {
            command.handle(ctx);
        } catch (Exception exception) {
            // TODO handle this? send the user error message?
            exception.printStackTrace();
            event.getTextChannel().sendMessage(
                    String.format("Oops, it looks like something went wrong...\n*%s: %s*",
                            exception.getClass(),
                            exception.getMessage()
                    )
            ).queue();
        }
    }

    public void handle(ButtonClickEvent event, String[] data) {
        String type = data[1];
        String subtype = data[2];
        ISlashCommand command = this.getSlashCommand(type);

        if (command == null) return;

        IButtonCommandContext ctx = new ButtonCommandContext(event, data, this.configuration);

        if (cantContinue(command, ctx)) return;

        try {
            command.handle(subtype, ctx);
        } catch (Exception exception) {
            // TODO handle this? send the user error message?
            exception.printStackTrace();
            event.getTextChannel().sendMessage(
                    String.format("Oops, it looks like something went wrong...\n*%s: %s*",
                            exception.getClass(),
                            exception.getMessage()
                    )
            ).queue();
        }
    }
}
