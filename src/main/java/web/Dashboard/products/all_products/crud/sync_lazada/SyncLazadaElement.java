package web.Dashboard.products.all_products.crud.sync_lazada;

import org.openqa.selenium.By;

public class SyncLazadaElement {
    By loc_ddlCategory = By.xpath("//label[@for='category']//following-sibling::div//input");
    By loc_categorySubmenu (int index){
        return By.xpath("(//ul[@class='ant-cascader-menu'])[%s]//div".formatted(index));
    }
    By loc_txtBrand = By.cssSelector("[aria-controls='productAttributeForm_brand_list']");
    By loc_lstBrand = By.cssSelector("#productAttributeForm_brand_list~div .ant-select-item-option-content");
    By loc_lst_txtSKU = By.cssSelector("[name='sellerSku']");
    By loc_ckbSelectAllVariation = By.cssSelector("th .ant-checkbox");
    By loc_btnCreate = By.xpath("//button[string()='Tạo' or string() = 'Create']");
    By loc_selectedAccount = By.cssSelector("[class='ant-select-selection-item']");
    By loc_lst_icnDimension  = By.cssSelector(".dimension-model-icon");
    By loc_txtLengthWidthHeightWeight = By.cssSelector("div .ant-input-number-input");
    By loc_icnCloseDimension = By.xpath("//button[@aria-label='Close']");
}
