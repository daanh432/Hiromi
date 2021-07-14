package nl.daanh.hiromi.helpers;

import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.commandcontext.IBaseCommandContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MessageFormatting {
    public static String currencyFormat(IBaseCommandContext ctx, int amount) {
        return ctx.getConfiguration().getDatabaseManager().getCurrency(ctx.getGuild()).replace("%", String.valueOf(amount));
    }

    public static String currentDateTimeFormatted(ZoneId zoneId) {
        return dateTimeFormat(Instant.now(), zoneId);
    }

    public static String dateTimeFormat(Instant instant, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId).format(IDatabaseManager.dateTimeFormatter);
    }

    public static String currentTimeFormatted(ZoneId zoneId) {
        return timeFormat(Instant.now(), zoneId);
    }

    public static String timeFormat(Instant instant, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId).format(IDatabaseManager.timeFormatter);
    }
}
