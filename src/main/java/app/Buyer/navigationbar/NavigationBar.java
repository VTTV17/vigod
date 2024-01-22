package app.Buyer.navigationbar;

import app.Buyer.shopcart.BuyerShopCartPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.home.BuyerHomePage;
import app.Buyer.search.BuyerSearchPage;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class NavigationBar extends UICommonMobile {
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
    public BuyerSearchPage tapOnSearchIcon(){
        clickElement(SEARCH_ICON);
        logger.info("Tap on search icon.");
        return new BuyerSearchPage(driver);
    }
    public BuyerHomePage tapOnHomeIcon(){
        new UICommonMobile(driver).waitPageLoaded(HOME_ICON);
        clickElement(HOME_ICON);
        logger.info("Tap on home icon.");
        return new BuyerHomePage(driver);
    }
    public BuyerShopCartPage tapOnCartIcon(){
        clickElement(SHOP_CART_ICON);
        logger.info("Tap on shop cart icon.");
//        new BuyerGeneral(driver).waitLoadingDisappear();
        return new BuyerShopCartPage(driver);
    }
    public BuyerAccountPage tapOnAccountIcon(){
        clickElement(PROFILE_ICON);
        logger.info("Tap on account icon.");
        return new BuyerAccountPage(driver);
    }

}
