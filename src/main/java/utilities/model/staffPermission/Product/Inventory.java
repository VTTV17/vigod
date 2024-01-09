package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class Inventory{
	private boolean exportInventoryHistory;
	private boolean viewProductInventory;
	private boolean viewInventoryHistory;
	private boolean updateStock;
	private boolean clearStock;
	private boolean downloadExportedProduct;
	private boolean viewCreatedProductInventory;
}