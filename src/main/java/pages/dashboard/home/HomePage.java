package pages.dashboard.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.excel.Excel;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utilities.links.Links.DOMAIN;

public class HomePage {
    WebDriver driver;
    UICommonAction commons;
    WebDriverWait wait;

    String pageLoadedTextENG = "We have created a short list of things you should do to complete your store";
    String pageLoadedTextVIE = "Chúng tôi có chuẩn bị danh sách bạn cần làm để hoàn tất cửa hàng của bạn";

    SoftAssert soft = new SoftAssert();

    Excel excel;
    int countFailed = 0;
    AssertCustomize assertCustomize;
    String planPermissionFileName = "PlanPermission.xlsx";

    final static Logger logger = LogManager.getLogger(HomePage.class);

    public HomePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".header-right__ele-right a[href='/logout']")
    WebElement LOGOUT_BTN;

    @FindBy(css = ".loading .lds-dual-ring-grey")
    WebElement SPINNER;

    @FindBy(css = "a[name $=settings]")
    WebElement SETTINGS_MENU;

    @FindBy(css = "a[name='component.navigation.products'] > span > span")
    WebElement PRODUCTS_MENU;

    @FindBy(css = ".modal-content .gs-button")
    WebElement UPGRADNOW_BTN;

    @FindBy(css = ".modal-content")
    WebElement UPGRADNOW_MESSAGE;

    @FindBy(css = "div.language-selector > button")
    WebElement LANGUAGE;

    @FindBy(css = "button.uik-select__option")
    List<WebElement> LANGUAGE_LIST;
    @FindBy(css = ".gs-sale-pitch_content")
    WebElement SALE_PITCH_POPUP;

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
        commons.sleepInMiliSecond(1000);
        String pageNavigate = pageMap().get(pageName);
        String newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
        if (pageName.equals("Shopee Products")) {
            newXpath = "(" + MENU_ITEM.replace("%pageNavigate%", pageNavigate) + ")[2]";
        }
        logger.debug("xpath: %s".formatted(newXpath));
        Boolean flag;
        try {
        	flag = !driver.findElement(By.xpath(newXpath)).getAttribute("active").contentEquals("active");
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught while getting attribute of the element \n" + ex);
            flag = !driver.findElement(By.xpath(newXpath)).getAttribute("active").contentEquals("active");
        }
        logger.debug("xpath: %s".formatted(flag));
        if (flag) {
            try {
                commons.clickElement(driver.findElement(By.xpath(newXpath)));
            } catch (StaleElementReferenceException ex) {
                logger.debug("StaleElementReferenceException caught in navigateToPage");
                commons.clickElement(driver.findElement(By.xpath(newXpath)));
            }
            logger.info("Click on %s item on menu".formatted(pageName));
            commons.sleepInMiliSecond(1000);
            commons.waitForElementInvisible(SPINNER);
        }
    }

    public void navigateToPage(String pageName, String... subMenus) {
        navigateToPage(pageName);
        for (String subMenu : subMenus) {
            navigateToPage(subMenu);
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

    public void navigateToAllProductsPage() {
        wait.until(ExpectedConditions.visibilityOf(PRODUCTS_MENU));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", PRODUCTS_MENU);
        logger.info("Click on the Products menu");
    }

    public void navigateToSettingsPage() {
        wait.until(ExpectedConditions.visibilityOf(SETTINGS_MENU));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SETTINGS_MENU);
        logger.info("Click on the Settings menu");
    }

    public String getDashboardLanguage() {
        return commons.getText(LANGUAGE).replace(" ", "");
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
		String message = commons.getText(TOAST_MESSAGE);
		commons.clickElement(TOAST_MESSAGE_CLOSE_BTN);
		return message;
	}

    public void completeVerify() {
        soft.assertAll();
    }
    public Integer verifySalePitchPopupDisplay() throws IOException {
//        commons.sleepInMiliSecond(1500);
        AssertCustomize assertCustomize = new AssertCustomize(driver);
        countFailed = assertCustomize.assertTrue(countFailed, commons.isElementDisplay(SALE_PITCH_POPUP), "Check Sale pitch video show");
        return countFailed;
    }
    public Integer verifySalePitchPopupNotDisplay() throws IOException {
//        commons.sleepInMiliSecond(1000);
        AssertCustomize assertCustomize = new AssertCustomize(driver);
        countFailed = assertCustomize.assertFalse(countFailed, commons.isElementDisplay(SALE_PITCH_POPUP), "Check Sale pitch video not show");
        return countFailed;
    }
    public boolean isMenuClicked(WebElement element) {
        commons.sleepInMiliSecond(1000);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            wait.until(ExpectedConditions.visibilityOf(element)).click();
            logger.debug("Element is clickable");
            return true;
        } catch (Exception e) {
            logger.debug("Element is not clickable");
            logger.debug(e.getMessage());
            return false;
        }
    }

    public HomePage checkPageHasPermission(String pageName, String path) throws IOException {
        commons.sleepInMiliSecond(1000);
        String pageNavigate = pageMap().get(pageName);
        String newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
        if (pageName.equals("Shopee Products")) {
            newXpath = "(" + MENU_ITEM.replace("%pageNavigate%", pageNavigate) + ")[2]";
        }
        boolean isClicked;
        try {
            WebElement element = commons.getElementByXpath(newXpath);
            isClicked = isMenuClicked(element);
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in checkPageHasPermission");
            WebElement element = commons.getElementByXpath(newXpath);
            isClicked = isMenuClicked(element);
        }
        countFailed = assertCustomize.assertTrue(countFailed, isClicked, "Check Menu clickable");
        verifySalePitchPopupNotDisplay();
        if (path.contains("intro") || path.contains("info")) {
            commons.waitForElementInvisible(SPINNER);
            commons.sleepInMiliSecond(1000);
        }
        if (pageName.equals("POS") || pageName.equals("Affiliate")) {
            commons.switchToWindow(1);
            commons.sleepInMiliSecond(1000);
            countFailed = assertCustomize.assertEquals(countFailed, commons.getCurrentURL(), DOMAIN + path, "Check URL of Page: " + pageName);
            commons.closeTab();
            commons.switchToWindow(0);
        } else {
            countFailed = assertCustomize.assertEquals(countFailed, commons.getCurrentURL(), DOMAIN + path, "Check URL of page: " + pageName);
            if (commons.getCurrentURL().contains("404")){
                commons.navigateBack();
                logger.debug("Page show 404");
            }
        }
        logger.info("Check page has permission");
        return this;
    }

    public HomePage checkPageNoPermission(String pageName, String path, String hasSalePitch) throws IOException {
        if (hasSalePitch.equalsIgnoreCase("Y")) {
            navigateToPage(pageName);
            verifySalePitchPopupDisplay();
        } else {
            String pageNavigate = pageMap().get(pageName);
            String newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
            if (pageName.equals("Shopee Products")) {
                newXpath = "(" + MENU_ITEM.replace("%pageNavigate%", pageNavigate) + ")[2]";
            }
            commons.sleepInMiliSecond(1000);
            countFailed = assertCustomize.assertFalse(countFailed, isMenuClicked(commons.getElementByXpath(newXpath)), "Check Menu not clickable: " + pageName);
            commons.openNewTab();
            commons.switchToWindow(1);
            commons.navigateToURL(DOMAIN + path);
            countFailed = assertCustomize.assertTrue(countFailed, commons.getCurrentURL().contains("/404"),  "Check url 404: " + pageName);
            commons.closeTab();
            commons.switchToWindow(0);
        }
        logger.info("Check page no permission");
        return this;
    }

    public HomePage checkPermissionFromExcel(String permission, String pageName, String path, String hasSalePitch) throws IOException {
        switch (permission) {
            case "A":
                checkPageHasPermission(pageName, path);
                break;
            case "D":
                checkPageNoPermission(pageName, path, hasSalePitch);
                break;
            default:
                break;
        }
        logger.info("Read and check permission from excel");
        return this;
    }
    /**
     *
     * @param packageType Input value: GoWeb, GoApp, GoPos, GoSocial, GoLead (ignore case)
     * @throws IOException
     */
    public HomePage checkPermissionAllPageByPackage(String packageType) throws IOException {
        excel = new Excel();
        Sheet planPermissionSheet = excel.getSheet(planPermissionFileName, 0);
        int rowNumber = planPermissionSheet.getLastRowNum();
        String permissionParentMenu = "";
        for (int i = 1; i <= rowNumber; i++) {
            int packageColIndex = excel.getCellIndexByCellValue(planPermissionSheet.getRow(0), packageType);
            logger.debug("packageColIndex: " + packageColIndex);
            String permissionFromExcel = planPermissionSheet.getRow(i).getCell(packageColIndex).getStringCellValue();
            String menuItemExcel = planPermissionSheet.getRow(i).getCell(0).getStringCellValue();
            logger.debug("PagesExcel: " + menuItemExcel);
            String pathExcel = planPermissionSheet.getRow(i).getCell(1).getStringCellValue();
            String hasSalePitch = planPermissionSheet.getRow(i).getCell(2).getStringCellValue();
            String[] pageNames = menuItemExcel.split("-");
            if (pageNames.length == 1) {
                checkPermissionFromExcel(permissionFromExcel, pageNames[0], pathExcel, hasSalePitch);
                permissionParentMenu = permissionFromExcel;
            } else {
                if (permissionParentMenu.equalsIgnoreCase("D") && hasSalePitch.equalsIgnoreCase("N")) {
                    logger.debug("Parent menu is disable");
                    continue;
                }
                for (int j = 0; j < pageNames.length; j++) {
                    logger.debug(pageNames[j]);
                    if (j != pageNames.length - 1) {
                        navigateToPage(pageNames[j]);
                    } else {
                        checkPermissionFromExcel(permissionFromExcel, pageNames[j], pathExcel, hasSalePitch);
                    }
                }
            }
        }
        return this;
    }

    public HomePage completeVerifyPermissionByPackage() {
        logger.info("countFail = %s".formatted(countFailed));
        if (countFailed > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(countFailed));
        }
        countFailed = 0;
        return this;
    }

    public HomePage verifyPageLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains(pageLoadedTextVIE) || driver.getPageSource().contains(pageLoadedTextENG);
        });
        return this;
    }
}
