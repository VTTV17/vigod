package utilities.model.staffPermission.GoChat;

import lombok.Data;

@Data
public class SMSCampaign{
	private boolean viewAllSMSCampaignList;
	private boolean viewSMSDetail;
	private boolean createSMSCampaign;
	private boolean updateSMSCampaign;
	private boolean registerSMSAccount;
	private boolean registerSMSBrandName;
	private boolean registerSMSTemplate;
}