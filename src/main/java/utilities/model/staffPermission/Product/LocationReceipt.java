package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class LocationReceipt{
	private boolean viewAddProductLocationReceiptDetail;
	private boolean deleteDraftGetProductReceipt;
	private boolean importProductToLocation;
	private boolean viewAddProductLocationReceiptList;
	private boolean viewGetProductLocationReceiptDetail;
	private boolean createCompletedAddProductReceipt;
	private boolean deleteDraftAddProductReceipt;
	private boolean editGetProductReceipt;
	private boolean editAddProductReceipt;
	private boolean createDraftAddProductReceipt;
	private boolean createDraftGetProductReceipt;
	private boolean viewGetProductLocationReceiptList;
	private boolean createCompletedGetProductReceipt;
	private boolean completeAddProductReceipt;
	private boolean completeGetProductReceipt;
}