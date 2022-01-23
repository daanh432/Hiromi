package nl.daanh.hiromi.commands.other;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.exceptions.HiromiSettingNotFoundException;
import nl.daanh.hiromi.models.commandcontext.IButtonCommandContext;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.models.commands.annotations.UserPermission;
import nl.daanh.hiromi.utils.MessageFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandInvoke("settings")
@CommandInvoke("setting")
@CommandInvoke("options")
@CommandInvoke("option")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@SelfPermission(Permission.MESSAGE_WRITE)
@UserPermission(Permission.ADMINISTRATOR)
public class SettingsCommand implements ICommand, ISlashCommand {
    final Logger LOGGER = LoggerFactory.getLogger(SettingsCommand.class);

    private static class SettingInformation {
        public SettingInformation(String name, String description, OptionType type) {
            this.Name = name;
            this.Description = description;
            this.Type = type;
        }

        public String Name;
        public String Description;
        public OptionType Type;
    }

    private static final List<SettingInformation> validSettings = Arrays.asList(
            new SettingInformation(
                    "prefix",
                    "Determines which prefix to listen to for legacy non slash commands. (Will be removed in the future)",
                    OptionType.STRING
            ),

            new SettingInformation(
                    "currency",
                    "Which currency should the server use? (examples: Ƕ, €, $, ¥)",
                    OptionType.STRING
            ),

            new SettingInformation(
                    "fun",
                    "Should all features regarding games / fun / currency be enabled on this server?",
                    OptionType.BOOLEAN
            ),

            new SettingInformation(
                    "music",
                    "Should all features regarding music be enabled on this server?",
                    OptionType.BOOLEAN
            ),

            new SettingInformation(
                    "moderation",
                    "Should all features regarding moderation be enabled on this server?",
                    OptionType.BOOLEAN
            ),

            new SettingInformation(
                    "personality",
                    "Should all features regarding personality be enabled on this server?",
                    OptionType.BOOLEAN
            ),

            new SettingInformation(
                    "leveling",
                    "Should all features regarding leveling be enabled on this server?",
                    OptionType.BOOLEAN
            ),

            new SettingInformation(
                    "emoji",
                    "Should all features regarding emojis be enabled on this server?",
                    OptionType.BOOLEAN
            )
    );

    private String formatCategory(String key, boolean value, boolean updated) {
        if (updated)
            return String.format("The category %s has been %s", key, value ? "enabled" : "disabled");

        return String.format("The category %s is currently %s", key, value ? "enabled" : "disabled");
    }

    private String format(String key, boolean value, boolean updated) {
        if (updated)
            return String.format("%s has been %s", key, value ? "enabled" : "disabled");

        return String.format("%s is currently %s", key, value ? "enabled" : "disabled");
    }

    private String format(String key, String value, boolean updated) {
        if (updated)
            return String.format("%s has been set to %s", key, value);

        return String.format("%s is currently set to %s", key, value);
    }

    private String get(Guild guild, IDatabaseManager databaseManager, SettingInformation setting, String key) {
        return get(guild, databaseManager, setting, key, false);
    }

    private String get(Guild guild, IDatabaseManager databaseManager, SettingInformation setting, String key, boolean updated) {
        switch (key) {
            case "prefix":
                return format(setting.Name, databaseManager.getPrefix(guild), updated);
            case "currency":
                return format(setting.Name, databaseManager.getCurrency(guild), updated);
            case "fun":
                return formatCategory(setting.Name, databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.FUN), updated);
            case "music":
                return formatCategory(setting.Name, databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC), updated);
            case "moderation":
                return formatCategory(setting.Name, databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION), updated);
            case "personality":
                return formatCategory(setting.Name, databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY), updated);
            case "leveling":
                return formatCategory(setting.Name, databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING), updated);
            case "emoji":
                return formatCategory(setting.Name, databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI), updated);
            default:
                throw new HiromiSettingNotFoundException(String.format("The setting %s was not found", key));
        }
    }

    private String set(Guild guild, IDatabaseManager databaseManager, SettingInformation setting, String key, String value) {
        switch (key) {
            case "prefix":
                databaseManager.setPrefix(guild, value);
                break;
            case "currency":
                databaseManager.setCurrency(guild, value);
                break;
            case "fun":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.FUN, MessageFormatting.mapStringToBool(value));
                break;
            case "music":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC, MessageFormatting.mapStringToBool(value));
                break;
            case "moderation":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION, MessageFormatting.mapStringToBool(value));
                break;
            case "personality":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY, MessageFormatting.mapStringToBool(value));
                break;
            case "leveling":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING, MessageFormatting.mapStringToBool(value));
                break;
            case "emoji":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI, MessageFormatting.mapStringToBool(value));
                break;
            default:
                throw new HiromiSettingNotFoundException(String.format("The setting %s was not found", key));
        }

        return get(guild, databaseManager, setting, key, true);
    }


    @Override
    public void handle(ICommandContext ctx) {
        final GuildMessageReceivedEvent event = ctx.getEvent();
        final Guild guild = ctx.getGuild();
        final IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        final List<String> args = ctx.getArgs();
        final String subcommandName = args.size() > 0 ? args.get(0) : null;

        if (subcommandName == null ||
                validSettings.stream().noneMatch(settingInformation -> settingInformation.Name.equalsIgnoreCase(subcommandName) && (
                        settingInformation.Type == OptionType.STRING ||
                                settingInformation.Type == OptionType.BOOLEAN ||
                                settingInformation.Type == OptionType.INTEGER ||
                                settingInformation.Type == OptionType.NUMBER
                ))
        ) {
            final List<String> collect = validSettings.stream().map(settingInformation -> settingInformation.Name).collect(Collectors.toList());
            ctx.reply(String.format("Valid settings are: %s", collect)).queue();
            return;
        }

        final SettingInformation setting = validSettings.stream().filter(settingInformation -> settingInformation.Name.equalsIgnoreCase(subcommandName)).findAny().orElse(null);

        final String value = args.size() > 1 ? args.get(1) : null;
        if (value == null) {
            // Send current value?
            ctx.reply(get(guild, databaseManager, setting, subcommandName.toLowerCase())).queue();
            return;
        }

        ctx.reply(set(guild, databaseManager, setting, subcommandName.toLowerCase(), value)).queue();
    }

    private String set(Guild guild, IDatabaseManager databaseManager, SettingInformation setting, String key, OptionMapping value) {
        switch (key) {
            case "prefix":
                databaseManager.setPrefix(guild, value.getAsString());
                break;
            case "currency":
                databaseManager.setCurrency(guild, value.getAsString());
                break;
            case "fun":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.FUN, value.getAsBoolean());
                break;
            case "music":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC, value.getAsBoolean());
                break;
            case "moderation":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION, value.getAsBoolean());
                break;
            case "personality":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY, value.getAsBoolean());
                break;
            case "leveling":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING, value.getAsBoolean());
                break;
            case "emoji":
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI, value.getAsBoolean());
                break;
            default:
                throw new HiromiSettingNotFoundException(String.format("The setting %s was not found", key));
        }

        return get(guild, databaseManager, setting, key, true);
    }

    @Override
    public void handle(ISlashCommandContext ctx) {
        final SlashCommandEvent event = ctx.getEvent();
        final Member member = ctx.getMember();
        final Guild guild = ctx.getGuild();
        final IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        final String subcommandName = event.getSubcommandName();
        final OptionMapping valueOption = event.getOption("value");

        if (subcommandName == null || validSettings.stream().noneMatch(settingInformation -> settingInformation.Name.equalsIgnoreCase(subcommandName))) {
            // Send GUI like thing?
            return;
        }

        final SettingInformation setting = validSettings.stream().filter(settingInformation -> settingInformation.Name.equalsIgnoreCase(subcommandName)).findAny().orElse(null);

        if (valueOption == null) {
            // Send current value?
            ctx.reply(get(guild, databaseManager, setting, subcommandName.toLowerCase())).setEphemeral(true).queue();
            return;
        }

        ctx.reply(set(guild, databaseManager, setting, subcommandName.toLowerCase(), valueOption)).setEphemeral(true).queue();
    }

    @Override
    public void handle(String subtype, IButtonCommandContext ctx) {
        LOGGER.info("Something ended up being called as a button. {}", ctx.getEvent());
    }

    @Override
    public CommandData getCommandDefinition() {
        List<SubcommandData> subcommands = new ArrayList<>();
        validSettings.forEach(s -> {
            final SubcommandData subcommand = new SubcommandData(s.Name, s.Description);
            subcommand.addOptions(new OptionData(s.Type, "value", String.format("Specify a new value for %s", s.Name)));
            subcommands.add(subcommand);
        });

        return new CommandData("settings", "Enable and disable modules and change the legacy prefix of the bot here.")
                .addSubcommands(subcommands);
    }
}
