package web.Dashboard.orders.orderlist.edit_order;

import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

public class EditOrderElement {
    By loc_btnSave = By.cssSelector(".order-edit-form-header .color--primary");
    By loc_btnPromotion = By.xpath("(//*[contains(@class,'title--discount')])[1]");
    By loc_dlgDiscount = By.cssSelector(".order-instore-purchase-discount-modal");

    enum DiscountType {
        discountCode, discountAmount, discountPercent;

        static List<DiscountType> getAllDiscountType() {
            return Arrays.asList(DiscountType.values());
        }
    }

    By loc_dlgDiscount_tabDiscountType = By.cssSelector(".box-promotion-title");
    By loc_dlgDiscount_txtDiscountValue = By.xpath("(//*[contains(@class, 'order-instore-purchase-discount-modal')]//input)[1]");
    By loc_dlgDiscount_btnApply = By.cssSelector(".order-instore-purchase-discount-modal .color--gradient-blue");
    By loc_btnCost = By.xpath("(//*[contains(@class,'title--discount')])[2]");
    By loc_dlgCost = By.cssSelector(".modal-cost");
    By loc_dlgCost_imgDeleteCost = By.cssSelector(".delete-code img");
    By loc_dlgCost_imgAddCost = By.cssSelector(".add-code .img");
    By loc_dlgCost_txtCostName = By.cssSelector("[name *= 'input-search']");
    By loc_dlgCost_txtCostValue = By.cssSelector("[name *= 'price']");
    By loc_dlgCost_btnSave = By.cssSelector(".modal-cost .gs-button__green");

}
