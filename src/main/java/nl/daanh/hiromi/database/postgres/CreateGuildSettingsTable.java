package nl.daanh.hiromi.database.postgres;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateGuildSettingsTable implements IPostgresMigration {
    @Override
    public void migrate(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE hiromi.guild_settings (     id bigint NOT NULL,     \"key\" varchar(255) NOT NULL,     value varchar(255) NOT NULL,     CONSTRAINT guild_settings_pk PRIMARY KEY (id,\"key\") );");
    }
}
