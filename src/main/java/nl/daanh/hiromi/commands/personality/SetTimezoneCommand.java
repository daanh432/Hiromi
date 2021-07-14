package nl.daanh.hiromi.commands.personality;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

@CommandInvoke("settimezone")
@CommandInvoke("settime")
@CommandInvoke("settz")
@CommandInvoke("timezoneset")
@CommandInvoke("timeset")
@CommandCategory(CommandCategory.CATEGORY.PERSONALITY)
@SelfPermission(Permission.MESSAGE_WRITE)
public class SetTimezoneCommand implements ICommand, ISlashCommand {
    private ZoneId parseTimezone(String timezoneInput) {
        try {
            return ZoneId.of(timezoneInput);
        } catch (DateTimeException exception) {
            return null;
        }
    }


    @Override
    public void handle(ICommandContext ctx) {
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            ctx.reply("Please specify a timezone. Examples: ``Europe/Amsterdam``, ``America/New_York``, ``UTC``").queue();
            return;
        }

        ZoneId zoneId = parseTimezone(args.get(0));

        if (zoneId == null) {
            ctx.reply("Timezone invalid or not found. Please try a different input. Examples: ``Europe/Amsterdam``, ``America/New_York``, ``UTC``").queue();
            return;
        }

        databaseManager.setTimezone(member.getUser(), zoneId);
        ctx.reply(String.format("Your timezone has been changed to ``%s``", args.get(0))).queue();
    }

    @Override
    public void handle(ISlashCommandContext ctx) {
        SlashCommandEvent event = ctx.getEvent();
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();
        OptionMapping timezoneOption = event.getOption("timezone");

        if (timezoneOption == null) {
            ctx.reply("Please specify a timezone. Examples: ``Europe/Amsterdam``, ``America/New_York``, ``UTC``")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        ZoneId zoneId = parseTimezone(timezoneOption.getAsString());

        if (zoneId == null) {
            ctx.reply("Timezone invalid or not found. Please try a different input. Examples: ``Europe/Amsterdam``, ``America/New_York``, ``UTC``")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        databaseManager.setTimezone(member.getUser(), zoneId);
        ctx.reply(String.format("Your timezone has been changed to ``%s``", timezoneOption.getAsString()))
                .setEphemeral(true)
                .queue();
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("settimezone", "Set your local timezone so your friends know what time it is for you.")
                .addOption(OptionType.STRING, "timezone", "Your timezone. Examples: Europe/Amsterdam, America/New_York, UTC", true);
    }
}
