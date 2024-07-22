package mobile.buyer.home;

import org.openqa.selenium.By;

import static utilities.environment.goBUYEREnvironment.goBUYERBundleId;

public class HomeElement {
    By loc_icnHeaderSearch = By.xpath("(//*[@resource-id=\"%s:id/activity_main_pager\"]//android.widget.Image)[2]".formatted(goBUYERBundleId));
    By loc_imgHeaderLogo = By.xpath("//*[@resource-id = 'btn-open-header-menu-mobile']/following-sibling::*[1]");
    By loc_txtHeaderSearchBox = By.xpath("//*[contains(@resource-id,'search_src_text')]");
    By loc_lstSearchResult(String keyword) {
        return By.xpath("//android.widget.TextView[@resource-id=\"%s:id/adapter_hot_deal_item_name\" and @text=\"%s\"]".formatted(goBUYERBundleId, keyword));
    }
    By loc_icnMenu = By.xpath("//*[@resource-id='btn-open-header-menu-mobile']");
    String MENU_ITEM_XPATH = "//*[@text='%s']";
    By loc_lstSearchResult = By.xpath("//*[contains(@resource-id, 'adapter_hot_deal_item_name')]");
    By loc_btnViewMore = By.xpath("//*[contains(@resource-id, 'tvDisplayMore')]");
    By loc_lstMenuItems = By.xpath("//*[ends-with(@resource-id,'mobile_nav_menu')]//android.widget.TextView");
}
