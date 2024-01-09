package utilities.model.staffPermission.Marketing;

import lombok.Data;

@Data
public class Marketing{
	private PushNotification pushNotification;
	private LoyaltyProgram loyaltyProgram;
	private BuyLink buyLink;
	private EmailCampaign emailCampaign;
	private LandingPage landingPage;
	private LoyaltyPoint loyaltyPoint;
}