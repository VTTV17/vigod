package pages.sellerapp.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.buyerapp.account.BuyerAccountPage;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class HomePage {

	final static Logger logger = LogManager.getLogger(HomePage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    public HomePage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    By ACCOUNT_TAB = By.xpath("//*[contains(@resource-id, 'bottom_navigation_tab_account')]");
    By LOGOUT_BTN = By.xpath("//*[contains(@resource-id, 'llLogout')]");
    By LOGOUT_OK_BTN = By.xpath("//*[contains(@resource-id, 'tvRightButton')]");
    By LOGOUT_ABORT_BTN = By.xpath("//*[contains(@resource-id, 'tvLeftButton')]");


    public boolean isAccountTabDisplayed() {
        commonAction.waitPageLoaded(ACCOUNT_TAB);
    	boolean isDisplayed = commonAction.getElement(ACCOUNT_TAB, 5).isDisplayed();
    	logger.info("Is Account Tab displayed: " + isDisplayed);
    	return isDisplayed;
    }
    
    public HomePage clickAccountTab() {
        commonAction.waitPageLoaded(ACCOUNT_TAB);
        commonAction.getElement(ACCOUNT_TAB, 5).click();
    	logger.info("Click on Account tab");
    	return this;
    }
    
    public HomePage clickLogoutBtn() {
    	commonAction.getElement(LOGOUT_BTN, 5).click();
    	logger.info("Click on Logout button");
    	return this;
    }
    
    public HomePage clickLogoutOKBtn() {
    	commonAction.getElement(LOGOUT_OK_BTN, 5).click();
    	logger.info("Click on OK button to confirm logout");
    	return this;
    }
    
    public HomePage clickLogoutAbortBtn() {
    	commonAction.getElement(LOGOUT_ABORT_BTN, 5).click();
    	logger.info("Click on Cancel button to abort logout");
    	return this;
    }
    public Map<String, String> pageMap() {
        Map<String, String> map = new HashMap<>();
        map.put("CreateNewOrder", "seller.home.createNewOrder");
        map.put("CreateReservation", "seller.home.createReservation");
        map.put("AddProduct", "seller.home.addProduct");
        map.put("AddNewCustomer", "seller.home.addNewCustomer");
        map.put("ScanProduct", "seller.home.scanProduct");
        map.put("LiveStream", "seller.home.livestream");
        map.put("Facebook", "seller.home.facebook");
        map.put("ZaloOA", "seller.home.zaloOA");
        map.put("Printers", "seller.home.printers");
        map.put("Orders", "seller.home.orders");
        map.put("Product", "seller.home.products");
        map.put("Customer", "seller.home.customers");
        map.put("Reservations", "seller.home.reservations");
        map.put("Inventory", "seller.home.inventory");
        map.put("Supplier", "seller.home.supplier");
        map.put("PurchaseOrders", "seller.home.purchaseOrders");
        map.put("Cashbook", "seller.home.cashbooks");
        map.put("Affiliate", "seller.home.affiliate");
        return map;
    }

    /**
     List pageName: CreateNewOrder, CreateReservation, AddProduct, AddNewCustomer, ScanProduct, LiveStream, Facebook, ZaloOA, Printers, Orders, Product, Customer, Reservations, Inventory, Supplier, PurchaseOrders, Cashbook, Affiliate
     */
    public void navigateToPage(String pageName){
        String propertyName = pageMap().get(pageName);
        String pageNavigate;
        try {
            pageNavigate = PropertiesUtil.getPropertiesValueByDBLang(propertyName);
        } catch (Exception e) {
            logger.info("Page name not found!");
            throw new RuntimeException(e);
        }
        commonAction.moveAndGetElementByText(pageNavigate).click();
    }
    public BuyerAccountPage scrollDown(){
        commonAction.swipeByCoordinatesInPercent(0.75,0.75,0.75,0.25);
        logger.info("Scroll down");
        return new BuyerAccountPage(driver);
    }
    public void LogOut(){
        clickAccountTab();
        clickLogoutBtn();
        commonAction.sleepInMiliSecond(1000);
        clickLogoutOKBtn();
        commonAction.sleepInMiliSecond(1000);
    }
}
