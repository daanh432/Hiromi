package nl.daanh.hiromi.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface IDatabaseManager {
    default String getDefaultSetting(String key) {
        // If value has not been found on the online api or in the cache return the default value
        switch (key) {
            case "prefix":
                return "hi!";
            case "categories":
                return "0";
            default:
                return String.format("NO_DEFAULT_VALUE_FOR_%s", key.toUpperCase());
        }
    }

    String getKey(Guild guild, String key);

    String getKey(Member member, String key);

    String getKey(User user, String key);

    void writeKey(Guild guild, String key, String value);

    void writeKey(Member member, String key, String value);

    void writeKey(User user, String key, String value);

    default String getPrefix(Guild guild) {
        return this.getKey(guild, "prefix");
    }

    default void setPrefix(Guild guild, String prefix) {
        this.writeKey(guild, "prefix", prefix);
    }

    default List<CommandCategory.CATEGORY> getEnabledCategories(Guild guild) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories);
        return Arrays.stream(CommandCategory.CATEGORY.values()).filter(category -> (guildCategories & category.getMask()) == category.getMask()).collect(Collectors.toList());
    }

    default boolean getCategoryEnabled(Guild guild, CommandCategory.CATEGORY category) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories);
        return (guildCategories & category.getMask()) == category.getMask();
    }

    default void setCategoryEnabled(Guild guild, CommandCategory.CATEGORY category, boolean enabled) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories);

        if (enabled)
            guildCategories = guildCategories | category.getMask();
        else if (getCategoryEnabled(guild, category))
            guildCategories = guildCategories ^ category.getMask();

        writeKey(guild, "categories", String.valueOf(guildCategories));
    }
}
