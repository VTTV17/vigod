package pages.buyerapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.buyerapp.account.BuyerAccountPage;
import pages.buyerapp.home.BuyerHomePage;
import pages.buyerapp.search.BuyerSearchPage;
import pages.buyerapp.shopcart.BuyerShopCartPage;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import utilities.UICommonMobile;

import java.time.Duration;

public class NavigationBar extends UICommonMobile {
    By el_home_icon = By.xpath("//android.widget.FrameLayout[@content-desc='Trang chủ' or @content-desc='Home']/android.widget.ImageView");
    By el_search_icon = By.xpath("//android.widget.FrameLayout[@content-desc='Tìm kiếm' or @content-desc='Search']/android.widget.ImageView");
    By el_shop_cart_icon = By.xpath("//android.widget.FrameLayout[@content-desc='Giỏ hàng' or @content-desc='Cart']/android.widget.ImageView");
    By el_noti_icon = By.xpath("//android.widget.FrameLayout[@content-desc='Thông báo'@content-desc='Notification']/android.widget.ImageView");
    By el_profile_icon = By.xpath("//android.widget.FrameLayout[@content-desc='Tài khoản' or @content-desc='Account']/android.widget.ImageView");

    final static Logger logger = LogManager.getLogger(NavigationBar.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;


    public NavigationBar(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    public BuyerSearchPage tapOnSearchIcon(){
        clickElement(el_search_icon);
        logger.info("Tap on search icon.");
        return new BuyerSearchPage(driver);
    }
    public BuyerHomePage tapOnHomeIcon(){
        clickElement(el_home_icon);
        logger.info("Tap on home icon.");
        return new BuyerHomePage(driver);
    }
    public BuyerShopCartPage tapOnCartIcon(){
        clickElement(el_shop_cart_icon);
        logger.info("Tap on shop cart icon.");
        return new BuyerShopCartPage(driver);
    }
    public BuyerAccountPage tapOnAccountIcon(){
        clickElement(el_profile_icon);
        logger.info("Tap on account icon.");
        return new BuyerAccountPage(driver);
    }

}
