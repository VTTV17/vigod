package sql;

import api.Seller.sale_channel.tiktok.APIGetTikTokProducts;
import lombok.Data;
import sql.SQLGetInventoryEvent.InventoryEvent;
import utilities.commons.UICommonAction;
import utilities.database.InitConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * This class handles the retrieval of inventory events from the database.
 */
public class SQLGetInventoryEvent {
    private final Connection connection;

    private final static int MAX_RETRIES = 5;
    private final static int DELAY_IN_MS_BEFORE_RETRY = 3000;
    
    /**
     * SQL query for fetching mapping events of a product without variations
     */
    public final static String SQL_FETCHING_EVENTS_NOVARS = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC";
    /**
     * SQL query for fetching mapping events of a product with variations
     */
    public final static String SQL_FETCHING_EVENTS_VARS = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC";
    
    
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
     * The list is sorted by {@code item_id}, and if {@code item_id}s are equal, by {@code model_id}.
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

    //TODO add function description
    public List<InventoryEvent> getShopeeInventoryEvents(String sqlQuery) {

        System.out.println(sqlQuery);

        List<InventoryEvent> accumulatedEvents = new ArrayList<>();
        try (ResultSet resultSet = InitConnection.executeSQL(connection, sqlQuery)) {
            while (resultSet.next()) {
                InventoryEvent event = new InventoryEvent();
                event.setBranch_id(resultSet.getString("branch_id"));
                event.setItem_id(resultSet.getString("item_id"));
                event.setModel_id(resultSet.getString("model_id"));
                event.setOrder_id(resultSet.getString("order_id"));
                event.setAction(resultSet.getString("action"));

                //Accumulate inventory events
                accumulatedEvents.add(event);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error retrieving inventory events from the database", exception);
        }

        return accumulatedEvents;
    }
    /**
     * Oftentimes database records are not generated the moment an SQL is executed.
     * This function waits until at least one record is present thanks to a retry mechanism.
     * @param sqlQuery
     * @return a list of InventoryEvent objects representing mapping events
     */
	public List<InventoryEvent> waitUntilEventRecordsAppear(String sqlQuery){
		List<InventoryEvent> inventoryEventList = new ArrayList<>();
		for (int i=0; i<MAX_RETRIES; i++) {
			inventoryEventList = getShopeeInventoryEvents(sqlQuery);
			
			if (!inventoryEventList.isEmpty()) return inventoryEventList;
			
			UICommonAction.sleepInMiliSecond(DELAY_IN_MS_BEFORE_RETRY, "Events are not present. Wait a little");
		}
		return inventoryEventList;
	}    
    
    public List<InventoryEvent> inventoryEventListByItem(int branchId, long itemId, String startTime) {
        String query = """
                select *
                from "inventory-services".inventory_event ie
                where ie .branch_id = '%s' and ie.item_id =  '%s' and created_date > '%s'
                """.formatted(branchId, itemId, startTime);
        List<InventoryEvent> inventoryEvents = getInventoryEventByQuery(query);
        System.out.println("Get event query: "+query);
        return inventoryEvents;
    }

    public List<InventoryEvent> getInventoryEventByQuery(String query) {
        // List to hold the parsed inventory events
        List<InventoryEvent> inventoryEvents = new ArrayList<>();

        // Use try-with-resources to ensure resources are closed properly
        try (ResultSet resultSet = InitConnection.executeSQL(connection, query)) {
            // Loop through the result set and map rows to InventoryEvent objects
            while (resultSet.next()) {
                InventoryEvent event = new InventoryEvent();
                event.setBranch_id(resultSet.getString("branch_id"));
                event.setItem_id(resultSet.getString("item_id"));
                String modelId = resultSet.getString("model_id");
                event.setModel_id(modelId != null ? modelId : "0");
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

    public static InventoryEvent getInventoryEventByItemMapping(List<InventoryEvent> inventoryEvents, APIGetTikTokProducts.ItemMapping itemMapping) {
        return inventoryEvents.stream()
                .filter(inventoryEvent -> Objects.equals(inventoryEvent.getItem_id(), itemMapping.getBc_item_id()) && Objects.equals(inventoryEvent.getModel_id(), itemMapping.getBc_model_id()))
                .findFirst()
                .orElse(null);
    }
}
