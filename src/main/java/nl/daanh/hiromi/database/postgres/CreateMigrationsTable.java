package nl.daanh.hiromi.database.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateMigrationsTable implements IPostgresMigration {
    @Override
    public void migrate(Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hiromi.migrations (  id serial NOT NULL,  \"name\" varchar(255) NOT NULL,  \"date\" timestamp(0) NOT NULL DEFAULT now(),  CONSTRAINT migrations_pk PRIMARY KEY (id),  CONSTRAINT migrations_un UNIQUE (name) );");
        statement.execute();
    }
}
