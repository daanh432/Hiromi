package nl.daanh.hiromi.database.postgres;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateGuildMemberSettingsTable implements IPostgresMigration {
    @Override
    public void migrate(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE hiromi.guild_member_settings (  guild_id bigint NOT NULL,  member_id bigint NOT NULL,  \"key\" varchar(255) NOT NULL,  value varchar(255) NOT NULL,  CONSTRAINT guild_member_settings_pk PRIMARY KEY (guild_id,member_id,\"key\") ); ");
    }
}
