package nl.daanh.hiromi.models.configuration;

import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.database.api.HiromiApiAsyncDataSource;
import nl.daanh.hiromi.database.api.HiromiApiSyncDataSource;
import nl.daanh.hiromi.database.disk.HiromiDiskDataSource;
import nl.daanh.hiromi.exceptions.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Optional;

public abstract class BaseHiromiConfig implements IHiromiConfig {
    protected static BaseHiromiConfig instance;
    protected static Logger LOGGER = LoggerFactory.getLogger(BaseHiromiConfig.class);

    private boolean tokenFetched = false;
    private IDatabaseManager databaseManager;

    // Generic methods for fetching values
    protected abstract Optional<String> getString(String key);

    protected abstract Optional<Boolean> getBool(String key);

    protected abstract Optional<Integer> getInt(String key);

    /**
     * The active configuration instance.
     *
     * @return the active configuration instance
     */
    public static IHiromiConfig getInstance() {
        return instance;
    }

    @NotNull
    public Color getEmbedColor() {
        return Color.decode("#" + instance.getString("EMBED_COLOR_HEX").or(() -> Optional.of("4287f5")));
    }

    /**
     * Gets the discord token, can only be called once
     *
     * @return Discord token string
     * @throws RuntimeException                 when it's being fetched more than once
     * @throws java.util.NoSuchElementException when the token is not set in the config
     */
    @Override
    @NotNull
    public String getToken() {
        if (tokenFetched) throw new RuntimeException("Token can only be fetched once!");
        tokenFetched = true;
        return getString("DISCORD_TOKEN").orElseThrow();
    }

    /**
     * Gets the amount of shards to start
     *
     * @return int of the count of shards to start
     */
    @Override
    public int getTotalShards() {
        return getInt("DISCORD_SHARDS").or(() -> Optional.of(1)).get();
    }

    @Override
    @NotNull
    public String getGlobalPrefix() {
        return getString("PREFIX").or(() -> Optional.of("hi!")).get();
    }

    @Override
    @NotNull
    public String getStatusText() {
        return getString("STATUS").or(() -> Optional.of("Hi! I'm Hiromi!")).get();
    }

    @Override
    public boolean getLavalinkEnabled() {
        return getBool("LAVALINK_ENABLED").or(() -> Optional.of(false)).get();
    }

    @Override
    public boolean getMusicEnabled() {
        return getBool("MUSIC_ENABLED").or(() -> Optional.of(false)).get();
    }

    @Override
    @NotNull
    public String getGithubLink() {
        return getString("GITHUB_URL").or(() -> Optional.of("https://github.com")).get();
    }

    @Override
    @NotNull
    public IDatabaseManager getDatabaseManager() {
        if (this.databaseManager != null) return this.databaseManager;

        switch (this.getString("DATA_SOURCE").or(() -> Optional.of("disk")).get().toLowerCase()) {
            case "apiasync":
            case "hirmoi_api_async":
            case "hiromiapiasync":
            case "hiromiasync":
                this.databaseManager = new HiromiApiAsyncDataSource();
                break;
            case "api":
            case "hirmoi_api":
            case "hiromiapi":
            case "hiromi":
                this.databaseManager = new HiromiApiSyncDataSource();
                break;
            case "mysql":
            case "mariadb":
            case "maria":
                throw new NotImplementedException("Mysql data source has not been implemented yet");
            case "postgresql":
                throw new NotImplementedException("PostgreSQL data source has not been implemented yet");
            case "sqlite":
                throw new NotImplementedException("SQLite data source has not been implemented yet");
            case "mongodb":
            case "mongo":
                throw new NotImplementedException("MongoDB data source has not been implemented yet");
            case "disk":
            case "dsk":
            case "dik":
                this.databaseManager = new HiromiDiskDataSource();
                break;
            default:
                throw new NotImplementedException("Unknown data source has not ben implemented yet");
        }

        return this.databaseManager;
    }

    @Override
    @NotNull
    public String getApiToken() {
        return getString("API_TOKEN").or(() -> Optional.of("")).get();
    }
}
