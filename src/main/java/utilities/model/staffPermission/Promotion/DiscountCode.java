package utilities.model.staffPermission.Promotion;

import lombok.Data;

@Data
public class DiscountCode{
	private boolean editProductDiscountCode;
	private boolean endProductDiscountCode;
	private boolean createProductDiscountCode;
	private boolean viewProductDiscountCodeDetail;
	private boolean viewServiceDiscountCodeList;
	private boolean createServiceDiscountCode;
	private boolean endServiceDiscountCode;
	private boolean viewServiceDiscountCodeDetail;
	private boolean viewProductDiscountCodeList;
	private boolean editServiceDiscountCode;
}