package utilities.model.staffPermission.GoChat;

import lombok.Data;

@Data
public class GoChat{
	private Zalo zalo = new Zalo();
	private SMSCampaign sMSCampaign = new SMSCampaign();
	private Facebook facebook = new Facebook();
}