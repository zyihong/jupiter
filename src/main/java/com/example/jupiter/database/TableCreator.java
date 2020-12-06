package com.example.jupiter.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class TableCreator {
    public static void main(String[] args) {
        // reset the database
        try {
            System.out.println("Connect to " + MySQLDBUtil.getMySQLAddress());

            // prevent corner case
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL
            Connection conn = DriverManager.getConnection(MySQLDBUtil.getMySQLAddress());

            if (conn == null) return;

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS favorite");
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("DROP TABLE IF EXISTS items");

            String createTableSQL;
            createTableSQL = "CREATE TABLE items (" +
                    "id VARCHAR(255) NOT NULL," +
                    "title VARCHAR(255)," +
                    "url VARCHAR(255)," +
                    "thumbnail_url VARCHAR(255)," +
                    "broadcaster_name VARCHAR(255)," +
                    "game_id VARCHAR(255)," +
                    "type VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ")";
            stmt.executeUpdate(createTableSQL);

            createTableSQL = "CREATE TABLE users (" +
                    "id VARCHAR(255) NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "first_name VARCHAR(255)," +
                    "last_name VARCHAR(255)," +
                    "PRIMARY KEY (id)" +
                    ")";
            stmt.executeUpdate(createTableSQL);

            createTableSQL = "CREATE TABLE favorite (" +
                    "user_id VARCHAR(255) NOT NULL," +
                    "item_id VARCHAR(255) NOT NULL," +
                    "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (user_id, item_id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (item_id) REFERENCES items(id)" +
                    ")";
            stmt.executeUpdate(createTableSQL);

            stmt.executeUpdate("INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')");

            conn.close();
            System.out.println("Import done successfully");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
