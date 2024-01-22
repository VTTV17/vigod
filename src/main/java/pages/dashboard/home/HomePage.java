package pages.dashboard.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.dashboard.marketing.landingpage.LandingPage;
import pages.dashboard.onlineshop.Domains;
import pages.dashboard.onlineshop.Themes;
import pages.dashboard.products.all_products.ProductPage;
import pages.dashboard.saleschannels.shopee.Shopee;
import pages.dashboard.settings.bankaccountinformation.BankAccountInformation;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static utilities.links.Links.DOMAIN;

public class HomePage extends HomePageElement {
    WebDriver driver;
    UICommonAction commons;
    WebDriverWait wait;
    LandingPage landingPage;

    SoftAssert soft = new SoftAssert();

    Excel excel;
    AssertCustomize assertCustomize;

    final static Logger logger = LogManager.getLogger(HomePage.class);
    HomePageElement homeUI;
    String MENU_ITEM = "//a[@name='%pageNavigate%']";

    public HomePage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        homeUI = new HomePageElement(driver);
        PageFactory.initElements(driver, this);
    }

//    @FindBy(css = ".header-right__ele-right a[href='/logout']")
//    WebElement LOGOUT_BTN;
//
//    @FindBy(css = ".loading .lds-dual-ring-grey")
//    WebElement SPINNER;
//
//    @FindBy(css = ".loading-screen")
//    WebElement LOADING_DOTS;
//
//    @FindBy(css = "a[name $=settings]")
//    WebElement SETTINGS_MENU;
//
//    @FindBy(css = "a[name='component.navigation.products'] > span > span")
//    WebElement PRODUCTS_MENU;
//
//    @FindBy(css = "a[name='component.navigation.promotion'] > span > span")
//    WebElement PROMOTION_MENU;
//
//    @FindBy(css = "a[name='component.navigation.promotion.flashsale'] > span > span")
//    WebElement PROMOTION_FLASH_SALE_MENU;
//
//    @FindBy(css = "a[name='component.navigation.promotion.discount'] > span > span")
//    WebElement PROMOTION_DISCOUNT_MENU;
//
//    @FindBy(css = ".alert-modal .modal-content .gs-button__green")
//    WebElement UPGRADENOW_BTN;
//
//    @FindBy(css = ".modal-success.modal-header img")
//    List<WebElement> CLOSE_UPGRADENOW_BTN;
//
//    @FindBy(css = "button[aria-label='skip-product-tour']")
//    List<WebElement> SKIP_INTRODUCTION_BTN;
//
//    @FindBy(css = ".modal-content")
//    List<WebElement> UPGRADENOW_MESSAGE;
//
//    @FindBy(css = "div.language-selector > button")
//    WebElement LANGUAGE;
//
//    @FindBy(css = "button.uik-select__option")
//    List<WebElement> LANGUAGE_LIST;
//    @FindBy(css = ".gs-sale-pitch_content")
//    WebElement SALE_PITCH_POPUP;
//
//    @FindBy(css = ".Toastify__toast-body")
//    WebElement TOAST_MESSAGE;
//
//    @FindBy(css = ".Toastify__close-button")
//    WebElement TOAST_MESSAGE_CLOSE_BTN;
//
//    @FindBy(id = "fb-root")
//    WebElement FACEBOOK_BUBBLE;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddProduct.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement CREATE_PRODUCT_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddProduct.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[2]")
//    WebElement IMPORT_FROM_SHOPEE_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddProduct.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[3]")
//    WebElement IMPORT_FROM_LAZADA_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeTheme.svg')]")
//    WebElement CUSTOMIZE_APPEARANCE_ICON;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeTheme.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement CHANGE_DESIGN_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeURL.svg')]")
//    WebElement ADD_YOUR_DOMAIN_ICON;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeURL.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement ADD_DOMAIN_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddBank.svg')]")
//    WebElement ADD_BANK_ACCOUNT_ICON;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddBank.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement BANK_INFORMATION_BTN;
//
//    String MENU_ITEM = "//a[@name='%pageNavigate%']";
//
//    By STATISTICS = By.cssSelector(".statistic");
//
//    @FindBy(xpath = ".what-to-do-next")
//    WebElement WHAT_TO_DO_NEXT;

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
        String pageNavigate = pageMap().get(pageName);
        String newXpath = MENU_ITEM.replace("%pageNavigate%", pageNavigate);
        if (pageName.equals("Shopee Products")) {
            newXpath = "(" + MENU_ITEM.replace("%pageNavigate%", pageNavigate) + ")[2]";
        }
        boolean isMenuAlreadyOpened = commons.getAttribute(By.xpath(newXpath), "class").contains("active");

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newXpath)));
        if (isMenuComponentVisiblyDisabled(element)) {
            Assert.assertFalse(isMenuClicked(element), "Element is disabled but still clickable");
        } else {
            if (!isMenuAlreadyOpened) {
                commons.click(By.xpath(newXpath));
                logger.info("Click on %s item on menu".formatted(pageName));
                commons.invisibilityOfElementLocated(loc_imgSpinner);
                if (pageName.equals("Marketing")) {
                    if (new LandingPage(driver).isPermissionModalDisplay()) {
                        new LandingPage(driver).closeModal();
                    }
                }
            }
        }
        logger.info("Navigated to page: " + pageName);
    }

    public void navigateToPage(String... subMenus) {
        if (subMenus.length == 0)
            subMenus[0] = "Home"; // If no input is provided, by default we navigate to Home screen
        for (String subMenu : subMenus) {
            navigateToPage(subMenu);
        }
    }

    public boolean isMenuComponentVisiblyDisabled(WebElement element) {
        if (element.findElement(By.xpath("./parent::*")).getAttribute("class").contains("gs-atm-must-disabled")) {
            return true;
        }
        if (element.findElement(By.xpath("./parent::*/parent::*")).getAttribute("class").contains("gs-atm-must-disabled")) {
            return true;
        }
        return false;
    }

    /**
     * Waits till the spinner <b>appears once then disappears</b>
     * @return HomePage Object
     */
    public HomePage waitTillSpinnerDisappear() {
        commons.waitVisibilityOfElementLocated(loc_imgSpinner);
        commons.waitInvisibilityOfElementLocated(loc_imgSpinner);
        logger.info("Spinner has finished loading");
        return this;
    }

    /**
     * Waits till the spinner disappears. Note that this function does not wait till the spinner appears
     * @return HomePage Object
     */
    public HomePage waitTillSpinnerDisappear1() {
        commons.waitInvisibilityOfElementLocated(loc_imgSpinner);
        logger.info("Spinner1 has finished loading");
        return this;
    }

    public HomePage waitTillLoadingDotsDisappear() {
        commons.waitInvisibilityOfElementLocated(loc_imgLoadingDots);
        logger.info("Loading dots have disappeared");
        return this;
    }

    public void clickLogout() {
        commons.sleepInMiliSecond(1000);
        commons.click(loc_btnLogOut);
        logger.info("Clicked on Logout linktext");
    }

    public void navigateToProducts_AllProductsPage() {
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_mnuProducts)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loc_mnuProducts);
        logger.info("Click on the Products menu");
    }

    public void navigateToPromotion_FlashSalePage() throws InterruptedException {
        // Expand promotion menu
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_mnuPromotion)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", loc_mnuPromotion);

        sleep(1000);

        // Navigate to Flash sale page
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_mnuPromotion_mnuFlashsale)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", loc_mnuPromotion_mnuFlashsale);
    }

    public void navigateToPromotion_DiscountPage() throws InterruptedException {
        // Expand promotion menu
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_mnuPromotion)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", loc_mnuPromotion);

        sleep(1000);

        // Navigate to Flash sale page
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_mnuPromotion_Discount)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", loc_mnuPromotion_Discount);
    }


    public void navigateToSettingsPage() {
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_mnuSettings)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", loc_mnuSettings);
        logger.info("Click on the Settings menu");
    }

    public String getDashboardLanguage() {
        return commons.getText(loc_btnLanguage);
    }	

    public boolean checkPresenceOfUpgradeNowPopUp() {
        boolean flag = (commons.getElements(loc_dlgUpgradeNow_lblMessage).size() > 0);
        logger.info("checkPresenceOfUpgradeNowPopUp: " + flag);
        return flag;
    }

    public boolean checkPresenceOfCloseUpgradeNowPopUpIcon() {
        boolean flag = (commons.getElements(loc_dlgUpgradeNow_btnClose).size() > 0);
        logger.info("checkPresenceOfCloseUpgradeNowPopUpIcon: " + flag);
        return flag;
    }

    public void clickUpgradeNow() {
        commons.click(loc_btnUpgradeNow);
        logger.info("Clicked on Upgrade Now button");
        new HomePage(driver).waitTillSpinnerDisappear();
    }

    public void closeUpgradeNowPopUp() {
        commons.click(loc_btnUpgradeNow,0);
        logger.info("Closed Upgrade Now Popup");
    }

    public void skipIntroduction() {
        commons.sleepInMiliSecond(2000); //Temporarily put sleep here.
        commons.click(loc_btnSkipIntroduction,0);
        logger.info("Skipped introduction.");
    }

    public HomePage verifyUpgradeNowMessage(String signupLanguage) throws Exception {
        commons.sleepInMiliSecond(2000); //Handle race condition
        String text = commons.getText(loc_dlgUpgradeNow_lblMessage,0);
        String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("home.upgradeNowTxt", signupLanguage);
        soft.assertEquals(text,retrievedMsg, "[Homepage][Upgrade Now Message] Message does not match.");
        logger.info("verifyUpgradeNowMessage completed");
        return this;
    }

    public HomePage selectLanguage(String language) {
        wait.until(ExpectedConditions.visibilityOf(commons.getElement(loc_btnLanguage)));
        if (!getDashboardLanguage().equals(language)) {
            commons.click(loc_btnLanguage);
            List<WebElement> languageElements = commons.getElements(loc_lst_btnLanguages);
            for (WebElement element : languageElements) {
                if (element.getText().equals(language)) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    break;
                }
            }
        }
        return this;
    }


    public String getToastMessage() {
        logger.info("Getting toast message.");
        String message = commons.getText(loc_lblToastMessage);
        commons.click(loc_toastMessage_btnClose);
        return message;
    }

    public void completeVerify() {
        soft.assertAll();
    }

    public Integer verifySalePitchPopupDisplay() {
        AssertCustomize assertCustomize = new AssertCustomize(driver);
        assertCustomize.assertTrue(commons.isElementDisplay(loc_dlgSalePitch), "Check Sale pitch video show");
        if (assertCustomize.getCountFalse() ==0) {
            logger.info("Sale pitch video is displayed");
        }
        return assertCustomize.getCountFalse();
    }

    public Integer verifySalePitchPopupNotDisplay() throws IOException {
        AssertCustomize assertCustomize = new AssertCustomize(driver);
        assertCustomize.assertFalse(commons.isElementDisplay(loc_dlgSalePitch), "Check Sale pitch video not show");
        return assertCustomize.getCountFalse();
    }

    public boolean isMenuClicked(WebElement element) {
//        commons.sleepInMiliSecond(1000);
        wait = new WebDriverWait(driver, Duration.ofSeconds(1));
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

    public boolean isStatisticsDisplayed() {
    	waitTillSpinnerDisappear1();
        return commons.getElements(loc_stnStatistics).size() >0;
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
        assertCustomize.assertTrue(isClicked, "Check Menu clickable");
        verifySalePitchPopupNotDisplay();
        if (path.contains("intro") || path.contains("info")) {
            commons.waitForElementInvisible(commons.getElement(loc_imgSpinner));
            commons.sleepInMiliSecond(1000);
        }
        if (pageName.equals("POS") || pageName.equals("Affiliate")) {
            commons.switchToWindow(1);
            commons.sleepInMiliSecond(1000);
            assertCustomize.assertEquals(commons.getCurrentURL(), DOMAIN + path, "Check URL of Page: " + pageName);
            commons.closeTab();
            commons.switchToWindow(0);
        } else {
            assertCustomize.assertEquals(commons.getCurrentURL(), DOMAIN + path, "Check URL of page: " + pageName);
            if (commons.getCurrentURL().contains("404")) {
                commons.navigateBack();
                logger.debug("Page show 404");
            }
        }
        LandingPage landingPage = new LandingPage(driver);
        if (pageName.equals("Marketing")) {
            waitTillSpinnerDisappear1();
            commons.sleepInMiliSecond(1000);
            if (landingPage.isPermissionModalDisplay()) {
                landingPage.closeModal();
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
            assertCustomize.assertFalse(isMenuClicked(commons.getElementByXpath(newXpath)), "Check Menu not clickable: " + pageName);
            if (!pageName.equals("Landing Page") || !pageName.equals("Marketing")) {
                commons.openNewTab();
                commons.switchToWindow(1);
                commons.navigateToURL(DOMAIN + path);
                assertCustomize.assertTrue(commons.getCurrentURL().contains("/404"), "Check url 404: " + pageName);
                commons.closeTab();
                commons.switchToWindow(0);
            } else {
                landingPage = new LandingPage(driver);
                if (landingPage.isPermissionModalDisplay()) {
                    landingPage.closeModal();
                }
            }
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
     * @param page        ALL: check all page, menuItemName: to check a page
     * @param packageType Input value: GoWeb, GoApp, GoPos, GoSocial, GoLead (ignore case)
     * @throws IOException
     */
    public HomePage checkPermissionAllPageByPackage(String page, String packageType) throws IOException {
        excel = new Excel();
        Sheet planPermissionSheet = excel.getSheet(FileNameAndPath.FILE_PLAN_PERMISSION, 0);
        int rowNumber = planPermissionSheet.getLastRowNum();
        String permissionParentMenu = "";
        for (int i = 1; i <= rowNumber; i++) {
            String menuItemExcel = planPermissionSheet.getRow(i).getCell(0).getStringCellValue();
            if (!page.equalsIgnoreCase("ALL") && !menuItemExcel.equalsIgnoreCase(page)) {
                logger.debug("Skip page: " + page);
                continue;
            }
            int packageColIndex = excel.getCellIndexByCellValue(planPermissionSheet.getRow(0), packageType);
            logger.debug("packageColIndex: " + packageColIndex);
            String permissionFromExcel = planPermissionSheet.getRow(i).getCell(packageColIndex).getStringCellValue();
            logger.debug("PagesExcel: " + menuItemExcel);
            String pathExcel = planPermissionSheet.getRow(i).getCell(1).getStringCellValue();
            String hasSalePitch = planPermissionSheet.getRow(i).getCell(2).getStringCellValue();
            String[] pageNames = menuItemExcel.split("-");
            if (pageNames.length == 1) {
                checkPermissionFromExcel(permissionFromExcel, pageNames[0], pathExcel, hasSalePitch);
                permissionParentMenu = permissionFromExcel;
            } else {
                if (permissionParentMenu.equalsIgnoreCase("D") && hasSalePitch.equalsIgnoreCase("N")) {
                    logger.info("Parent menu is disable");
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
            if (!page.equalsIgnoreCase("All") && menuItemExcel.equalsIgnoreCase(page)) {
                break;
            }
        }
        return this;
    }

    public HomePage completeVerifyPermissionByPackage() {
        logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
        if (assertCustomize.getCountFalse() > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
        }
        return this;
    }

    public HomePage verifyPageLoaded() {
        commons.waitElementVisible(commons.getElement(loc_lblWhatToDoNextTitle));
        return this;
    }

    public HomePage hideFacebookBubble() {
        commons.hideElement(commons.getElement(loc_imgFacebookBubble));
        logger.info("Hid Facebook bubble.");
        return this;
    }

    public void clickCreateProduct() {
        commons.click(loc_btnCreateProduct);
        logger.info("Clicked on 'Create Products' button");
        waitTillSpinnerDisappear1();
    }

    public void clickImportFromShopee() {
        commons.click(loc_btnImportFromShopee);
        logger.info("Clicked on 'Import From Shopee' button");
        waitTillSpinnerDisappear1();
        commons.sleepInMiliSecond(2000);
        new Shopee(driver).waitTillPageFinishLoading();
    }

    public void clickImportFromLazada() {
        commons.click(loc_btnImportFromLazada);
        logger.info("Clicked on 'Import From Lazada' button");
        waitTillSpinnerDisappear1();
    }

    public void clickChangeDesign() {
        commons.click(loc_lblCustomizeAppearance);
        commons.click(loc_btnChangeDesign);
        logger.info("Clicked on 'Change Design' button");
        waitTillSpinnerDisappear1();
    }

    public void clickDomain() {
        commons.click(loc_lblAddYourDomain);
        commons.click(loc_btnAddDomain);
        logger.info("Clicked on 'Add Domain' button");
        waitTillSpinnerDisappear1();
    }

    public void clickBankInformation() {
        commons.click(loc_lblAddBankAccount);
        commons.click(loc_btnBankInformation);
        logger.info("Clicked on 'Bank Information' button");
        waitTillSpinnerDisappear1();
        commons.sleepInMiliSecond(2000);
    }

    public String getShopName() {
        return commons.getText(loc_lblShopName);
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToDisplayStatistics(String permission) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(isStatisticsDisplayed());
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(isStatisticsDisplayed());
        } else {
            Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
        }
    }
    public void verifyPermissionToCreateProduct(String permission, LoginInformation loginInformation) {
        if (permission.contentEquals("A")) {
            clickCreateProduct();
            new ProductPage(driver, loginInformation).clickOnTheCreateProductBtn();
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
        }
    }
    public void verifyPermissionToImportProductFromShopee(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickImportFromShopee();
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
        }
    }
    public void verifyPermissionToImportProductFromLazada(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickImportFromLazada();
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
        }
    }
    public void verifyPermissionToAddDomain(String permission) {
        clickDomain();
        if (permission.contentEquals("A")) {
            new Domains(driver).inputSubDomain("testdomain");
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
        }
    }
    public void verifyPermissionToAddBankAccount(String permission) {
        if (permission.contentEquals("A")) {
            clickBankInformation();
            new BankAccountInformation(driver).selectCountry("Vietnam");
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
        }
    }
    public void verifyPermissionToCustomizeAppearance(String permission) {
        clickChangeDesign();
        new Themes(driver).verifyPermissionToCustomizeAppearance(permission);
    }

    /*-------------------------------------*/

    public void verifyTextOfPage() throws Exception {
        Assert.assertEquals(commons.getText(homeUI.loc_lblPageTitle), PropertiesUtil.getPropertiesValueByDBLang("home.pageTitle") + " " + getShopName());
        Assert.assertEquals(commons.getText(homeUI.loc_lblGoPOS), PropertiesUtil.getPropertiesValueByDBLang("home.POSLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblGoWeb),PropertiesUtil.getPropertiesValueByDBLang("home.GOWEBLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblGoWebBuilding),PropertiesUtil.getPropertiesValueByDBLang("home.GOWEBBuildingTxt"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblGoApp),PropertiesUtil.getPropertiesValueByDBLang("home.GOAPPLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblGoAppBuilding),PropertiesUtil.getPropertiesValueByDBLang("home.GOAPPBuildingTxt"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblSaleChannels),PropertiesUtil.getPropertiesValueByDBLang("home.saleChannelsLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblToConfirmOrders),PropertiesUtil.getPropertiesValueByDBLang("home.toConfirmOrdersLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblDeliveredOrders),PropertiesUtil.getPropertiesValueByDBLang("home.deliveredOrdersLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblToConfirmReservations),PropertiesUtil.getPropertiesValueByDBLang("home.toConfirmReservationsLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblCompletedReservations),PropertiesUtil.getPropertiesValueByDBLang("home.completedReservationsLbl"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblWhatToDoNextTitle),PropertiesUtil.getPropertiesValueByDBLang("home.whatToDoNext.title"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblWhatToDoNextDescription),PropertiesUtil.getPropertiesValueByDBLang("home.whatToDoNext.description"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddOrImportProduct),PropertiesUtil.getPropertiesValueByDBLang("home.addOrImportProducts.title"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddOrImportProductDescription),PropertiesUtil.getPropertiesValueByDBLang("home.addOrImportProduct.description"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddOrImportProductHint),PropertiesUtil.getPropertiesValueByDBLang("home.addOrImportProduct.hintTxt"));
        Assert.assertEquals(commons.getText(homeUI.loc_btnCreateProduct),PropertiesUtil.getPropertiesValueByDBLang("home.addOrImportProduct.createProductBtn"));
        Assert.assertEquals(commons.getText(homeUI.loc_btnImportFromShopee),PropertiesUtil.getPropertiesValueByDBLang("home.addOrImportProduct.importFromShopeeBtn"));
        Assert.assertEquals(commons.getText(homeUI.loc_btnImportFromLazada),PropertiesUtil.getPropertiesValueByDBLang("home.addOrImportProduct.importFromLazadaBtn"));
        commons.click(homeUI.loc_lblCustomizeAppearance);
        Assert.assertEquals(commons.getText(homeUI.loc_lblCustomizeAppearance),PropertiesUtil.getPropertiesValueByDBLang("home.customizeAppearance.title"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblCustomizeAppearanceDescription),PropertiesUtil.getPropertiesValueByDBLang("home.customizeAppearance.description"));
        Assert.assertEquals(commons.getText(homeUI.loc_btnChangeDesign),PropertiesUtil.getPropertiesValueByDBLang("home.customizeAppearance.changeDesignBtn"));
        commons.click(homeUI.loc_lblAddYourDomain);
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddYourDomain),PropertiesUtil.getPropertiesValueByDBLang("home.addYourDomain.title"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddYourDomainDescription),PropertiesUtil.getPropertiesValueByDBLang("home.addYourDomain.description"));
        Assert.assertEquals(commons.getText(homeUI.loc_btnAddDomain),PropertiesUtil.getPropertiesValueByDBLang("home.addYourDomain.addDomainBtn"));
        commons.click(homeUI.loc_lblAddBankAccount);
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddBankAccount),PropertiesUtil.getPropertiesValueByDBLang("home.addBankAccount.title"));
        Assert.assertEquals(commons.getText(homeUI.loc_lblAddBankAccountDescription),PropertiesUtil.getPropertiesValueByDBLang("home.addBankAccount.description"));
        Assert.assertEquals(commons.getText(homeUI.loc_btnBankInformation),PropertiesUtil.getPropertiesValueByDBLang("home.addBankAccount.bankInformation"));
    }
    public HomePage navigateToPageByURL(){
        commons.navigateToURL(DOMAIN);
        return this;
    }
}
