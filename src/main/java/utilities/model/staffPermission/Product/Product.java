package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class Product{
	private ProductManagement productManagement;
	private LocationReceipt locationReceipt;
	private Transfer transfer;
	private Collection collection;
	private Review review;
	private LotDate lotDate;
	private Inventory inventory;
	private Location location;
}