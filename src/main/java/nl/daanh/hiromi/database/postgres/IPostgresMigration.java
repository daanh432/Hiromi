package nl.daanh.hiromi.database.postgres;

import java.sql.Connection;
import java.sql.SQLException;

public interface IPostgresMigration {
    void migrate(Connection connection) throws SQLException;
}
