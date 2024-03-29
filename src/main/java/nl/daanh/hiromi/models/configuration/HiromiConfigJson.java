package nl.daanh.hiromi.models.configuration;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class HiromiConfigJson extends BaseHiromiConfig {
    private JSONObject jsonObject;

    public HiromiConfigJson(File file) {
        try {
            jsonObject = new JSONObject(load(file));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        instance = this;
    }

    private static String load(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    @Override
    protected Optional<String> getString(String key) {
        try {
            return Optional.ofNullable(jsonObject.getString(key));
        } catch (Exception exception) {
            LOGGER.warn("The setting " + key + " was not set.");
            return Optional.empty();
        }
    }

    @Override
    protected Optional<Integer> getInt(String key) {
        try {
            return Optional.of(jsonObject.getInt(key));
        } catch (Exception exception) {
            LOGGER.warn("The setting " + key + " was not set.");
            return Optional.empty();
        }
    }

    @Override
    protected Optional<Long> getLong(String key) {
        try {
            return Optional.of(jsonObject.getLong(key));
        } catch (Exception exception) {
            LOGGER.warn("The setting " + key + " was not set.");
            return Optional.empty();
        }
    }

    @Override
    protected Optional<Boolean> getBool(String key) {
        try {
            return Optional.of(jsonObject.getBoolean(key));
        } catch (Exception exception) {
            LOGGER.warn("The setting " + key + " was not set.");
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