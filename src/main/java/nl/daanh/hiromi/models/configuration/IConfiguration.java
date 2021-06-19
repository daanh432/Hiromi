package nl.daanh.hiromi.models.configuration;

import nl.daanh.hiromi.database.IDatabaseManager;

import java.util.List;

public interface IConfiguration {
    String getGlobalPrefix();

    String getStatusText();

    String getDiscordToken();

    boolean getMusicEnabled();

    boolean getSelfHosted();

    int getShardCount();

    IDatabaseManager getDatabaseManager();
}
