package com.example.jupiter.database;

import com.example.jupiter.entity.Item;
import com.example.jupiter.entity.ItemType;
import com.example.jupiter.entity.User;

import java.sql.*;
import java.util.*;

public class MySQLClient {
    private final Connection conn;

    public MySQLClient() throws MySQLException {
        try {
            // Prevent corner case.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL.
            conn = DriverManager.getConnection(MySQLDBUtil.getMySQLAddress());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to connect to Database!");
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addItem(Item item) throws MySQLException {
        if (conn != null) {
            String insertSql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = conn.prepareStatement(insertSql);
                stmt.setString(1, item.getId());
                stmt.setString(2, item.getTitle());
                stmt.setString(3, item.getUrl());
                stmt.setString(4, item.getThumbnailUrl());
                stmt.setString(5, item.getBroadcasterName());
                stmt.setString(6, item.getGameId());
                stmt.setString(7, item.getType().toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Insert new item into database fail!");
            }
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    public void addFavorite(String uid, Item item) throws MySQLException {
        if (conn != null) {
            String insertSql = "INSERT IGNORE INTO favorite (user_id, item_id) VALUES (?, ?)";

            // Make sure item exist in the database before update favorite.
            addItem(item);
            try {
                PreparedStatement stmt = conn.prepareStatement(insertSql);
                stmt.setString(1, uid);
                stmt.setString(2, item.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Insert new favorite into database fail!");
            }
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    public void removeFavorite(String uid, String itemId) throws MySQLException {
        if (conn != null) {
            String deleteSql = "DELETE FROM favorite WHERE user_id = ? AND item_id = ?";

            try {
                PreparedStatement stmt = conn.prepareStatement(deleteSql);
                stmt.setString(1, uid);
                stmt.setString(2, itemId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Delete favorite record from database fail!");
            }
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    public Set<String> getFavoriteItemIds(String uid) throws MySQLException {
        if (conn != null) {
            String query = "SELECT item_id FROM favorite WHERE user_id = ?";
            Set<String> items = new HashSet<>();

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, uid);
                ResultSet rst = stmt.executeQuery();

                while (rst.next()) {
                    items.add(rst.getString("item_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Query favorite item ids fail!");
            }

            return items;
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    public Map<String, List<Item>> getFavoriteItems(String uid) throws MySQLException {
        if (conn != null) {
            String query = "SELECT t.* FROM favorite f INNER JOIN items t WHERE f.user_id = ? AND t.id = f.item_id";
            Map<String, List<Item>> items = new HashMap<>();
            for (ItemType type : ItemType.values()) {
                items.put(type.toString(), new ArrayList<>());
            }

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, uid);
                ResultSet rst = stmt.executeQuery();

                while (rst.next()) {
                    ItemType itemType = ItemType.valueOf(rst.getString("type"));
                    Item item = new Item.Builder()
                            .setId(rst.getString("id"))
                            .setTitle(rst.getString("title"))
                            .setUrl(rst.getString("url"))
                            .setThumbnailUrl(rst.getString("thumbnail_url"))
                            .setBroadcasterName(rst.getString("broadcaster_name"))
                            .setGameId(rst.getString("game_id"))
                            .setType(itemType)
                            .build();

                    items.get(rst.getString("type")).add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Query favorite item ids fail!");
            }

            return items;
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    public Map<String, List<String>> getFavoriteGameIds(Set<String> favoriteItemIds)
            throws MySQLException {
        if (conn != null) {
            Map<String, List<String>> games = new HashMap<>();
            for (ItemType type : ItemType.values()) {
                games.put(type.toString(), new ArrayList<>());
            }

            String query = "SELECT game_id, type FROM items WHERE id = ?";

            try {
                PreparedStatement statement = conn.prepareStatement(query);
                for (String itemId : favoriteItemIds) {
                    statement.setString(1, itemId);
                    ResultSet rs = statement.executeQuery();
                    if (rs.next()) {
                        games.get(rs.getString("type")).add(rs.getString("game_id"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Failed to get favorite game ids from Database");
            }

            return games;
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    // Return name is success else NULL.
    public String verifyUser(String usrId, String password) throws MySQLException {
        if (conn != null) {
            String name = null;

            String query = "SELECT first_name, last_name FROM users WHERE id = ? AND password = ?";

            try {
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, usrId);
                statement.setString(2, password);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    name = rs.getString("first_name") + " " + rs.getString("last_name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Failed to verify a user");
            }

            return name;
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }

    public boolean register(User user) throws MySQLException {
        if (conn != null) {
            String query = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";

            try {
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, user.getUsrId());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getFirstName());
                statement.setString(4, user.getLastName());
                return statement.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MySQLException("Failed to register a new user");
            }
        }
        else {
            throw new MySQLException("No database connection!");
        }
    }
}