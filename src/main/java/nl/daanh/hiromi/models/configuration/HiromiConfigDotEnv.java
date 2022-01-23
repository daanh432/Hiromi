package nl.daanh.hiromi.models.configuration;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;
import java.util.Optional;

public class HiromiConfigDotEnv extends BaseHiromiConfig {
    private final Dotenv dotenv = Dotenv.load();
    private final Map<String, String> env = System.getenv();

    public HiromiConfigDotEnv() {
        instance = this;
    }

    @Override
    protected Optional<String> getString(String key) {
        if (env.containsKey(key))
            return Optional.ofNullable(env.get(key));
        else
            return Optional.ofNullable(dotenv.get(key));
    }

    @Override
    protected Optional<Boolean> getBool(String key) {
        final Optional<String> string = this.getString(key);
        if (string.isEmpty()) return Optional.empty();
        return Optional.of(string.get().equalsIgnoreCase("yes"));
    }

    @Override
    protected Optional<Integer> getInt(String key) {
        final Optional<String> string = this.getString(key);
        if (string.isEmpty()) return Optional.empty();
        try {
            return Optional.of(Integer.parseInt(string.get()));
        } catch (Exception exception) {
            LOGGER.error("Something went wrong trying to parse a integer from the configuration.", exception);
            return Optional.empty();
        }
    }

    @Override
    protected Optional<Long> getLong(String key) {
        final Optional<String> string = this.getString(key);
        if (string.isEmpty()) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(string.get()));
        } catch (Exception exception) {
            LOGGER.error("Something went wrong trying to parse a long from the configuration.", exception);
            return Optional.empty();
        }
    }

    @Override
    public String getJdbcUrl() {
        return this.getString("JDBC_URL").orElseThrow();
    }

    @Override
    public String getJdbcUsername() {
        return this.getString("JDBC_USERNAME").orElseThrow();
    }

    @Override
    public String getJdbcPassword() {
        return this.getString("JDBC_PASSWORD").orElseThrow();
    }
}
