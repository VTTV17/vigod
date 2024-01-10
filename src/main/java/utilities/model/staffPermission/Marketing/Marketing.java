package utilities.model.staffPermission.Marketing;

import lombok.Data;

@Data
public class Marketing {
    private PushNotification pushNotification = new PushNotification();
    private LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
    private BuyLink buyLink = new BuyLink();
    private EmailCampaign emailCampaign = new EmailCampaign();
    private LandingPage landingPage = new LandingPage();
    private LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
}