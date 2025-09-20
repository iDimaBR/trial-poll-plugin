package com.github.idimabr.storage;

import com.henryfabio.sqlprovider.connector.SQLConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public final class SQLHelper {

    private final SQLConnector sqlConnector;

    public SQLHelper(SQLConnector sqlConnector) {
        this.sqlConnector = sqlConnector;
    }

    public void batchUpdate(String query, Consumer<PreparedStatement> batchConsumer) {
        sqlConnector.consumeConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                batchConsumer.accept(ps);
                ps.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public long updateAndReturn(String query, Consumer<PreparedStatement> consumer) {
        AtomicLong generatedId = new AtomicLong(-1);
        sqlConnector.consumeConnection(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                consumer.accept(stmt);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId.set(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return generatedId.get();
    }
}
