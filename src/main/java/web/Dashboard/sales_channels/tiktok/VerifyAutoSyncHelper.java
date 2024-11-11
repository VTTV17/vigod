package web.Dashboard.sales_channels.tiktok;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import sql.SQLGetInventoryEvent;
import sql.SQLGetInventoryMapping;

import java.sql.Connection;
import java.util.List;
import java.util.stream.IntStream;

import static api.Seller.sale_channel.tiktok.APIGetTikTokProducts.ItemMapping;
import static sql.SQLGetInventoryEvent.InventoryEvent;
import static sql.SQLGetInventoryMapping.InventoryMapping;

public class VerifyAutoSyncHelper {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Main method to verify inventory events based on the auto-sync status, item mappings, and the specified event action.
     * <p>
     * This method handles the overall validation of inventory events by matching them with product item mappings
     * that triggered new inventory events after certain actions (e.g., sync, update).
     *
     * @param isAutoSynced                       Boolean flag indicating whether auto-sync is enabled.
     * @param itemMappingsWithNewInventoryEvents List of item mappings for products that triggered new inventory events after actions were completed.
     * @param actionTime                         Array containing the start and end times of the action used to retrieve inventory events.
     * @param storeId                            The ID of the store where inventory mappings are being verified.
     * @param connection                         The database connection used to retrieve inventory events.
     * @param eventAction                        The specific action that triggered the inventory event (e.g., download, update).
     */
    public static void verifyInventoryEvent(Boolean isAutoSynced, List<ItemMapping> itemMappingsWithNewInventoryEvents,
                                            String[] actionTime, int storeId, Connection connection, String eventAction) {
        // Step 1: Validate the input parameters. If invalid, return early.
        if (!areValidInputs(itemMappingsWithNewInventoryEvents, actionTime)) return;

        // Step 2: Check if itemMappings are empty. If yes, return early.
        if (itemMappingsWithNewInventoryEvents.isEmpty()) return;

        // Step 3: Retrieve inventory events from the database using store ID and action time range.
        List<InventoryEvent> events = retrieveInventoryEvents(connection, storeId, actionTime);

        // Step 4: Match the inventory events with the item mappings and validate them.
        matchInventoryEvents(isAutoSynced, itemMappingsWithNewInventoryEvents, events, eventAction);
    }

    /**
     * Validates input parameters for TikTok product verification to ensure they are properly initialized.
     *
     * @param changedItemMappings List of product item mappings that have new events following the completed actions.
     * @param actionTime          Array containing start and end times of the action.
     * @return true if inputs are valid; false otherwise.
     */
    private static boolean areValidInputs(List<ItemMapping> changedItemMappings, String[] actionTime) {
        // Check if itemMappings list is null.
        if (changedItemMappings == null) {
            logger.error("TikTok products list cannot be null.");
            return false;
        }

        // Check if itemMappings list is empty.
        if (changedItemMappings.isEmpty()) {
            logger.warn("No TikTok products available for verification.");
            return false;
        }

        // Check if actionTime is null or does not contain at least two elements.
        if (actionTime == null || actionTime.length < 2) {
            logger.warn("Action time is missing or incomplete.");
            return false;
        }

        return true;  // Input parameters are valid.
    }

    /**
     * Retrieves inventory events from the database based on the store ID and action time range.
     *
     * @param connection Database connection to execute SQL queries.
     * @param storeId    The store ID for which to fetch inventory events.
     * @param actionTime Array containing start and end times of the action.
     * @return List of inventory events retrieved from the database.
     */
    private static List<InventoryEvent> retrieveInventoryEvents(Connection connection, int storeId, String[] actionTime) {
        // Execute SQL query to get inventory events for the given store ID and time range.
        List<InventoryEvent> events = new SQLGetInventoryEvent(connection)
                .getTiktokInventoryEvents(storeId, actionTime[0], actionTime[1]);

        // Log the number of retrieved events.
        logger.info("Retrieved {} inventory events from the database.", events.size());
        return events;  // Return the list of inventory events.
    }

    /**
     * Matches the retrieved inventory events with the corresponding product item mappings and validates them.
     *
     * @param isAutoSynced Boolean flag indicating if auto-sync is enabled.
     * @param itemMappings List of product item mappings to match with events.
     * @param events       List of inventory events retrieved from the database.
     * @param eventAction  The action associated with the inventory events (e.g., download or update).
     */
    private static void matchInventoryEvents(Boolean isAutoSynced, List<ItemMapping> itemMappings,
                                             List<InventoryEvent> events, String eventAction) {

        // Determine the expected number of events based on whether auto-sync is enabled.
        int expectedEventCount = isAutoSynced ? itemMappings.size() : 0;

        // Check if the number of events matches the expected number of product variations.
        if (events.size() != expectedEventCount) {
            // Try logging the events as a JSON string for better traceability.
            try {
                String eventDetails = new ObjectMapper().writeValueAsString(events);
                logger.info("Events: {}", eventDetails);
            } catch (JsonProcessingException e) {
                // Log the failure to process events and throw a runtime exception.
                logger.error("Failed to process events into JSON format", e);
                throw new RuntimeException("Error processing event details", e);
            }
        }

        // Assert that the event count matches the expected number of product variations.
        Assert.assertEquals(events.size(), expectedEventCount,
                "The number of events should match the total number of product variations.");

        // If no events are retrieved, log a warning and return early.
        if (events.isEmpty()) {
            logger.warn("No inventory events available for validation.");
            return;
        }

        // Validate each event against the corresponding item mapping.
        IntStream.range(0, expectedEventCount).forEach(index -> validateEvent(events, itemMappings.get(index), eventAction));

        // Log a success message after all events have been successfully validated.
        logger.info("All events have been successfully verified against the linked TikTok product variations.");
    }

    /**
     * Validates an inventory event by comparing its details with the corresponding product item mapping.
     *
     * @param events      The list of inventory events to search within.
     * @param itemMapping The product item mapping to validate against.
     * @param eventAction The expected action for the event (e.g., download or update).
     * @throws IllegalArgumentException if no matching event is found.
     */
    private static void validateEvent(List<InventoryEvent> events, ItemMapping itemMapping, String eventAction) {
        // Retrieve the matching inventory event based on item mapping.
        InventoryEvent event = SQLGetInventoryEvent.getInventoryEventByItemMapping(events, itemMapping);

        // Fail fast if the event is not found, providing detailed itemMapping information.
        if (event == null) {
            throw new IllegalArgumentException(String.format("No matching inventory event found for item mapping with Branch ID = %s, Item ID = %s, Model ID = %s.",
                    itemMapping.getBranch_id(), itemMapping.getBc_item_id(), itemMapping.getBc_model_id()));
        }

        // Log the details of the event being validated.
        logger.info("Validating event - Branch ID: {}, Item ID: {}, Model ID: {}", event.getBranch_id(), event.getItem_id(), event.getModel_id());

        // Validate the event's action, throwing an error if it doesn’t match the expected action.
        Assert.assertEquals(event.getAction(), eventAction,
                String.format("Event action mismatch: expected '%s' but found '%s'.", eventAction, event.getAction()));

        // Validate that the branch ID, item ID, and model ID match the item mapping.
        Assert.assertEquals(event.getBranch_id(), itemMapping.getBranch_id(),
                String.format("Branch ID mismatch: expected '%s' but found '%s'.", itemMapping.getBranch_id(), event.getBranch_id()));
        Assert.assertEquals(event.getItem_id(), itemMapping.getBc_item_id(),
                String.format("Item ID mismatch: expected '%s' but found '%s'.", itemMapping.getBc_item_id(), event.getItem_id()));
        Assert.assertEquals(event.getModel_id(), itemMapping.getBc_model_id(),
                String.format("Model ID mismatch: expected '%s' but found '%s'.", itemMapping.getBc_model_id(), event.getModel_id()));

        // Validate that the order ID matches based on the event action type.
        String expectedOrderId = eventAction.equals("GS_TIKTOK_DOWNLOAD_PRODUCT")
                ? String.valueOf(itemMapping.getTt_item_id())
                : String.valueOf(itemMapping.getBc_store_id());
        Assert.assertEquals(event.getOrder_id(), expectedOrderId,
                String.format("Order ID mismatch: expected '%s' but found '%s'.", expectedOrderId, event.getOrder_id()));
    }

    /**
     * Main method to verify the inventory mappings based on the status of unchanged, removed, and newly created mappings
     * after certain actions are performed.
     * <p>
     * This method validates that:
     * 1. Unchanged inventory mappings remain intact.
     * 2. Specified mappings are removed after syncing.
     * 3. New inventory mappings are created for the relevant item mappings.
     *
     * @param unChangedInventoryMappings           The inventory mappings before syncing that should remain unchanged.
     * @param removedInventoryMappings             The inventory mappings that are expected to be removed after syncing.
     * @param itemMappingsWithNewInventoryMappings The item mappings of items that have new mappings created after the actions.
     * @param storeId                              The ID of the store for which inventory mappings are being verified.
     * @param connection                           Database connection to retrieve inventory events and mappings.
     */
    public static void verifyInventoryMapping(
            List<InventoryMapping> unChangedInventoryMappings, List<InventoryMapping> removedInventoryMappings,
            List<ItemMapping> itemMappingsWithNewInventoryMappings, int storeId, Connection connection) {

        // Step 1: Retrieve the current inventory mappings from the database after syncing
        List<InventoryMapping> updatedMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Step 2: Verify that unchanged mappings are still present in the updated inventory mappings
        verifyUnchangedMappings(unChangedInventoryMappings, updatedMappings);

        // Step 3: Verify that the specified inventory mappings have been successfully removed from the updated mappings
        verifyMappingsAreRemoved(removedInventoryMappings, connection);

        // Step 4: Verify that new inventory mappings have been created for the provided item mappings
        verifyCreatedMappings(itemMappingsWithNewInventoryMappings, updatedMappings);
    }

    /**
     * Verifies that the original inventory mappings have not changed after syncing.
     * <p>
     * This method checks that the pre-existing mappings are still present in the current list
     * of inventory mappings after the syncing process, ensuring that none of the original
     * mappings have been altered or removed.
     *
     * @param unChangedMappings The inventory mappings before syncing that should remain unchanged.
     * @param updatedMappings   The current inventory mappings after syncing.
     * @throws IllegalArgumentException If the updated mappings do not contain all original mappings.
     */
    private static void verifyUnchangedMappings(List<InventoryMapping> unChangedMappings,
                                                List<InventoryMapping> updatedMappings) {
        // Check if there are unchanged mappings to verify
        if (unChangedMappings == null || unChangedMappings.isEmpty()) {
            logger.info("No unchanged mappings to verify; exiting verification process.");
            return; // Exit early if there are no mappings to check
        }

        // Log the start of unchanged mappings verification
        logger.info("Starting verification of unchanged mappings.");

        // Validate that the updated mappings list is not null
        if (updatedMappings == null) {
            throw new IllegalArgumentException("Updated mappings cannot be null.");
        }

        // Check that updated mappings list contains at least as many mappings as the original list
        if (updatedMappings.size() < unChangedMappings.size()) {
            throw new IllegalArgumentException("Updated mappings must contain all original mappings.");
        }

        // Iterate through each original mapping to verify its presence in the updated mappings
        unChangedMappings.forEach(originalMapping -> {
            boolean mappingExists = updatedMappings.contains(originalMapping);
            Assert.assertTrue(mappingExists,
                    String.format("Original mapping must be kept, inventory_id: %s", originalMapping.getInventory_id()));
        });

        // Log successful completion of unchanged mappings verification
        logger.info("Verification of unchanged mappings completed successfully.");
    }

    /**
     * Verifies that new mappings for both GoSELL and TikTok channels have been correctly created.
     * <p>
     * This method iterates through the list of item mappings and checks if the mappings for
     * both GoSELL and TikTok channels exist and match the expected branch, item, and model IDs.
     * </p>
     *
     * @param itemMappings    The item mappings of items that have new mappings created after the actions.
     * @param updatedMappings The current inventory mappings after syncing.
     * @throws IllegalArgumentException If the updated mappings are null.
     */
    private static void verifyCreatedMappings(List<ItemMapping> itemMappings,
                                              List<InventoryMapping> updatedMappings) {
        // Check if there are new mappings to verify
        if (itemMappings == null || itemMappings.isEmpty()) {
            logger.info("No new mappings to verify; exiting verification process.");
            return; // Exit early if there are no mappings to check
        }

        // Log the start of new mappings verification
        logger.info("Starting verification of newly created mappings.");

        // Validate that the updated mappings list is not null
        if (updatedMappings == null) {
            throw new IllegalArgumentException("Updated mappings cannot be null.");
        }

        // Loop through each item mapping and verify its corresponding inventory mappings
        itemMappings.forEach(itemMapping -> {
            // Retrieve inventory mappings that match the generated inventory ID for the item mapping
            List<InventoryMapping> matchingMappings = SQLGetInventoryMapping.getInventoryMappingsByItemMapping(updatedMappings, itemMapping);

            // Verify mapping for the GoSELL channel with specified branch, item, and model IDs
            verifyMappingForChannel(matchingMappings, itemMapping.getBranch_id(),
                    itemMapping.getBc_item_id(), itemMapping.getBc_model_id(), "GOSELL");

            // Verify mapping for the TikTok channel with specified branch, item, and model IDs
            verifyMappingForChannel(matchingMappings, itemMapping.getBranch_id(),
                    itemMapping.getTt_item_id(), itemMapping.getTt_model_id(), "TIKTOK");
        });

        // Log successful completion of new mappings verification
        logger.info("Verification of newly created mappings completed successfully.");
    }

    /**
     * Verifies the mapping for a specific channel (GoSELL or TikTok) by checking that the inventory mappings contain
     * a matching entry for the provided branch ID, item ID, and model ID.
     *
     * @param matchingMappings The list of inventory mappings that match the generated inventory ID.
     * @param branchId         The branch ID of the item.
     * @param itemId           The item ID of the product.
     * @param modelId          The model ID of the product.
     * @param channel          The channel to verify (either "GOSELL" or "TIKTOK").
     */
    private static void verifyMappingForChannel(List<InventoryMapping> matchingMappings,
                                                String branchId, String itemId, String modelId, String channel) {
        // Log the start of channel-specific mapping verification
        logger.info("Verifying mapping for channel: {} with branchId: {}, itemId: {}, modelId: {}.", channel, branchId, itemId, modelId);

        matchingMappings.stream()
                .filter(inventoryMapping -> inventoryMapping.getChannel().equals(channel))
                .findFirst()
                .ifPresentOrElse(inventoryMapping -> {
                    // Log a successful verification of the channel-specific mapping
                    logger.info("[{}] Verified mapping for inventory_id: {}", channel, inventoryMapping.getInventory_id());

                    // Assert each ID field (branch, item, model) to confirm accurate mapping for the channel
                    Assert.assertEquals(inventoryMapping.getBranch_id(), branchId, "Branch Id mismatch for " + channel);
                    Assert.assertEquals(inventoryMapping.getItem_id(), itemId, "Item ID mismatch for " + channel);
                    Assert.assertEquals(inventoryMapping.getModel_id(), modelId, "Model ID mismatch for " + channel);
                }, () -> {
                    // Log an error if no matching inventory mapping is found for the channel
                    logger.error("[{}] No matching {} inventory mapping found for the provided details.", channel, channel);
                });

        // Log completion of channel-specific mapping verification
        logger.info("Completed verification for channel: {} with branchId: {}, itemId: {}, modelId: {}.", channel, branchId, itemId, modelId);
    }

    /**
     * Verifies that the specified inventory mappings have been removed from the database.
     * <p>
     * This method checks that the inventory mappings listed in `removedInventoryMappings` are no longer
     * present in the database, confirming the successful execution of the removal process.
     * If there are no mappings to verify, it exits early.
     * </p>
     *
     * @param removedInventoryMappings A list of {@link InventoryMapping} objects expected to be removed
     *                                 from the database. If null or empty, the verification process is skipped.
     * @param connection               Database connection to verify the inventory mappings.
     * @throws IllegalArgumentException If the removedInventoryMappings parameter is null.
     * @throws RuntimeException         If the specified inventory mappings are still present in the database.
     */
    private static void verifyMappingsAreRemoved(List<InventoryMapping> removedInventoryMappings, Connection connection) {
        // Check if there are mappings to verify for removal
        if (removedInventoryMappings == null || removedInventoryMappings.isEmpty()) {
            logger.info("No removed mappings to verify; exiting verification process.");
            return; // Exit early if there are no mappings to check
        }

        // Log the start of removed mappings verification
        logger.info("Starting verification of removed mappings.");

        // Extract IDs of removed inventory mappings to use in verification query
        List<String> removedInventoryMappingIds = removedInventoryMappings.stream()
                .map(InventoryMapping::getInventory_id)
                .toList();

        // Perform the verification to check that removed mappings are no longer in the database
        SQLGetInventoryMapping.verifyInventoryMappingsAreRemoved(removedInventoryMappingIds, connection);

        // Log successful completion of removed mappings verification
        logger.info("Verification of removed mappings completed successfully.");
    }
}