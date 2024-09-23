package web.Dashboard.marketing.affiliate.commission;

import org.openqa.selenium.By;

public class CreateCommissionElement {
    By loc_txtCommissionName = By.id("commissionName");
    By loc_txtCommissionPercent = By.cssSelector(".form-commission-type input:not(#percent)");
    By loc_lst_txtOrderLevelValue = By.cssSelector(".item-commission-applicable-level input:not([id*='commissionApplicableLevels'])");
    By loc_btnSave = By.xpath("//button[string()='Save' or string()='Lưu']");
    By loc_lst_ddlOrderLevelOperators = By.xpath("//select[contains(@id,'commissionApplicableLevels')]");
    By loc_btnAddCondition = By.cssSelector(".container-btn-add");
    By loc_lblSpecificProducts = By.cssSelector("[for='radio-type-SPECIFIC_PRODUCTS']");
    By loc_lnkAddProduct = By.cssSelector("[for='radio-type-SPECIFIC_PRODUCTS'] .gs-fake-link");
    public By loc_lst_lblCollectionName = By.cssSelector(".product-name");
    public By loc_lst_lblProductName = By.cssSelector(".product-name");
    public By loc_txtSearch = By.cssSelector(".search-input");
    public By loc_lblSpecificCollection = By.cssSelector("[for='radio-type-SPECIFIC_COLLECTIONS']");
    public By loc_lnkAddCollection = By.cssSelector("[for='radio-type-SPECIFIC_COLLECTIONS'] .gs-fake-link");
}
