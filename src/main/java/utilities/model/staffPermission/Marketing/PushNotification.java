package utilities.model.staffPermission.Marketing;

import lombok.Data;

@Data
public class PushNotification{
	private boolean deleteCampaign;
	private boolean editCampaign;
	private boolean viewCampaignList;
	private boolean createCampaign;
	private boolean viewCampaignDetail;
}