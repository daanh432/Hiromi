package nl.daanh.hiromi.database.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromi.Hiromi;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.exceptions.NotImplementedException;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public class HiromiPostgresDataSource implements IDatabaseManager {
    private static final int MAX_LIFETIME = 60 * 1000;
    private static final int MIN_IDLE = 5;

    private final HikariConfig config;
    private final HikariDataSource ds;

    public HiromiPostgresDataSource() {
        final IHiromiConfig config = Hiromi.getConfig();
        this.config = new HikariConfig();

        this.config.setJdbcUrl(config.getJdbcUrl());
        this.config.setUsername(config.getJdbcUsername());
        this.config.setPassword(config.getJdbcPassword());
        this.config.setConnectionTimeout(3000);
        this.config.setKeepaliveTime(5000);
        this.config.setMaxLifetime(MAX_LIFETIME);
        this.config.setMaxLifetime(MIN_IDLE);

// TODO determine if these are required for postgres
//        this.config.addDataSourceProperty("cachePrepStmts", "true");
//        this.config.addDataSourceProperty("prepStmtCacheSize", "250");
//        this.config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.ds = new HikariDataSource(this.config);
    }

    private Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    @Nullable
    @Override
    public String getKey(Guild guild, String key) {
        throw new NotImplementedException("Not implemented on postgres");
    }

    @Nullable
    @Override
    public String getKey(Member member, String key) {
        throw new NotImplementedException("Not implemented on postgres");
    }

    @Nullable
    @Override
    public String getKey(User user, String key) {
        throw new NotImplementedException("Not implemented on postgres");
    }

    @Override
    public void writeKey(Guild guild, String key, String value) {
        throw new NotImplementedException("Not implemented on postgres");
    }

    @Override
    public void writeKey(Member member, String key, String value) {
        throw new NotImplementedException("Not implemented on postgres");
    }

    @Override
    public void writeKey(User user, String key, String value) {
        throw new NotImplementedException("Not implemented on postgres");
    }
}
