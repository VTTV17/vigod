package app.Buyer.checkout;

import org.openqa.selenium.By;

public class CheckoutOneStepElement {
    By loc_delivery_icnArrow = By.xpath("//*[contains(@resource-id, 'ivArrow')]");
    By loc_tvFullAddress = By.xpath("//*[ends-with(@resource-id, 'tvFullAddress')]");
    By loc_btnCheckout = By.xpath("//*[ends-with(@resource-id, 'tvCheckout')]");

}
