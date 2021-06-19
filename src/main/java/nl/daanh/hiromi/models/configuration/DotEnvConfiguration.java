package nl.daanh.hiromi.models.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.database.disk.HiromiDiskDataSource;
import nl.daanh.hiromi.exceptions.NotImplementedException;

import java.util.Map;

public class DotEnvConfiguration implements IConfiguration {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Map<String, String> env = System.getenv();
    private final IDatabaseManager databaseManager;

    public DotEnvConfiguration() {
        String implementationVersion = this.getClass().getPackage().getImplementationVersion();
//        WebUtils.setUserAgent(String.format("Hiromi/%s", implementationVersion != null ? implementationVersion : "DEVELOPMENT"));
//        WebUtils.setHiromiApiToken(this.getEnv("HIRMOIAPI_TOKEN", ""));

        switch (this.getEnv("DATA_SOURCE", "disk").toLowerCase()) {
            case "api":
            case "hirmoi_api":
            case "hiromiapi":
            case "hiromi":
                throw new NotImplementedException("Hiromi API data source has not been implemented yet");
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
            default:
                this.databaseManager = new HiromiDiskDataSource();
                break;
        }
    }

    private String getEnv(String key) {
        if (env.containsKey(key))
            return env.get(key);
        else
            return dotenv.get(key);
    }

    private String getEnv(String key, String defaultValue) {
        if (env.containsKey(key))
            return env.get(key);
        else
            return dotenv.get(key, defaultValue);
    }

    private boolean getBool(String key, String defaultValue) {
        return this.getEnv(key, defaultValue).equalsIgnoreCase("yes");
    }

    @Override
    public String getGlobalPrefix() {
        return this.getEnv("GLOBAL_PREFIX", "hi!");
    }

    @Override
    public String getStatusText() {
        return this.getEnv("STATUS_TEXT", String.format("%shelp", this.getGlobalPrefix()));
    }

    @Override
    public String getDiscordToken() {
        return this.getEnv("DISCORD_TOKEN", "");
    }

    @Override
    public IDatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    @Override
    public boolean getMusicEnabled() {
        return this.getBool("MUSIC_ENABLED", "NO");
    }

    @Override
    public boolean getSelfHosted() {
        return this.getBool("SELF_HOSTED", "YES");
    }

// TODO Reimplement lavalink

//    @Override
//    public List<LavalinkRemoteNode> getLavalinkNodes() {
//        int counter = 0;
//        String raw = this.getEnv("LAVALINK_CLIENT_" + counter);
//
//        List<LavalinkRemoteNode> remoteNodes = new ArrayList<>();
//
//        while (raw != null) {
//            String[] fields = raw.split(",");
//            remoteNodes.add(new LavalinkRemoteNode(fields[0], fields[1]));
//            raw = this.getEnv("LAVALINK_CLIENT_" + ++counter);
//        }
//
//        return remoteNodes;
//    }
//
//    @Override
//    public boolean getLavalinkEnabled() {
//        return this.getEnv("LAVALINK_CLIENT_0", null) != null && this.getBool("LAVALINK_ENABLED", "NO");
//    }

    @Override
    public int getShardCount() {
        return Integer.parseInt(this.getEnv("SHARD_COUNT", "1"));
    }
}
