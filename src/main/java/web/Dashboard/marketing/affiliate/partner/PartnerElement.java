package web.Dashboard.marketing.affiliate.partner;

import org.openqa.selenium.By;

public class PartnerElement {
    By loc_lstName = By.cssSelector(".name");
    By loc_btnAddPartner = By.cssSelector("a .gs-button__green");
    By loc_btnExport = By.cssSelector(".gs-content-header-right-el div> .gs-button__green");
    By loc_lst_btnExportOption = By.cssSelector(".uik-menuDrop__list button");
    By loc_tabAffiliateActive = By.cssSelector(".affiliate-tab__tab__button__active");
    By loc_tab_dropshipReseller = By.xpath("//div[contains(@class,'affiliate-tab__tab__button')]");

}
