package nl.daanh.hiromi.utils;

import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.commandcontext.IBaseCommandContext;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessageFormatting {
    public static String currencyFormat(IBaseCommandContext ctx, int amount) {
        return ctx.getConfiguration().getDatabaseManager().getCurrency(ctx.getGuild()).replace("%", String.valueOf(amount));
    }

    public static String currencyFormat(IBaseCommandContext ctx, long amount) {
        return ctx.getConfiguration().getDatabaseManager().getCurrency(ctx.getGuild()).replace("%", String.valueOf(amount));
    }

    public static String currentDateTimeFormatted(TimeZone zoneId) {
        return dateTimeFormat(Instant.now(), zoneId);
    }

    public static String dateTimeFormat(Instant instant, TimeZone zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId.toZoneId()).format(IDatabaseManager.dateTimeFormatter);
    }

    public static String parseTimezone(TimeZone timezoneInput) {
        return timezoneInput.getDisplayName(timezoneInput.useDaylightTime() && timezoneInput.inDaylightTime(new Date()), TimeZone.LONG, Locale.ENGLISH);
    }
}
