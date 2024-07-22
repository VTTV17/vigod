package mobile.buyer.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class BuyerSearchElement {
    WebDriver driver;
    public BuyerSearchElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By SEARCH_BAR = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'btn_action_bar_search')]");
    By MENU_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'home_product_filter_bar_group_menu_item')]");
    String MENU_ITEM_XPATH = "//*[@text='%s']";
    By PRODUCT_LIST = By.xpath("//*[ends-with(@resource-id,'adapter_hot_deal_item_name')]");
    By NO_PRODUCT_MESSAGE = By.xpath("//*[ends-with(@resource-id,'fragment_tab_home_products_rl_empty_container')]/android.widget.TextView");
    By MENU_ITEM = By.xpath("//*[ends-with(@resource-id,'item_filter_menu_item_tv_name')]");
}
