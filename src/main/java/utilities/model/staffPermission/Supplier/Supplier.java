package utilities.model.staffPermission.Supplier;

import lombok.Data;

@Data
public class Supplier{
	private boolean viewSupplierDetail;
	private boolean addSupplier;
	private boolean editSupplier;
	private boolean viewSupplierList;
	private boolean deleteSupplier;
}