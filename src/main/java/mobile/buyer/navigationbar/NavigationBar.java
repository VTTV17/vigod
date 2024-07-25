package mobile.buyer.navigationbar;

import mobile.buyer.home.HomeScreen;
import mobile.buyer.home.account.BuyerAccountPage;
import mobile.buyer.search.BuyerSearchPage;
import mobile.buyer.shopcart.BuyerShopCartPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAndroid;

import java.time.Duration;

public class NavigationBar extends UICommonAndroid {
    By HOME_ICON = By.xpath("//*[ends-with(@resource-id,'bottom_navigation_tab_home')]");
    By SEARCH_ICON = By.xpath("//*[ends-with(@resource-id,'bottom_navigation_tab_product')]");
    By SHOP_CART_ICON = By.xpath("//*[ends-with(@resource-id,'bottom_navigation_tab_cart')]");
    By NOTI_ICON = By.xpath("//*[ends-with(@resource-id,'bottom_navigation_tab_notification')]");
    By PROFILE_ICON = By.xpath("//*[ends-with(@resource-id,'bottom_navigation_tab_me')]");
    By NAVIGATE_BAR = By.xpath("//android.widget.FrameLayout[contains(@resource-id,'activity_main_bottom_navigation')]");
    final static Logger logger = LogManager.getLogger(NavigationBar.class);

    WebDriver driver;
    WebDriverWait wait;

    public NavigationBar(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public BuyerSearchPage tapOnSearchIcon() {
        click(SEARCH_ICON);
        logger.info("Tap on search icon.");
        return new BuyerSearchPage(driver);
    }

    public HomeScreen tapOnHomeIcon() {
        click(HOME_ICON);
        logger.info("Tap on home icon.");
        return new HomeScreen(driver);
    }

    public BuyerShopCartPage tapOnCartIcon() {
        click(SHOP_CART_ICON);
        logger.info("Tap on shop cart icon.");
        return new BuyerShopCartPage(driver);
    }

    public BuyerAccountPage tapOnAccountIcon() {
        click(PROFILE_ICON);
        logger.info("Tap on account icon.");
        return new BuyerAccountPage(driver);
    }

}
