package pages.dashboard.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

import java.time.Duration;
import java.util.*;

public class HomePage {
    WebDriver driver;
    UICommonAction commons;
    WebDriverWait wait;
    
    SoftAssert soft = new SoftAssert(); 
    
    final static Logger logger = LogManager.getLogger(HomePage.class);

    public HomePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//a[@name='component.navigation.services']")
    WebElement SERVICES_LINK;

    @FindBy(css = ".header-right__ele-right a[href='/logout']")
    WebElement LOGOUT_BTN;

    @FindBy(css = ".loading .lds-dual-ring-grey")
    WebElement SPINNER;

    @FindBy(css = "a[name $=settings]")
    WebElement SETTINGS_MENU;

    @FindBy(css = ".modal-content .gs-button")
    WebElement UPGRADNOW_BTN;
    
    @FindBy(css = ".modal-content")
    WebElement UPGRADNOW_MESSAGE;

    @FindBy(css = "div.language-selector > button")
    WebElement LANGUAGE;

    @FindBy(css = "button.uik-select__option")
    List<WebElement> LANGUAGE_LIST;

	@FindBy(css = ".Toastify__toast-body")
	WebElement TOAST_MESSAGE;

	@FindBy(css = ".Toastify__close-button")
	WebElement TOAST_MESSAGE_CLOSE_BTN;    
    
    String MENU_ITEM = "//a[@name='%pageNavigate%']";

    public Map<String, String> pageMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Home", "component.navigation.home");
        map.put("GoChat", "component.navigation.liveChat");
        map.put("Facebook", "component.navigation.liveChat.facebook");
        map.put("Conversation", "component.navigation.liveChat.conversation");
        map.put("Configuration", "component.navigation.liveChat.configuration");
        map.put("Automation", "component.navigation.liveChat.automation");
        map.put("Broadcast", "component.navigation.liveChat.broadcast");
        map.put("Zalo", "component.navigation.liveChat.zalo");
        map.put("Products", "component.navigation.products");
        map.put("All Products", "component.navigation.all_products");
        map.put("Inventory", "component.navigation.product.inventory");
        map.put("Transfer", "component.navigation.product.transfer");
        map.put("Product Collections", "component.navigation.collections_products");
        map.put("Product Reviews", "component.navigation.reviews");
        map.put("Supplier", "component.navigation.supplier");
        map.put("Purchase Orders", "component.navigation.product.purchaseOrder");
        map.put("Services", "component.navigation.services");
        map.put("All Services", "component.navigation.all_services");
        map.put("Service Collections", "component.navigation.collections_services");
        map.put("Orders", "component.navigation.orders");
        map.put("Order List", "component.navigation.orderList");
        map.put("Return Orders", "component.navigation.returnOrder");
        map.put("Create Quotation", "page.order.list.createQuotation");
        map.put("POS", "page.order.list.create");
        map.put("Reservations", "page.reservation.menu.name");
        map.put("Promotion", "component.navigation.promotion");
        map.put("Discount", "component.navigation.promotion.discount");
        map.put("Flash Sale", "component.navigation.promotion.flashsale");
        map.put("Customers", "component.navigation.customers");
        map.put("All Customers", "component.navigation.customers.allCustomers");
        map.put("Segments", "component.navigation.customers.segments");
        map.put("Call Center", "component.navigation.callCenter");
        map.put("Call History", "component.navigation.callCenter.history");
        map.put("Cashbook", "page.cashbook.menu.name");
        map.put("Analytics", "component.navigation.analytics");
        map.put("Order Analytic", "component.navigation.analytics.orders");
        map.put("Reservations Analytic", "component.navigation.analytics.reservations");
        map.put("Marketing", "component.navigation.marketing");
        map.put("Landing page", "component.navigation.landing");
        map.put("Buy Link", "component.navigation.buylink");
        map.put("Email Campaign", "component.navigation.emailMarketing");
        map.put("Push Notification", "component.navigation.notification");
        map.put("Loyalty Program", "component.navigation.loyalty");
        map.put("Google Analytics", "component.navigation.googleAnalytics");
        map.put("Google Shopping", "component.navigation.googleShopping");
        map.put("Google Tag Manager", "component.navigation.googleTagManager");
        map.put("Facebook Pixel", "component.navigation.facebookPixel");
        map.put("Loyalty Point", "component.navigation.loyaltyPoint");
        map.put("Affiliate", "component.navigation.affiliate");
        map.put("Online Shop", "component.navigation.storefront");
        map.put("Themes", "page.themeEngine.management.title");
        map.put("Blog", "page.storeFront.blog.nav");
        map.put("Pages", "component.navigation.pages");
        map.put("Menus", "component.navigation.menus");
        map.put("Domains", "component.navigation.domains");
        map.put("Preferences", "component.navigation.preferences");
        map.put("Shopee", "component.button.selector.saleChannel.shopee");
        map.put("Account Information", "component.navigation.account");
        map.put("Shopee Products", "component.navigation.products");
        map.put("Link Products", "component.navigation.linkProducts");
        map.put("Account Management", "component.navigation.account.management");
        map.put("Shopee Settings", "component.navigation.account.settings");
        map.put("Lazada", "component.button.selector.saleChannel.lazada");
        map.put("GoMua", "component.button.selector.saleChannel.beecow");
        map.put("Settings", "component.navigation.settings");
        return map;
    }

    public void navigateToPage(String pageName) {
        commons.waitTillElementDisappear(SPINNER, 15);
        String pageNavigate = pageMap().get(pageName);
        String newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
        commons.clickElement(driver.findElement(By.xpath(newXpath)));
        logger.info("Click on %s item on menu".formatted(pageName));
    }

    public void navigateToPage(String pageName, String... subMenus) {
        commons.waitTillElementDisappear(SPINNER, 15);
        String pageNavigate = pageMap().get(pageName);
        String newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
        commons.clickElement(driver.findElement(By.xpath(newXpath)));
        logger.info("Click on %s item on menu".formatted(pageName));
        for (String subMenu : subMenus) {
            pageNavigate = pageMap().get(subMenu);
            newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
            commons.clickElement(driver.findElement(By.xpath(newXpath)));
            logger.info("Click on %s item on menu".formatted(pageName));
        }
    }

    public HomePage waitTillSpinnerDisappear() {
        commons.waitTillElementDisappear(SPINNER, 15);
        logger.info("Spinner has finished loading");
        return this;
    }

    public void clickLogout() {
        commons.clickElement(LOGOUT_BTN);
        logger.info("Clicked on Logout linktext");
    }

    public void navigateToSettingsPage() {
        wait.until(ExpectedConditions.visibilityOf(SETTINGS_MENU));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SETTINGS_MENU);
    }

    public String getDashboardLanguage() {
        return wait.until(ExpectedConditions.visibilityOf(LANGUAGE)).getText().replace(" ", "");
    }

    public void clickUpgradeNow() {
        commons.clickElement(UPGRADNOW_BTN);
        logger.info("Clicked on Upgrade Now button");
    }

    public HomePage verifyUpgradeNowMessage(String message) {
    	String text = commons.getText(UPGRADNOW_MESSAGE);
    	soft.assertEquals(text,message, "[Homepage][Upgrade Now Message] Message does not match.");
    	logger.info("verifyUpgradeNowMessage completed");
    	return this;
    }    
    
    public HomePage selectLanguage(String language) {
        wait.until(ExpectedConditions.visibilityOf(LANGUAGE));
        if (!getDashboardLanguage().equals(language)) {
            LANGUAGE.click();
            for (WebElement element : LANGUAGE_LIST) {
                if (element.getText().equals(language)) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    break;
                }
            }
        }
        return this;
    }

	public String getToastMessage() {
		logger.info("Finished getting toast message.");
		return commons.getText(TOAST_MESSAGE);
	}    
    
    public void completeVerify() {
        soft.assertAll();
    }
}
