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
import nl.daanh.hiromi.utils.ActionRowUtils;

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
    private static final List<String> validSettings = Arrays.asList("prefix", "currency", "music", "moderation", "personality", "leveling", "emoji");

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
            case "currency":
                boolean currencyEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.CURRENCY);
                return String.format("The category ``currency`` is ``%s``.", currencyEnabled ? "enabled" : "disabled");
            case "music":
                boolean musicEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC);
                return String.format("The category ``music`` is ``%s``.", musicEnabled ? "enabled" : "disabled");
            case "moderation":
                boolean moderationEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION);
                return String.format("The category ``moderation`` is ``%s``.", moderationEnabled ? "enabled" : "disabled");
            case "personality":
                boolean personalityEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY);
                return String.format("The category ``personality`` is ``%s``.", personalityEnabled ? "enabled" : "disabled");
            case "leveling":
                boolean levelingEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING);
                return String.format("The category ``leveling`` is ``%s``.", levelingEnabled ? "enabled" : "disabled");
            case "emoji":
                boolean emojiEnabled = databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI);
                return String.format("The category ``emoji`` is ``%s``.", emojiEnabled ? "enabled" : "disabled");
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
            case "currency":
                boolean currencyEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.CURRENCY, currencyEnabled);
                return String.format("I have ``%s`` the category ``currency`` for you.", currencyEnabled ? "enabled" : "disabled");
            case "music":
                boolean musicEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC, musicEnabled);
                return String.format("I have ``%s`` the category ``music`` for you.", musicEnabled ? "enabled" : "disabled");
            case "moderation":
                boolean moderationEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION, moderationEnabled);
                return String.format("I have ``%s`` the category ``moderation`` for you.", moderationEnabled ? "enabled" : "disabled");
            case "personality":
                boolean personalityEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY, personalityEnabled);
                return String.format("I have ``%s`` the category ``personality`` for you.", personalityEnabled ? "enabled" : "disabled");
            case "leveling":
                boolean levelingEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING, levelingEnabled);
                return String.format("I have ``%s`` the category ``leveling`` for you.", levelingEnabled ? "enabled" : "disabled");
            case "emoji":
                boolean emojiEnabled = this.mapStringToBool(value);
                databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI, emojiEnabled);
                return String.format("I have ``%s`` the category ``emoji`` for you.", emojiEnabled ? "enabled" : "disabled");
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
            Button[] buttons = validSettings.stream().map((s -> Button.secondary(userId + ":" + this.getInvoke() + ":view:" + s, s))).toArray(Button[]::new);
            ctx.reply("Please specify the name of the setting you want to view / edit.")
                    .addActionRows(ActionRowUtils.splitButtons(buttons))
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
                Button[] buttons = validSettings.stream().map((s -> Button.secondary(userId + ":" + this.getInvoke() + ":view:" + s, s))).toArray(Button[]::new);
                ctx.reply("Please specify the name of the setting you want to view / edit.")
                        .setActionRows(ActionRowUtils.splitButtons(buttons))
                        .queue();
                break;
            case "edit":
                if (args.size() < 1) return;

                switch (args.get(0)) {
                    case "currency":
                        boolean currencyEnabled = !databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.CURRENCY);
                        databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.CURRENCY, currencyEnabled);
                        ctx.reply(String.format("I have ``%s`` the category ``currency`` for you.", currencyEnabled ? "enabled" : "disabled"))
                                .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                                .queue();
                        return;
                    case "music":
                        boolean musicEnabled = !databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC);
                        databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MUSIC, musicEnabled);
                        ctx.reply(String.format("I have ``%s`` the category ``music`` for you.", musicEnabled ? "enabled" : "disabled"))
                                .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                                .queue();
                        return;
                    case "moderation":
                        boolean moderationEnabled = !databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION);
                        databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.MODERATION, moderationEnabled);
                        ctx.reply(String.format("I have ``%s`` the category ``moderation`` for you.", moderationEnabled ? "enabled" : "disabled"))
                                .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                                .queue();
                        return;
                    case "personality":
                        boolean personalityEnabled = !databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY);
                        databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.PERSONALITY, personalityEnabled);
                        ctx.reply(String.format("I have ``%s`` the category ``personality`` for you.", personalityEnabled ? "enabled" : "disabled"))
                                .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                                .queue();
                        return;
                    case "leveling":
                        boolean levelingEnabled = !databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING);
                        databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.LEVELING, levelingEnabled);
                        ctx.reply(String.format("I have ``%s`` the category ``leveling`` for you.", levelingEnabled ? "enabled" : "disabled"))
                                .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                                .queue();
                        return;
                    case "emoji":
                        boolean emojiEnabled = !databaseManager.getCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI);
                        databaseManager.setCategoryEnabled(guild, CommandCategory.CATEGORY.EMOJI, emojiEnabled);
                        ctx.reply(String.format("I have ``%s`` the category ``emoji`` for you.", emojiEnabled ? "enabled" : "disabled"))
                                .setActionRow(Button.secondary(userId + ":" + this.getInvoke() + ":back", "Back"))
                                .queue();
                        return;
                }

                ctx.reply("To change the value of this setting please use ``/" + this.getInvoke() + " key:" + args.get(0) + " value:<your value>`` for now.")
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
