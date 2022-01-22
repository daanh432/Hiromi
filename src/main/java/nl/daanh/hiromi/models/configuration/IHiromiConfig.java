package nl.daanh.hiromi.models.configuration;

import nl.daanh.hiromi.database.IDatabaseManager;

import java.awt.*;

public interface IHiromiConfig {
    static IHiromiConfig getInstance() {
        return BaseHiromiConfig.getInstance();
    }

    /**
     * Fetches the id of the person that owns the bot and has permissions to access overrides
     *
     * @return id long of member
     */
    long getOwner();

    Color getEmbedColor();

    /**
     * Gets the discord token, can only be called once
     *
     * @return Discord token string
     * @throws RuntimeException                 when it's being fetched more than once
     * @throws java.util.NoSuchElementException when the token is not set in the config
     */
    String getToken();

    /**
     * Gets the amount of shards to start
     *
     * @return int of the count of shards to start
     */
    int getTotalShards();

    String getGlobalPrefix();

    String getStatusText();

    boolean getLavalinkEnabled();

    boolean getMusicEnabled();

    IDatabaseManager getDatabaseManager();

    String getGithubLink();

    String getApiToken();
}
