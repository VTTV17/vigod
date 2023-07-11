package pages.buyerapp.home;

import org.openqa.selenium.By;

public class BuyerHomeElement {
    By HEADER_SEARCH_ICON = By.xpath("//*[@resource-id = 'btn-open-header-menu-mobile']/following-sibling::*[2]");
    By HEADER_STORE_LOGO = By.xpath("//*[@resource-id = 'btn-open-header-menu-mobile']/following-sibling::*[1]");
    By HEADER_SEARCH_BOX = By.xpath("//*[contains(@resource-id,'search_src_text')]");
    By SEARCH_RESULT = By.xpath("//*[contains(@resource-id, 'adapter_hot_deal_item_root')]");
    By MENU_ICON = By.xpath("//*[@resource-id='btn-open-header-menu-mobile']");
    String MENU_ITEM_XPATH = "//*[@text='%s']";

}
