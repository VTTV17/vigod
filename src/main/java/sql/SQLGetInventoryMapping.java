package sql;

import api.Seller.sale_channel.tiktok.APIGetTikTokProducts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.testng.Assert;
import utilities.database.InitConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * Retrieves a list of inventory mappings for a specified store ID.
     * <p>
     * This method executes a SQL query to fetch all inventory mappings from the
     * "inventory-services" schema where the `inventory_id` starts with the
     * combination of branch ID and item ID associated with the TikTok shop for
     * the specified store. The method handles SQL exceptions and ensures that
     * database resources are properly closed after use.
     * </p>
     *
     * @param storeId The ID of the store for which to filter the inventory mappings.
     *                Must be a valid store ID that exists in the database.
     * @return A list of {@link InventoryMapping} objects containing the inventory
     * details for the specified store. The list may be empty if no
     * inventory mappings are found.
     * @throws RuntimeException if there is an error executing the SQL query or
     *                          retrieving the inventory mappings.
     */
    public List<InventoryMapping> getTiktokInventoryMappings(int storeId) {
        // SQL query to fetch inventory mappings based on store ID
        String query = """
                SELECT *
                FROM "inventory-services".inventory_mapping im_outer
                WHERE im_outer.inventory_id IN (
                    SELECT im.inventory_id
                    FROM "inventory-services".inventory_mapping im
                    WHERE im.item_id IN (
                        SELECT i.tiktok_item_id
                        FROM "tiktok-services".shop s
                        JOIN "tiktok-services".item i
                          ON i.tiktok_shop_id = s.tiktok_shop_id
                        WHERE s.bc_store_id = %s
                          AND i.bc_item_id IS NOT NULL
                    )
                )
                AND im_outer.channel IN ('GOSELL', 'TIKTOK');
                """.formatted(storeId);

        return executeInventoryMappingQuery(query);
    }

    /**
     * Retrieves a list of inventory mappings that correspond to a specific TikTok product.
     * <p>
     * This method filters the provided inventory mappings list based on the TikTok product's
     * third-party item ID, selecting only those mappings that are related to the specified product
     * and are associated with the 'GOSELL' and 'TIKTOK' channels.
     * </p>
     * <p>
     * For TikTok products without a link error status, the method expects that for each variation,
     * there will be two corresponding inventory mappings: one for the 'GOSELL' channel and one
     * for the 'TIKTOK' channel. If the TikTok product has a link error status, no inventory mappings
     * are expected for it.
     * </p>
     *
     * @param tikTokProduct     The TikTok product used to generate the inventory ID, which is used
     *                          to identify the relevant inventory mappings.
     * @param inventoryMappings The full list of inventory mappings from which to filter. This list
     *                          may contain mappings for multiple items and channels.
     * @return A filtered list of inventory mappings that match the generated inventory ID. The list
     *         will contain 2 mappings per product variation (one for each channel) if the TikTok product
     *         has no link error status; otherwise, it will contain no mappings for products with link errors.
     * @throws RuntimeException If an error occurs while processing inventory mappings or if the
     *                          count of matched mappings is unexpected.
     */
    public static List<InventoryMapping> getInventoryMappingsByItem(
            APIGetTikTokProducts.TikTokProduct tikTokProduct,
            List<InventoryMapping> inventoryMappings) {

        // Collect inventory IDs corresponding to the TikTok product's third-party item ID
        List<String> inventoryIds = inventoryMappings.stream()
                .filter(inventoryMapping -> inventoryMapping.getItem_id().equals(tikTokProduct.getThirdPartyItemId()))
                .map(InventoryMapping::getInventory_id)
                .toList();

        // Filter inventory mappings by matching IDs
        List<InventoryMapping> itemInventoryMappings = inventoryMappings.stream()
                .filter(inventoryMapping -> inventoryIds.contains(inventoryMapping.getInventory_id()))
                .toList();

        // Calculate expected inventory mapping count (2 per variation if no link error, 0 if link error exists)
        int expectedInventoryMappingRecords = tikTokProduct.getHasLinkErrorStatus()
                ? 0
                : tikTokProduct.getVariations().size() * 2;

        // Verify the count of inventory mappings matches expectations
        Assert.assertEquals(itemInventoryMappings.size(), expectedInventoryMappingRecords,
                "Expected " + expectedInventoryMappingRecords + " inventory mappings (2 for each variation: one for GoSELL and one for TikTok, if no link error).");

        // Return the filtered list of inventory mappings
        return itemInventoryMappings;
    }

    /**
     * Retrieves a list of inventory mappings corresponding to a list of TikTok products.
     * <p>
     * This method processes each TikTok product in the provided list by calling
     * {@link #getInventoryMappingsByItem(APIGetTikTokProducts.TikTokProduct, List<InventoryMapping>)}
     * to fetch the inventory mappings for each product. The resulting lists of mappings for each
     * product are then flattened into a single list containing all inventory mappings for the provided
     * TikTok products.
     * </p>
     *
     * @param tikTokProducts    A list of {@link APIGetTikTokProducts.TikTokProduct} objects
     *                          for which inventory mappings are to be retrieved. Each product
     *                          should have a valid third-party item ID that corresponds to
     *                          inventory data in the database.
     * @param inventoryMappings The list of all inventory mappings from which to filter.
     *                          This list may include mappings for various products and channels.
     * @return A list of {@link InventoryMapping} objects containing the inventory mappings
     *         for the specified TikTok products. The list may be empty if no mappings are
     *         found for the provided products.
     */
    public static List<InventoryMapping> getInventoryMappingsByItems(
            List<APIGetTikTokProducts.TikTokProduct> tikTokProducts,
            List<InventoryMapping> inventoryMappings) {

        // Process each TikTok product to get its inventory mappings, then flatten the results
        return tikTokProducts.stream()
                .map(tikTokProduct -> getInventoryMappingsByItem(tikTokProduct, inventoryMappings))
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Verifies that specific inventory mappings have been removed based on a list of inventory IDs.
     * <p>
     * Executes a SQL query to check for inventory mappings in the "inventory-services" schema
     * where `inventory_id` matches any of the provided IDs. Ensures that no mappings remain for
     * the given IDs and logs details if they are still present.
     * </p>
     *
     * @param inventoryIds A list of inventory IDs to check for removal.
     * @throws RuntimeException if the specified inventory mappings are found or if a database error occurs.
     */
    public static void verifyInventoryMappingsAreRemoved(List<String> inventoryIds, Connection connection) {
        // Build SQL query to fetch inventory mappings based on provided inventory IDs
        String query = """
                    SELECT *
                    FROM "inventory-services".inventory_mapping im
                    WHERE im.inventory_id LIKE ANY (ARRAY[%s])
                """.formatted(inventoryIds.stream()
                .map(id -> "'%" + id + "%'")
                .collect(Collectors.joining(", ")));

        try (ResultSet resultSet = InitConnection.executeSQL(connection, query)) {
            // Check if any mappings are found
            if (!resultSet.isBeforeFirst()) {
                // No results, so mappings are confirmed removed
                return;
            }

            // List to hold any mappings found
            List<InventoryMapping> inventoryMappings = new ArrayList<>();

            // Loop through result set and map rows to InventoryMapping objects
            do {
                inventoryMappings.add(createInventoryMapping(resultSet));
            } while (resultSet.next());

            // Log details of mappings that were expected to be removed but still exist
            LogManager.getLogger().error("Inventory mappings not removed: {}",
                    new ObjectMapper().writeValueAsString(inventoryMappings));

            // Throw exception indicating mappings were not removed as expected
            throw new RuntimeException("Specific inventory mappings are not removed as expected.");

        } catch (SQLException | JsonProcessingException e) {
            // Handle SQL or JSON processing exceptions and rethrow as RuntimeException
            throw new RuntimeException("Error during inventory mapping verification", e);
        }
    }

    /**
     * Executes a SQL query to retrieve inventory mappings and maps the result set to a list of InventoryMapping objects.
     *
     * @param query The SQL query to execute.
     * @return A list of InventoryMapping objects populated from the query result.
     */
    private List<InventoryMapping> executeInventoryMappingQuery(String query) {
        List<InventoryMapping> inventoryMappings = new ArrayList<>();

        // Use try-with-resources to ensure resources are closed properly
        try (ResultSet resultSet = InitConnection.executeSQL(connection, query)) {
            // Loop through the result set and map rows to InventoryMapping objects
            while (resultSet.next()) {
                inventoryMappings.add(createInventoryMapping(resultSet));
            }
        } catch (SQLException exception) {
            // Handle SQLException and rethrow as RuntimeException
            throw new RuntimeException("Error retrieving inventory mappings from the database", exception);
        }

        // Sort the inventory mappings by inventory ID, then by channel
        inventoryMappings.sort(Comparator.comparing(InventoryMapping::getInventory_id)
                .thenComparing(InventoryMapping::getChannel));

        // Return the sorted list of inventory mappings
        return inventoryMappings;
    }

    /**
     * Retrieves a list of inventory mappings that correspond to a specific item mapping.
     * <p>
     * This method generates a unique inventory ID based on the branch ID, item ID, and model ID
     * from the provided item mapping, and then filters the updated inventory mappings to find
     * those that match the generated inventory ID.
     * </p>
     *
     * @param inventoryMappings The current list of updated inventory mappings to search through.
     * @param itemMapping       The item mapping used to generate the inventory ID.
     * @return A list of inventory mappings that match the generated inventory ID.
     */
    public static List<InventoryMapping> getInventoryMappingsByItemMapping(List<InventoryMapping> inventoryMappings, APIGetTikTokProducts.ItemMapping itemMapping) {
        // Generate a unique inventory ID based on the branch, item, and model IDs
        String inventoryId = "%s-%s-%s".formatted(itemMapping.getBranch_id(), itemMapping.getBc_item_id(), itemMapping.getBc_model_id());

        // Filter the updated mappings to find those that match the generated inventory ID
        return inventoryMappings.stream()
                .filter(inventoryMapping -> inventoryMapping.getInventory_id().equals(inventoryId))
                .toList(); // Return the list of matching inventory mappings
    }

    /**
     * Creates an InventoryMapping object from a ResultSet.
     *
     * @param resultSet The ResultSet containing the data.
     * @return An InventoryMapping object populated with the data from the ResultSet.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private static InventoryMapping createInventoryMapping(ResultSet resultSet) throws SQLException {
        InventoryMapping mapping = new InventoryMapping();
        mapping.setId(resultSet.getInt("id"));
        mapping.setBranch_id(resultSet.getString("branch_id"));
        mapping.setItem_id(resultSet.getString("item_id"));
        mapping.setModel_id(resultSet.getString("model_id"));
        mapping.setShop_id(resultSet.getString("shop_id"));
        mapping.setStock(resultSet.getInt("stock"));
        mapping.setChannel(resultSet.getString("channel"));
        mapping.setInventory_id(resultSet.getString("inventory_id"));
        return mapping;
    }
    
    //TODO add function description
    public List<InventoryMapping> getMappingRecords(String sqlQuery) {
    	System.out.println(sqlQuery);
    	
    	List<InventoryMapping> accumulatedRecords = new ArrayList<>();
    	try (ResultSet resultSet = InitConnection.executeSQL(connection, sqlQuery)) {
    		while (resultSet.next()) {
    	        InventoryMapping mapping = new InventoryMapping();
    	        mapping.setId(resultSet.getInt("id"));
    	        mapping.setBranch_id(resultSet.getString("branch_id"));
    	        mapping.setItem_id(resultSet.getString("item_id"));
    	        mapping.setModel_id(resultSet.getString("model_id"));
    	        mapping.setShop_id(resultSet.getString("shop_id"));
    	        mapping.setStock(resultSet.getInt("stock"));
    	        mapping.setChannel(resultSet.getString("channel"));
    	        mapping.setInventory_id(resultSet.getString("inventory_id"));
    	        
    	        accumulatedRecords.add(mapping);
    		}
    	} catch (SQLException exception) {
    		throw new RuntimeException("Error retrieving inventory mapping records from the database", exception);
    	}
    	
    	return accumulatedRecords;
    }
}
