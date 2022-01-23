package nl.daanh.hiromi.database.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromi.Hiromi;
import nl.daanh.hiromi.database.HiromiDatabaseException;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class HiromiPostgresDataSource implements IDatabaseManager {
    private static final int MAX_LIFETIME = 300 * 60 * 1000;
    private static final int MIN_IDLE = 5;
    private static final List<IPostgresMigration> MIGRATION_LIST = List.of(
            new CreateUserSettingsTable(),
            new CreateGuildSettingsTable(),
            new CreateGuildMemberSettingsTable()
    );

    private final HikariConfig config;
    private final HikariDataSource ds;

    public HiromiPostgresDataSource() {
        final IHiromiConfig config = Hiromi.getConfig();
        this.config = new HikariConfig();

        this.config.setJdbcUrl(config.getJdbcUrl());
        this.config.setUsername(config.getJdbcUsername());
        this.config.setPassword(config.getJdbcPassword());
        this.config.setKeepaliveTime(30000);
        this.config.setMaxLifetime(MAX_LIFETIME);
        this.config.setMinimumIdle(MIN_IDLE);
        this.config.setMaximumPoolSize(50);

        // TODO determine if these are required for postgres
        this.config.addDataSourceProperty("cachePrepStmts", "true");
        this.config.addDataSourceProperty("prepStmtCacheSize", "250");
        this.config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.ds = new HikariDataSource(this.config);

        migrate();
    }

    /**
     * All changes that are required will be called from here
     */
    private void migrate() {
        try {
            final Connection connection = this.getConnection();

            final IPostgresMigration createMigrationsTable = new CreateMigrationsTable();
            createMigrationsTable.migrate(connection);

            MIGRATION_LIST.forEach(migration -> {
                final String name = migration.getClass().getName();
                try {
                    connection.createStatement().execute("START TRANSACTION");
                    PreparedStatement checkStatement = connection.prepareStatement("select * from migrations where \"name\"=?");
                    checkStatement.setString(1, name);
                    final ResultSet result = checkStatement.executeQuery();
                    if (!result.next()) {
                        migration.migrate(connection);
                        PreparedStatement checkStatementInsert = connection.prepareStatement("insert into migrations (\"name\") values (?)");
                        checkStatementInsert.setString(1, name);
                        checkStatementInsert.execute();
                    }
                    connection.createStatement().execute("COMMIT");
                } catch (SQLException exception) {
                    LOGGER.error("Something went wrong trying to run the migration {}.", name, exception);
                    try {
                        connection.createStatement().execute("ABORT");
                    } catch (SQLException e) {
                        LOGGER.error("Transaction couldn't be aborted", e);
                    }
                }
            });

        } catch (SQLException exception) {
            LOGGER.error("Something went wrong trying to migrate the database.", exception);
            Hiromi.getEventManager().shutdown();
        }
    }

    private Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    @Nullable
    @Override
    public String getKey(Guild guild, String key) {
        try {
            final Connection connection = getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from hiromi.guild_settings where id = ?  and key = ?");
            preparedStatement.setLong(1, guild.getIdLong());
            preparedStatement.setString(2, key);
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value");
            }
        } catch (SQLException e) {
            throw new HiromiDatabaseException("Something went wrong trying to fetch data from the database", e);
        }

        return this.getDefaultSetting(key);
    }

    @Nullable
    @Override
    public String getKey(Member member, String key) {
        try {
            final Connection connection = getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from hiromi.guild_member_settings where guild_id = ? and member_id = ? and key = ?");
            preparedStatement.setLong(1, member.getGuild().getIdLong());
            preparedStatement.setLong(2, member.getIdLong());
            preparedStatement.setString(3, key);
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value");
            }
        } catch (SQLException e) {
            throw new HiromiDatabaseException("Something went wrong trying to fetch data from the database", e);
        }

        return this.getDefaultSetting(key);
    }

    @Nullable
    @Override
    public String getKey(User user, String key) {
        try {
            final Connection connection = getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from hiromi.user_settings where id = ?  and key = ?");
            preparedStatement.setLong(1, user.getIdLong());
            preparedStatement.setString(2, key);
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value");
            }
        } catch (SQLException e) {
            throw new HiromiDatabaseException("Something went wrong trying to fetch data from the database", e);
        }

        return this.getDefaultSetting(key);
    }

    @Override
    public void writeKey(Guild guild, String key, String value) {
        try {
            final Connection connection = getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("insert into guild_settings (id, key, value) VALUES(?, ?, ?) on conflict on constraint guild_settings_pk do update set value=?");
            preparedStatement.setLong(1, guild.getIdLong());
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, value);
            preparedStatement.setString(4, value);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new HiromiDatabaseException("Something went wrong trying to update in the database", e);
        }
    }

    @Override
    public void writeKey(Member member, String key, String value) {
        try {
            final Connection connection = getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("insert into guild_member_settings (guild_id, member_id, key, value) VALUES(?, ?, ?, ?) on conflict on constraint guild_member_settings_pk do update set value=?");
            preparedStatement.setLong(1, member.getGuild().getIdLong());
            preparedStatement.setLong(2, member.getIdLong());
            preparedStatement.setString(3, key);
            preparedStatement.setString(4, value);
            preparedStatement.setString(5, value);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new HiromiDatabaseException("Something went wrong trying to update in the database", e);
        }
    }

    @Override
    public void writeKey(User user, String key, String value) {
        try {
            final Connection connection = getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("insert into user_settings (id, key, value) VALUES(?, ?, ?) on conflict on constraint user_settings_pk do update set value=?");
            preparedStatement.setLong(1, user.getIdLong());
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, value);
            preparedStatement.setString(4, value);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new HiromiDatabaseException("Something went wrong trying to update in the database", e);
        }
    }
}
