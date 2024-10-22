package sql;

import lombok.Data;
import utilities.database.InitConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the retrieval of inventory mappings from the database.
 */
public class SQLGetInventoryMapping {
    private final Connection connection;

    /**
     * Constructor for GetInventoryMapping.
     *
     * @param connection The active database connection.
     */
    public SQLGetInventoryMapping(Connection connection) {
        this.connection = connection;
    }

    /**
     * Represents an inventory mapping with fields such as branch ID, item ID, model ID, shop ID, stock, and inventory ID.
     */
    @Data
    public static class InventoryMapping {
        private int id;
        private String branch_id;
        private String item_id;
        private String model_id;
        private String shop_id;
        private int stock;
        private String channel;
        private String inventory_id;
    }

    /**
     * Retrieves a list of inventory mappings for a given store ID.
     * This query fetches all inventory mappings whose `inventory_id` starts with the combination of
     * branch ID and item ID from the associated TikTok shop.
     *
     * @param storeId The store ID to filter the mappings.
     * @return A list of {@link InventoryMapping} objects.
     */
    public List<InventoryMapping> getTiktokInventoryMappings(int storeId) {
        // SQL query to fetch inventory mappings based on store ID
        String query = """
            SELECT *
            FROM "inventory-services".inventory_mapping im
            WHERE im.inventory_id LIKE ANY (
                SELECT (s.branch_id || '-' || i.bc_item_id) || '%%'
                FROM "tiktok-services".shop s
                JOIN "tiktok-services".item i
                    ON i.tiktok_shop_id = s.tiktok_shop_id
                WHERE s.bc_store_id = %d
                    AND i.bc_item_id IS NOT NULL
            );
        """.formatted(storeId);

        // List to hold the parsed inventory mappings
        List<InventoryMapping> inventoryMappings = new ArrayList<>();

        // Use try-with-resources to ensure resources are closed properly
        try (ResultSet resultSet = InitConnection.executeSQL(connection, query)) {
            // Loop through the result set and map rows to InventoryMapping objects
            while (resultSet.next()) {
                InventoryMapping mapping = new InventoryMapping();
                mapping.setId(resultSet.getInt("id"));
                mapping.setBranch_id(resultSet.getString("branch_id"));
                mapping.setItem_id(resultSet.getString("item_id"));
                mapping.setModel_id(resultSet.getString("model_id"));
                mapping.setShop_id(resultSet.getString("shop_id"));
                mapping.setStock(resultSet.getInt("stock"));
                mapping.setInventory_id(resultSet.getString("inventory_id"));

                // Add the mapping to the list
                inventoryMappings.add(mapping);
            }
        } catch (SQLException exception) {
            // Handle SQLException and rethrow as RuntimeException
            throw new RuntimeException("Error retrieving inventory mappings from the database", exception);
        }

        // Return the list of inventory mappings
        return inventoryMappings;
    }
}
