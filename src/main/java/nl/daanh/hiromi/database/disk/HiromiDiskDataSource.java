package nl.daanh.hiromi.database.disk;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.exceptions.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Properties;

public class HiromiDiskDataSource implements IDatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiromiDiskDataSource.class);
    private static final int EXPIRES_IN = 60; // 60 seconds
    private final HashMap<Long, Properties> settingsCache = new HashMap<>();

    private File getGuildFile(Guild guild) {
        return new File("./guildConfigs/" + guild.getIdLong() + "/config.properties");
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

    @Override
    public String getKey(Guild guild, String key) {
        Properties setting = this.fetchSettings(guild);
        if (setting.containsKey(key)) {
            return setting.getProperty(key);
        }

        return this.getDefaultSetting(key);
    }

    @Override
    public void writeKey(Guild guild, String key, String value) {
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
    public String getKey(Member member, String key) {
        throw new NotImplementedException();
    }

    @Override
    public String getKey(User user, String key) {
        throw new NotImplementedException();
    }

    @Override
    public void writeKey(Member member, String key, String value) {
        throw new NotImplementedException();
    }

    @Override
    public void writeKey(User user, String key, String value) {
        throw new NotImplementedException();
    }
}