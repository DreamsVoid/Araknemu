package fr.quatrevieux.araknemu.core.dbal;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple implementation of connection pool
 */
final public class SimpleConnectionPool implements ConnectionPool {
    final private Driver driver;
    final private BlockingQueue<Connection> connections;
    final private Logger logger;

    public SimpleConnectionPool(Driver driver, int size, Logger logger) {
        this.driver = driver;
        this.logger = logger;
        this.connections = new ArrayBlockingQueue<>(size);
    }

    public SimpleConnectionPool(Driver driver, int size) {
        this(driver, size, NOPLogger.NOP_LOGGER);
    }

    @Override
    public void initialize() throws SQLException {
        int toInitialize = Math.min(connections.remainingCapacity(), 8);

        while (toInitialize-- > 0) {
            connections.offer(
                driver.newConnection()
            );
        }
    }

    @Override
    public Connection acquire() throws SQLException {
        if (connections.isEmpty()) {
            logger.warn("Pool is empty : create a new connection. If this message occurs too many times consider increase poolSize value");
            return driver.newConnection();
        }

        try {
            return connections.take();
        } catch (InterruptedException e) {
            return driver.newConnection();
        }
    }

    @Override
    public void release(Connection connection) {
        try {
            if (connection.isClosed()) {
                return;
            }
        } catch (SQLException e) { }

        // Cannot post the connection, close the connection
        if (!connections.offer(connection)) {
            try {
                connection.close();
            } catch (SQLException e) { }
        }
    }

    @Override
    public int size() {
        return connections.size();
    }

    @Override
    public void close() {
        logger.info("Closing database connections...");

        Collection<Connection> toClose = new ArrayList<>(connections);
        connections.clear();

        for (Connection connection : toClose) {
            try {
                connection.close();
            } catch (SQLException e) { }
        }
    }
}
