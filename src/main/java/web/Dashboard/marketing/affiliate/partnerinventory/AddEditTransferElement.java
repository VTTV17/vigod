package web.Dashboard.marketing.affiliate.partnerinventory;

import org.openqa.selenium.By;

public class AddEditTransferElement {
    By loc_txtSearchProduct = By.cssSelector(".product .search-box input");
    By loc_lst_searchProductSuggestion = By.cssSelector(".search-result .product-item .search-item span:nth-child(1)");
    By loc_txtSearchPartner = By.cssSelector("[data-testid='input-partner-search']");
    By loc_lst_searchPartnerSuggestion = By.cssSelector(".float-right .search-item");
    By loc_ddlOrigin = By.xpath("//div[@class='float-right']//div[@class='uik-select__wrapper']");
    By loc_ddlOrigin_options = By.xpath("//div[@class='float-right']//div[@class='uik-select__label']");
    By loc_btnSave = By.cssSelector("[data-testid='submitAffiliateTransferFormEditor']");
}
