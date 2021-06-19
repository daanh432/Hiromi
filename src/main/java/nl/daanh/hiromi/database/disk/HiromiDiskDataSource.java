package nl.daanh.hiromi.database.disk;

import net.dv8tion.jda.api.entities.Guild;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class HiromiDiskDataSource implements IDatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiromiDiskDataSource.class);
    private static final int EXPIRES_IN = 60; // 60 seconds
    private final HashMap<Long, Properties> settingsCache = new HashMap<>();

    private File getGuildFile(Guild guild) {
        return new File("./guildConfigs/" + guild.getIdLong() + "/config.properties");
    }

    private String getDefaultSetting(String key) {
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

    private Properties fetchSettings(Guild guild) {
        if (this.settingsCache.containsKey(guild.getIdLong())) {
            Properties setting = this.settingsCache.get(guild.getIdLong());
            if (Long.parseLong(setting.getProperty("local_expires_at", "0")) > Instant.now().getEpochSecond()) {
                return setting;
            }
        }

        File guildFile = this.getGuildFile(guild);
        if (!guildFile.exists()) return new Properties();

        try {
            FileReader reader = new FileReader(guildFile);
            Properties properties = new Properties();
            properties.load(reader);
            reader.close();

            properties.put("local_expires_at", String.valueOf(Instant.now().getEpochSecond() + EXPIRES_IN));
            this.settingsCache.put(guild.getIdLong(), properties);
            return properties;
        } catch (IOException e) {
            throw new HiromiDiskIOException(e);
        }
    }

    private String getKey(Guild guild, String key) {
        Properties setting = this.fetchSettings(guild);
        if (setting.containsKey(key)) {
            return setting.getProperty(key);
        }

        return this.getDefaultSetting(key);
    }

    private void writeKey(Guild guild, String key, String value) {
        try {
            Properties properties = this.fetchSettings(guild);
            properties.setProperty(key, value);

            File settingsFile = this.getGuildFile(guild);
            if (!settingsFile.exists())
                if (settingsFile.getParentFile().mkdirs())
                    LOGGER.debug(String.format("Created the initial file structure for the guild: %s", guild.getName()));

            FileWriter writer = new FileWriter(settingsFile);
            properties.store(writer, String.format("Guild: %s", guild.getIdLong()));
            writer.close();
        } catch (IOException e) {
            throw new HiromiDiskIOException(e);
        }
    }

    @Override
    public String getPrefix(Guild guild) {
        return this.getKey(guild, "prefix");
    }

    @Override
    public void setPrefix(Guild guild, String prefix) {
        this.writeKey(guild, "prefix", prefix);
    }

    @Override
    public List<CommandCategory.CATEGORY> getEnabledCategories(Guild guild) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories);
        return Arrays.stream(CommandCategory.CATEGORY.values()).filter(category -> (guildCategories & category.getMask()) == category.getMask()).collect(Collectors.toList());
    }

    @Override
    public boolean getCategoryEnabled(Guild guild, CommandCategory.CATEGORY category) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories);
        return (guildCategories & category.getMask()) == category.getMask();
    }

    @Override
    public void setCategoryEnabled(Guild guild, CommandCategory.CATEGORY category, boolean enabled) {
        String categories = this.getKey(guild, "categories");
        int guildCategories = Integer.parseInt(categories);

        if (enabled)
            guildCategories = guildCategories | category.getMask();
        else if (getCategoryEnabled(guild, category))
            guildCategories = guildCategories ^ category.getMask();

        this.writeKey(guild, "categories", String.valueOf(guildCategories));
    }
}