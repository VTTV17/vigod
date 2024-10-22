package sql;

import lombok.Data;
import utilities.database.InitConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the retrieval of inventory events from the database.
 */
public class SQLGetInventoryEvent {
    private final Connection connection;

    /**
     * Constructor for GetInventoryEvent.
     *
     * @param connection The active database connection.
     */
    public SQLGetInventoryEvent(Connection connection) {
        this.connection = connection;
    }

    /**
     * Represents an inventory event with details like branch ID, item ID, model ID, order ID, and action.
     */
    @Data
    public static class InventoryEvent {
        private String branch_id;
        private String item_id;
        private String model_id;
        private String order_id;
        private String action;
    }

    /**
     * Retrieves a list of inventory events for a given store and time range.
     *
     * @param storeId   The store ID to filter the events.
     * @param startTime The start of the time range (in 'YYYY-MM-DD HH:MM:SS.mmm' format).
     * @param endTime   The end of the time range (in 'YYYY-MM-DD HH:MM:SS.mmm' format).
     * @return A list of {@link InventoryEvent} objects.
     */
    public List<InventoryEvent> getTiktokInventoryEvents(int storeId, String startTime, String endTime) {
        // SQL query to fetch the inventory events based on store ID and time range
        String query = """
            SELECT DISTINCT
                ie.branch_id,
                ie.item_id,
                ie.model_id,
                ie.order_id,
                ie."action",
                ie.status
            FROM "tiktok-services".shop s
            JOIN "inventory-services".inventory_event ie
                ON s.branch_id = ie.branch_id::INT
            JOIN "tiktok-services".item i
                ON ie.item_id = i.tiktok_item_id
                    OR ie.order_id = i.tiktok_item_id
                    OR ie.item_id = i.bc_item_id::VARCHAR
            WHERE s.bc_store_id = %d
              AND ie.created_date >= '%s'
              AND ie.created_date < '%s'
            ORDER BY ie."action";
            """.formatted(storeId, startTime, endTime);

        // List to hold the parsed inventory events
        List<InventoryEvent> inventoryEvents = new ArrayList<>();

        try {
            // Execute the query and fetch the result set
            ResultSet resultSet = InitConnection.executeSQL(connection, query);

            // Loop through the result set and map rows to InventoryEvent objects
            while (resultSet.next()) {
                InventoryEvent event = new InventoryEvent();
                event.setBranch_id(resultSet.getString("branch_id"));
                event.setItem_id(resultSet.getString("item_id"));
                event.setModel_id(resultSet.getString("model_id"));
                event.setOrder_id(resultSet.getString("order_id"));
                event.setAction(resultSet.getString("action"));

                // Add the event to the list
                inventoryEvents.add(event);
            }
        } catch (SQLException exception) {
            // Throw the exception
            throw new RuntimeException("Error retrieving inventory events from the database", exception);
        }

        // Return the list of inventory events
        return inventoryEvents;
    }
}
