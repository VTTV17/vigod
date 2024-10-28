package sql;

import lombok.Data;
import utilities.database.InitConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
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
     * Retrieves a sorted list of inventory events for a given store and time range.
     * The events are first fetched from the database based on the store ID and time range.
     * After retrieval, they are sorted by {@code item_id} and, in case of matching {@code item_id}s, by {@code model_id}.
     *
     * @param storeId   The store ID to filter the events.
     * @param startTime The start of the time range in 'YYYY-MM-DD HH:MM:SS.mmm' format.
     * @param endTime   The end of the time range in 'YYYY-MM-DD HH:MM:SS.mmm' format.
     * @return A sorted list of {@link InventoryEvent} objects.
     *         The list is sorted by {@code item_id}, and if {@code item_id}s are equal, by {@code model_id}.
     * @throws RuntimeException if there is an error retrieving inventory events from the database.
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

        // Use try-with-resources to ensure resources are closed properly
        try (ResultSet resultSet = InitConnection.executeSQL(connection, query)) {
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

        // Sort the inventory events by item_id, and if item_id is the same, then by model_id
        inventoryEvents.sort(Comparator.comparing(InventoryEvent::getItem_id)
                        .thenComparing(InventoryEvent::getModel_id));

        // Return the list of inventory events
        return inventoryEvents;
    }
}
