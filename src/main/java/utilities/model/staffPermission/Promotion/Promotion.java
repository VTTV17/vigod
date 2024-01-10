package utilities.model.staffPermission.Promotion;

import lombok.Data;

@Data
public class Promotion {
    private DiscountCampaign discountCampaign = new DiscountCampaign();
    private FlashSale flashSale = new FlashSale();
    private BxGy bxGy = new BxGy();
    private DiscountCode discountCode = new DiscountCode();
}