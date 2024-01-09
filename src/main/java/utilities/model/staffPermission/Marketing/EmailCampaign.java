package utilities.model.staffPermission.Marketing;

import lombok.Data;

@Data
public class EmailCampaign{
	private boolean deleteCampaign;
	private boolean editCampaign;
	private boolean viewCampaignList;
	private boolean createCampaign;
}