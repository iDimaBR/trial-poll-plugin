package com.github.idimabr.storage;

import com.github.idimabr.TrialPoll;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.util.logging.Logger;

public class Database {

    private Logger logger;
    private File dataFolder;
    private TrialPoll instance;

    public static String databaseType = "sqlite";

    public Database(TrialPoll instance) {
        this.instance = instance;
        logger = instance.getLogger();
    }

    public static boolean isSQLITE(){
        return databaseType.equals("sqlite");
    }

    public SQLConnector createConnector(ConfigurationSection section) {
        String databaseType = section.getString("type");
        ConfigurationSection typeSection = section.getConfigurationSection(databaseType);
        if (typeSection == null) {
            throw new IllegalArgumentException("Configuration section for database type is missing: " + databaseType);
        }

        switch (databaseType.toLowerCase()) {
            case "sqlite":
                databaseType = "sqlite";
                return createSQLiteConnector(typeSection);
            case "mysql":
                databaseType = "mysql";
                return createMySQLConnector(typeSection);
            default:
                throw new UnsupportedOperationException("Database type unsupported: " + databaseType);
        }
    }

    private SQLConnector createSQLiteConnector(ConfigurationSection typeSection) {
        dataFolder = new File(instance.getDataFolder(), "database");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logger.severe("Failed to create database directory!");
        }else{
            logger.info("Database directory created!");
        }

        try {
            return SQLiteDatabaseType.builder()
                    .file(new File(dataFolder, typeSection.getString("fileName")))
                    .build()
                    .connect();
        } catch (Exception e) {
            logger.severe("Failed to create SQLite connector: " + e.getMessage());
            throw new RuntimeException("Failed to create SQLite connector", e);
        }
    }

    private SQLConnector createMySQLConnector(ConfigurationSection typeSection) {
        try {
            return MySQLDatabaseType.builder()
                    .address(typeSection.getString("address"))
                    .username(typeSection.getString("username"))
                    .password(typeSection.getString("password"))
                    .database(typeSection.getString("database"))
                    .build()
                    .connect();
        } catch (Exception e) {
            logger.severe("Failed to create MySQL connector: " + e.getMessage());
            throw new RuntimeException("Failed to create MySQL connector", e);
        }
    }
}