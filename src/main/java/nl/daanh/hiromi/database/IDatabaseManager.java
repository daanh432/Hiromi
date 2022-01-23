package nl.daanh.hiromi.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public interface IDatabaseManager {
    Logger LOGGER = LoggerFactory.getLogger(IDatabaseManager.class);
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Nullable
    default String getDefaultSetting(String key) {
        // If value has not been found on the online api or in the cache return the default value
        switch (key) {
            case "currency":
                return "Ƕ %";
            case "prefix":
                return "hi!";
            case "categories":
            case "bank":
            case "cash":
                return "0";
            case "birthdate":
            case "timezone":
                return null;
            default:
                throw new RuntimeException("No default value for " + key.toUpperCase());
        }
    }

    @Nullable
    String getKey(Guild guild, String key);

    @Nullable
    String getKey(Member member, String key);

    @Nullable
    String getKey(User user, String key);

    void writeKey(Guild guild, String key, String value);

    void writeKey(Member member, String key, String value);

    void writeKey(User user, String key, String value);

    default String getPrefix(Guild guild) {
        return this.getKey(guild, "prefix");
    }

    default String getCurrency(Guild guild) {
        return this.getKey(guild, "currency");
    }

    default void setPrefix(Guild guild, String prefix) {
        this.writeKey(guild, "prefix", prefix);
    }

    default void setCurrency(Guild guild, String currency) {
        this.writeKey(guild, "currency", currency);
    }

    default List<CommandCategory.CATEGORY> getEnabledCategories(Guild guild) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories != null ? categories : "0");
        return Arrays.stream(CommandCategory.CATEGORY.values()).filter(category -> (guildCategories & category.getMask()) == category.getMask()).collect(Collectors.toList());
    }

    default boolean getCategoryEnabled(Guild guild, CommandCategory.CATEGORY category) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories != null ? categories : "0");
        return (guildCategories & category.getMask()) == category.getMask();
    }

    default void setCategoryEnabled(Guild guild, CommandCategory.CATEGORY category, boolean enabled) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories != null ? categories : "0");

        if (enabled)
            guildCategories = guildCategories | category.getMask();
        else if (getCategoryEnabled(guild, category))
            guildCategories = guildCategories ^ category.getMask();

        writeKey(guild, "categories", String.valueOf(guildCategories));
    }

    default long getBankAmount(Member member) {
        String bankAmount = this.getKey(member, "bank");
        try {
            return Long.parseLong(bankAmount != null ? bankAmount : "0");
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    default long getCashAmount(Member member) {
        String cashAmount = this.getKey(member, "cash");
        try {
            return Long.parseLong(cashAmount != null ? cashAmount : "0");
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    default void setBankAmount(Member member, long bankAmount) {
        writeKey(member, "bank", String.valueOf(bankAmount));
    }

    default void setCashAmount(Member member, long cashAmount) {
        writeKey(member, "cash", String.valueOf(cashAmount));
    }

    @Nullable
    default Date getBirthdate(User user) {
        String birthdate = this.getKey(user, "birthdate");

        if (birthdate == null) return null;

        try {
            return dateFormatter.parse(birthdate);
        } catch (Exception e) {
            LOGGER.error("Something went wrong parsing the birthdate.", e);
            return null;
        }
    }

    @Nullable
    default TimeZone getTimezone(User user) {
        String timezone = this.getKey(user, "timezone");
        if (timezone == null) return null;
        try {
            return TimeZone.getTimeZone(timezone);
        } catch (Exception exception) {
            LOGGER.error("Something went wrong parsing the timezone.", exception);
            return null;
        }
    }

    default void setTimezone(User user, TimeZone timezone) {
        writeKey(user, "timezone", timezone.getID());
    }
}
