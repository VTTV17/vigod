package utilities.model.staffPermission.Promotion;

import lombok.Data;

@Data
public class DiscountCampaign{
	private boolean editProductDiscountCampaign;
	private boolean viewServiceDiscountCampaignDetail;
	private boolean viewProductCampaignList;
	private boolean editServiceDiscountCampaign;
	private boolean viewServiceDiscountCampaignList;
	private boolean createServiceDiscountCampaign;
	private boolean endServiceDiscountCampaign;
	private boolean viewProductDiscountCampaignDetail;
	private boolean createProductDiscountCampaign;
	private boolean endProductDiscountCampaign;
}