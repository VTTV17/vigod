package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class Product{
	private ProductManagement productManagement = new ProductManagement();
	private LocationReceipt locationReceipt = new LocationReceipt();
	private Transfer transfer = new Transfer();
	private Collection collection = new Collection();
	private Review review = new Review();
	private LotDate lotDate = new LotDate();
	private Inventory inventory = new Inventory();
	private Location location = new Location();
}