package nl.daanh.hiromi.commands.other;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
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

import java.util.Arrays;
import java.util.List;

@CommandInvoke("settings")
@CommandInvoke("setting")
@CommandInvoke("options")
@CommandInvoke("option")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@SelfPermission(Permission.MESSAGE_WRITE)
@UserPermission(Permission.ADMINISTRATOR)
public class SettingsCommand implements ICommand, ISlashCommand {
    private static final List<String> validSettings = Arrays.asList("prefix", "music", "fun", "moderation");

    @Override
    public void handle(ICommandContext ctx) {
        List<String> args = ctx.getArgs();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        Guild guild = ctx.getGuild();

        if (args.size() == 0) {
            ctx.reply(String.format("Following the command specify the option to get it's current value. To set the value specify both the option and value.\nPossible options: %s", String.join(", ", validSettings))).queue();
            return;
        }

        String key = args.get(0);

        if (!validSettings.contains(args.get(0).toLowerCase())) {
            ctx.reply(String.format("Sorry. I'm not sure what setting you are looking for.\nPossible options: %s", String.join(", ", validSettings))).queue();
            return;
        }

        if (args.size() == 1) {
            ctx.reply(this.getValue(databaseManager, guild, key)).queue();
            return;
        }

        String value = String.join(" ", args.subList(1, args.size()));
        ctx.reply(this.setValue(databaseManager, guild, key, value)).queue();
    }

    /**
     * Check what the current setting is
     *
     * @param databaseManager the database manager from ctx
     * @param guild           the guild from ctx
     * @param key             the key to be fetched
     * @return Returns a response message to be send back to the user.
     */
    private String getValue(IDatabaseManager databaseManager, Guild guild, String key) {
        switch (key) {
            case "prefix":
                String prefix = databaseManager.getPrefix(guild);
                return String.format("The prefix is set to ``%s``.", prefix);
            case "music":
                boolean musicEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC);
                return String.format("The category ``music`` is ``%s``.", musicEnabled ? "enabled" : "disabled");
            case "fun":
                boolean funEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.FUN);
                return String.format("The category ``fun`` is ``%s``.", funEnabled ? "enabled" : "disabled");
            case "moderation":
                boolean moderationEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION);
                return String.format("The category ``moderation`` is ``%s``.", moderationEnabled ? "enabled" : "disabled");
        }

        throw new HiromiSettingNotFoundException(String.format("%s is not a valid setting", key));
    }

    /**
     * Sets a setting to a new value
     *
     * @param databaseManager the database manager from ctx
     * @param guild           the guild from ctx
     * @param key             the key to be set
     * @param value           the value to be set
     * @return Returns a response message to be send back to the user.
     */
    private String setValue(IDatabaseManager databaseManager, Guild guild, String key, String value) {
        switch (key) {
            case "prefix":
                String prefix = value.replaceAll("\\s", "");
                databaseManager.setPrefix(guild, prefix);
                return String.format("I have set the prefix to ``%s`` for you.", prefix);
            case "music":
                boolean musicEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC, musicEnabled);
                return String.format("I have ``%s`` the category ``music`` for you.", musicEnabled ? "enabled" : "disabled");
            case "fun":
                boolean funEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.FUN, funEnabled);
                return String.format("I have ``%s`` the category ``fun`` for you.", funEnabled ? "enabled" : "disabled");
            case "moderation":
                boolean moderationEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION, moderationEnabled);
                return String.format("I have ``%s`` the category ``moderation`` for you.", moderationEnabled ? "enabled" : "disabled");
        }

        throw new HiromiSettingNotFoundException(String.format("%s is not a valid setting", key));
    }

    private boolean mapStringToBool(String value) {
        value = value.toLowerCase();
        switch (value) {
            case "yes":
            case "true":
            case "tru":
            case "tre":
            case "tue":
            case "rue":
            case "ja":
            case "jaa":
            case "y":
            case "yeah":
            case "ye":
            case "yep":
            case "ys":
            case "es":
                return true;
            default:
                return false;
        }
    }

    @Override
    public void handle(ISlashCommandContext ctx) {
        SlashCommandEvent event = ctx.getEvent();
        Guild guild = ctx.getGuild();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        final long userId = ctx.getMember().getIdLong();

        OptionMapping keyOption = event.getOption("key");
        OptionMapping valueOption = event.getOption("value");

        if (keyOption == null) {

            ctx.reply("Please specify the name of the setting you want to view / edit.")
                    .addActionRow(validSettings.stream().map((s -> Button.secondary(userId + ":" + this.getInvoke() + ":view:" + s, s))).toArray(Button[]::new))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String key = keyOption.getAsString();

        if (!validSettings.contains(key.toLowerCase())) {
            ctx.reply(String.format("Sorry. I'm not sure what setting you are looking for.\nPossible options: %s", String.join(", ", validSettings)))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (valueOption == null) {
            ctx.reply(this.getValue(databaseManager, guild, key))
                    .addActionRow(
                            Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"),
                            Button.secondary(userId + ":" + this.getInvoke() + ":edit:" + key, "Edit")
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String value = valueOption.getAsString();
        ctx.reply(this.setValue(databaseManager, guild, key, value))
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void handle(String subtype, IButtonCommandContext ctx) {
        List<String> args = ctx.getArgs();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        Guild guild = ctx.getGuild();
        final long userId = ctx.getMember().getIdLong();

        switch (subtype) {
            case "view":
                if (args.size() < 1) return;

                ctx.reply(this.getValue(databaseManager, guild, args.get(0)))
                        .setActionRow(
                                Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"),
                                Button.secondary(userId + ":" + this.getInvoke() + ":edit:" + args.get(0), "Edit")
                        )
                        .queue();
                break;
            case "back":

                ctx.reply("Please specify the name of the setting you want to view / edit.")
                        .setActionRow(
                                validSettings.stream().map((s -> Button.secondary(userId + ":" + this.getInvoke() + ":view:" + s, s))).toArray(Button[]::new)
                        )
                        .queue();
                break;
            case "edit":
                if (args.size() < 1) return;
                ctx.reply("To change the value of the settings please use ``/" + this.getInvoke() + " key:" + args.get(0) + " value:<your value>`` for now.")
                        .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                        .queue();
                break;
        }
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("settings", "Enable and disable modules and change the legacy prefix of the bot here.")
                .addOptions(
                        new OptionData(OptionType.STRING, "key", "The name of the setting you want to change or view"),
                        new OptionData(OptionType.STRING, "value", "The new value you want to specify for the setting")

                );
    }
}
