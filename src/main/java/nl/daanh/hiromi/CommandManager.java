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
import nl.daanh.hiromi.commands.personality.SetTimezoneCommand;
import nl.daanh.hiromi.commands.personality.TimezoneCommand;
import nl.daanh.hiromi.models.commandcontext.*;
import nl.daanh.hiromi.models.commands.IBaseCommand;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.IGenericCommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final HashMap<String, ICommand> commands = new HashMap<>();
    private final HashMap<String, ISlashCommand> slashCommands = new HashMap<>();
    private final HashMap<String, IGenericCommand> genericCommands = new HashMap<>();

    public CommandManager(IHiromiConfig config) {
        // Miscellaneous
        addCommand(new PingCommand());
        addCommand(new LoadCommand(this));
        addCommand(new SettingsCommand());
        addCommand(new StatusCommand());

        // Currency system
        addCommand(new BankCommand());
        addCommand(new BalanceCommand());

        // Personality system
        addCommand(new SetTimezoneCommand());
        addCommand(new TimezoneCommand());
    }

    private void addCommand(IBaseCommand command) {
        CommandCategory commandCategory = command.getClass().getAnnotation(CommandCategory.class);
        if (commandCategory == null)
            throw new RuntimeException("Command category is required.");
        if (command.getClass().getAnnotationsByType(SelfPermission.class).length == 0)
            throw new RuntimeException("Command self permissions are required.");

        if (commandCategory.value() == CommandCategory.CATEGORY.MUSIC && !Hiromi.getConfig().getMusicEnabled())
            return;

        if (command instanceof ICommand)
            this.addGuildCommand((ICommand) command);

        if (command instanceof ISlashCommand)
            this.addSlashCommand((ISlashCommand) command);

        if (command instanceof IGenericCommand)
            this.addGenericCommand((IGenericCommand) command);
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

    private void addGenericCommand(IGenericCommand command) {
        if (this.genericCommands.containsKey(command.getCommandDefinition().getName())) {
            throw new RuntimeException("Invoke has already been defined!");
        }

        this.genericCommands.put(command.getCommandDefinition().getName(), command);

        for (CommandInvoke annotation : command.getClass().getAnnotationsByType(CommandInvoke.class)) {
            if (this.commands.containsKey(annotation.value())) {
                continue;
            }
            this.genericCommands.put(annotation.value(), command);
        }
    }

    public List<ISlashCommand> getSlashCommands() {
        List<ISlashCommand> slashCommands = new ArrayList<>();
        this.slashCommands.forEach((s, command) -> {
            if (!slashCommands.contains(command)) slashCommands.add(command);
        });
        return slashCommands;
    }

    public List<IGenericCommand> getGenericCommands() {
        List<IGenericCommand> genericCommands = new ArrayList<>();
        this.genericCommands.forEach((s, command) -> {
            if (!genericCommands.contains(command)) genericCommands.add(command);
        });
        return genericCommands;
    }

    @Nullable
    public ICommand getCommand(String invoke) {
        return this.commands.getOrDefault(invoke.toLowerCase(), null);
    }

    @Nullable
    public ISlashCommand getSlashCommand(String invoke) {
        return this.slashCommands.getOrDefault(invoke.toLowerCase(), null);
    }

    @Nullable
    public IGenericCommand getGenericCommand(String invoke) {
        return this.genericCommands.getOrDefault(invoke.toLowerCase(), null);
    }

    private boolean cantContinue(IBaseCommand command, IBaseCommandContext ctx) {
        CommandCategory.CATEGORY category = command.getClass().getAnnotation(CommandCategory.class).value();

        if (category != CommandCategory.CATEGORY.OTHER && !Hiromi.getConfig().getDatabaseManager().getCategoryEnabled(ctx.getGuild(), category))
            return true;

        return !command.checkPermissions(ctx);
    }

    public void handle(GuildMessageReceivedEvent event, final String prefix) {
        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

        ICommand command = this.getCommand(splitMessage[0].toLowerCase());
        if (command != null) {
            ICommandContext ctx = new GuildMessageCommandContext(event, args, Hiromi.getConfig());
            if (cantContinue(command, ctx)) return;

            try {
                command.handle(ctx);
            } catch (Exception exception) {
                // TODO handle this? send the user error message?
                LOGGER.error("Something went wrong trying to handle the command.", exception);
            }

            return;
        }

        IGenericCommand genericCommand = this.getGenericCommand(splitMessage[0].toLowerCase());
        if (genericCommand == null) return;
        IGenericCommandContext ctx = new GenericCommandContext(event, Hiromi.getConfig());
        if (cantContinue(genericCommand, ctx)) return;

        try {
            genericCommand.handle(ctx);
        } catch (Exception exception) {
            // TODO handle this? send the user error message?
            LOGGER.error("Something went wrong trying to handle the command.", exception);
        }
    }

    public void handle(SlashCommandEvent event) {
        if (event.getGuild() == null) return;

        ISlashCommand slashCommand = this.getSlashCommand(event.getName());
        if (slashCommand != null) {
            ISlashCommandContext ctx = new SlashCommandContext(event, Hiromi.getConfig());
            if (cantContinue(slashCommand, ctx)) return;

            try {
                slashCommand.handle(ctx);
            } catch (Exception exception) {
                // TODO handle this? send the user error message?
                LOGGER.error("Something went wrong trying to handle the command.", exception);
            }
            return;
        }

        IGenericCommand genericCommand = this.getGenericCommand(event.getName());
        if (genericCommand == null) return;
        IGenericCommandContext ctx = new GenericCommandContext(event, Hiromi.getConfig());
        if (cantContinue(genericCommand, ctx)) return;

        try {
            genericCommand.handle(ctx);
        } catch (Exception exception) {
            // TODO handle this? send the user error message?
            LOGGER.error("Something went wrong trying to handle the command.", exception);
        }
    }

    public void handle(ButtonClickEvent event, String[] data) {
        String type = data[1];
        String subtype = data[2];
        ISlashCommand command = this.getSlashCommand(type);

        if (command == null) return;
        IButtonCommandContext ctx = new ButtonCommandContext(event, data, Hiromi.getConfig());
        if (cantContinue(command, ctx)) return;

        try {
            command.handle(subtype, ctx);
        } catch (Exception exception) {
            // TODO handle this? send the user error message?
            LOGGER.error("Something went wrong trying to handle the command.", exception);
        }
    }
}
