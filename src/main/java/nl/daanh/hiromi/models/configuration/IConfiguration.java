package nl.daanh.hiromi.models.configuration;

import nl.daanh.hiromi.database.IDatabaseManager;

import java.util.List;

public interface IConfiguration {
    default String getGithubLink() {
        return "https://github.com/daanh432/Hiromi_V3";
    }

    String getGlobalPrefix();

    String getStatusText();

    String getDiscordToken();

    boolean getMusicEnabled();

    boolean getSelfHosted();

    int getShardCount();

    IDatabaseManager getDatabaseManager();
}
