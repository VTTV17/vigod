package web.Dashboard.onlineshop.preferences;

import api.Seller.sale_channel.onlineshop.APIPreferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class Configuration {

    final static Logger logger = LogManager.getLogger(Configuration.class);

    WebDriver driver;
    UICommonAction commonAction;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;

    public Configuration(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public Configuration getLoginInfo(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        return this;
    }

    By loc_btnEnableFacebookMessengerToggle = By.xpath("(//div[contains(@class,'live-chat-configuration')]//section)[1]//h3[contains(.,'Facebook')]/following-sibling::*");
    By loc_btnEnableZaloOAToggle = By.xpath("(//div[contains(@class,'live-chat-configuration')]//section)[2]//h3[contains(.,'Zalo')]/following-sibling::*");
    By loc_btnEnableLoginViaFacebookToggle = By.xpath("(//div[contains(@class,'live-chat-configuration')]//section)[3]//h3[contains(.,'login') or contains(.,'đăng nhập')]/following-sibling::*");
    By loc_txtGoogleAnalytics = By.id("gaCode");
    By loc_txtHTMLTag = By.id("gvHtmlTag");
    By loc_txtGoogleTagManager = By.id("note");
    By loc_txtFacebookPixel = By.id("fbPixelId");
    By loc_txtFacebookAppId = By.id("fbAppId");
    By loc_btnComplete = By.cssSelector(".btn-next");
    By loc_txtFacebookPageId = By.id("pageId");
    By loc_ctnFacebookChatConfigDetail = By.xpath("//input[@id='pageId']//ancestor::div[contains(@class,'preference-content-detail')]");
    By loc_ctnZaloChatConfigDetail = By.xpath("//input[@id='pageIdZalo']//ancestor::div[contains(@class,'preference-content-detail')]");
    By loc_ctnFacebookLoginConfigDetail = By.xpath("//input[@id='clientId']//ancestor::div[contains(@class,'preference-content-detail')]");
    By loc_ctnMultipleCurrencyConfigDetail = By.cssSelector(".multiple-currency__body");
    By loc_btnGuestCheckoutToggle = By.cssSelector(".checkout-information .uik-checkbox__toggle");
    By loc_btnMultipleCurrencyToggle = By.cssSelector(".multiple-currency .uik-checkbox__toggle");
    By loc_btnSaveAll = By.cssSelector(".gs-button__green");
    By loc_txtZaloId = By.id("pageIdZalo");
    By loc_txtWelcomeText = By.id("welcomeTextZalo");
    By loc_txtClientId = By.id("clientId");
    By loc_txtClientSecretKey = By.id("clientSecretKey");
    By loc_ctnMultipleCurrency_btnSave = By.cssSelector(".multiple-currency .gs-button__blue");
    By loc_cntProductListing_btnToggleAction = By.xpath("(//div[contains(@class,'setting__listing_website')]//label[contains(@class,'uik-checkbox__toggle')])[1]");
    By loc_cntProductListing_toggleValue = By.xpath("(//div[contains(@class,'setting__listing_website')]//label[contains(@class,'uik-checkbox__toggle')])[1]//input");
    By loc_cntServiceListing_btnToggleAction = By.xpath("(//div[contains(@class,'setting__listing_website')]//label[contains(@class,'uik-checkbox__toggle')])[2]");
    By loc_cntServiceListing_toggleValue = By.xpath("(//div[contains(@class,'setting__listing_website')]//label[contains(@class,'uik-checkbox__toggle')])[2]//input");
    By loc_cntProductListing_chkPhone = By.id("phone-number-product");
    By loc_cntProductListing_txtPhone = By.id("productphone");
    By loc_cntServiceListing_chkPhone = By.id("phone-number-service");
    By loc_cntServiceListing_txtPhone = By.id("servicephone");
    By loc_btnSaveListingWebsite = By.cssSelector(".listing__website .gs-button__blue");
    By loc_btnSaveGoogleAnalytics = By.cssSelector(".google-analytics .gs-button__blue");
    By loc_btnSaveGoogleShopping = By.cssSelector(".google-shopping .gs-button__blue");
    By loc_btnSaveGoogleTagMangager = By.cssSelector(".google-tag-manager .gs-button__blue");
    By loc_btnSaveFacebookPixel = By.cssSelector(".facebook-pixel .gs-button__blue");
    By loc_btnSaveCustomWebsiteData = By.cssSelector(".custom-website-data .gs-button__blue");
    By loc_txaInputBody = By.id("customBody");
    By loc_cntGoogleShopping_btnAllProducts = By.cssSelector(".preference-content-title .btn-save:nth-child(1)");
    By loc_cntGoogleShopping_btnSpecificProduct = By.cssSelector(".preference-content-title .btn-save:nth-child(2)");
    By loc_dlgProductSelection_chkProductList = By.cssSelector(".product-list-barcode-printer__product-list .uik-checkbox__label");

    String url = Links.DOMAIN + Links.PREFERENCE_PATH;

    public void navigateByUrl() {
        commonAction.navigateToURL(url);
        logger.info("Navigate to url: " + url);
        commonAction.sleepInMiliSecond(500, "Wait page load.");
    }

    public Configuration clickEnableFacebookMessengerToggle() {
        commonAction.clickJS(loc_btnEnableFacebookMessengerToggle);
        logger.info("Clicked on 'Enable Facebook Messenger' toggle button.");
        return this;
    }

    public Configuration clickEnableZaloOAMessengerToggle() {
//        commonAction.sleepInMiliSecond(500);
        commonAction.clickJS(loc_btnEnableZaloOAToggle);
        logger.info("Clicked on 'Zalo OA Messenger' toggle button.");
        return this;
    }

    public Configuration clickEnableFacebookLoginForOnlineStoreToggle() {
//        commonAction.sleepInMiliSecond(500);
        commonAction.clickJS(loc_btnEnableLoginViaFacebookToggle);
        logger.info("Clicked on 'Enable Facebook Login For Online Store' toggle button.");
        return this;
    }

    public Configuration inputGoogleAnalyticsCode(String code) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtGoogleAnalytics).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtGoogleAnalytics));
            return this;
        }
        commonAction.sendKeys(loc_txtGoogleAnalytics, code);
        logger.info("Input '" + code + "' into Google Analytics Code field.");
        return this;
    }

    public String getGoogleAnalyticsCode() {
        String text = commonAction.getValue(loc_txtGoogleAnalytics);
        logger.info("Retrieved Google Analytics Code: " + text);
        return text;
    }

    public Configuration inputHTMLTag(String htmlTag) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtHTMLTag).findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtHTMLTag));
            return this;
        }
        commonAction.sendKeys(loc_txtHTMLTag, htmlTag);
        logger.info("Input '" + htmlTag + "' into HTML Tag field.");
        return this;
    }

    public String getHTMLTag() {
        String text = commonAction.getValue(loc_txtHTMLTag);
        logger.info("Retrieved HTML Tag: " + text);
        return text;
    }

    public Configuration inputGoogleTagManager(String tag) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtGoogleTagManager).findElement(By.xpath("./parent::*/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtGoogleTagManager));
            return this;
        }
        commonAction.sendKeys(loc_txtGoogleTagManager, tag);
        logger.info("Input '" + tag + "' into Google Tag Manager field.");
        return this;
    }

    public String getGoogleTagManager() {
        String text = commonAction.getValue(loc_txtGoogleTagManager);
        logger.info("Retrieved Google Tag Manager: " + text);
        return text;
    }

    public Configuration inputFacebookPixel(String pixel) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtFacebookPixel).findElement(By.xpath("./parent::*/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtFacebookPixel));
            return this;
        }
        commonAction.sendKeys(loc_txtFacebookPixel, pixel);
        logger.info("Input '" + pixel + "' into Facebook Pixel field.");
        return this;
    }

    public String getFacebookPixel() {
        String text = commonAction.getValue(loc_txtFacebookPixel);
        logger.info("Retrieved Facebook Pixel: " + text);
        return text;
    }

    public Configuration inputFacebookAppID(String id) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtFacebookAppId).findElement(By.xpath("./parent::*/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtFacebookAppId));
            return this;
        }
        commonAction.sendKeys(loc_txtFacebookAppId, id);
        logger.info("Input '" + id + "' into Facebook App ID field.");
        return this;
    }

    public String getFacebookAppID() {
        String text = commonAction.getValue(loc_txtFacebookAppId);
        logger.info("Retrieved Facebook App ID: " + text);
        return text;
    }

    public Configuration inputFacebookPageId(String fbPageId) {
        commonAction.inputText(loc_txtFacebookPageId, fbPageId);
        logger.info("Input %s into facebook page id.".formatted(fbPageId));
        return this;
    }

    public Configuration clickSaveAllConfig() {
        commonAction.click(loc_btnSaveAll);
        logger.info("Click on Save all config");
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public Configuration inputZaloAccountId(String zaloId) {
        commonAction.inputText(loc_txtZaloId, zaloId);
        logger.info("input %s into zalo account id field.".formatted(zaloId));
        return this;
    }

    public Configuration inputZaloOAWelcomeText(String text) {
        commonAction.inputText(loc_txtWelcomeText, text);
        logger.info("Input %s into Welcome text of Zalo OA.".formatted(text));
        return this;
    }

    public Configuration inputZaloOAMessengerConfig() {
        String random = new DataGenerator().generateString(10);
        inputZaloAccountId(random);
        inputZaloOAWelcomeText(random);
        return this;
    }

    public Configuration inputClientId(String clientId) {
        commonAction.inputText(loc_txtClientId, clientId);
        logger.info("Input %s into client id field".formatted(clientId));
        return this;
    }

    public Configuration inputClientSecretKey(String clientSecretKey) {
        commonAction.inputText(loc_txtClientSecretKey, clientSecretKey);
        logger.info("Input %s into client secret key.".formatted(clientSecretKey));
        return this;
    }
    public String getClientId(){
        String clientId = "";
        if(!isContentConfigInvisible(loc_ctnFacebookLoginConfigDetail)){
            clientId = commonAction.getValue(loc_txtClientId);
        }
        logger.info("Get client id: "+clientId);
        return clientId;
    }
    public Configuration inputLoginFacebookConfig() {
        String random = new DataGenerator().generateString(10);
        if(getFacebookAppID().equals("")) inputClientId(random);
        else inputClientId(getFacebookAppID());
        inputClientSecretKey(random);
        return this;
    }

    public Configuration clickOnGuestCheckoutToggle() {
        commonAction.sleepInMiliSecond(300);
        commonAction.clickJS(loc_btnGuestCheckoutToggle);
        logger.info("Click on Guest checkout Toggle.");
        return this;
    }

    public Configuration clickMultipleCurrencyToggle() {
        commonAction.sleepInMiliSecond(300);
        commonAction.clickJS(loc_btnMultipleCurrencyToggle);
        logger.info("Click on Multiple currency toggle.");
        return this;
    }

    public Configuration clickOnSaveMultipleCurrency() {
        commonAction.click(loc_ctnMultipleCurrency_btnSave);
        logger.info("Click on Save multiple currency.");
        return this;
    }

    public Configuration clickProductListingToggle() {
        commonAction.clickJS(loc_cntProductListing_btnToggleAction);
        logger.info("Click on Product listing toggle button");
        return this;
    }

    public Configuration clickServiceListingToggle() {
        commonAction.clickJS(loc_cntServiceListing_btnToggleAction);
        logger.info("Click on Service listing toggle button");
        return this;
    }

    public Configuration clickSaveListingWebsite() {
        commonAction.click(loc_btnSaveListingWebsite);
        logger.info("Click Save button on Listing Website.");
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public Configuration clickSaveGoogleAnalytics() {
        commonAction.click(loc_btnSaveGoogleAnalytics);
        logger.info("Click save on Google Analytics.");
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public Configuration clickSaveGoogleShopping() {
        commonAction.click(loc_btnSaveGoogleShopping);
        logger.info("Click save on Google Shopping");
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public Configuration clickExportAllProduct() {
        commonAction.click(loc_cntGoogleShopping_btnAllProducts);
        logger.info("Click on Export All product on Google Shopping.");
        return this;
    }

    public Configuration clickExportSpecificProductBtn() {
        commonAction.click(loc_cntGoogleShopping_btnSpecificProduct);
        logger.info("Click on Export Specific product on Google Shopping.");
        return this;
    }

    public Configuration selectProductToExport() {
        commonAction.click(loc_dlgProductSelection_chkProductList, 0);
        logger.info("Select product on ProductSelection popup");
        new ConfirmationDialog(driver).clickGreenBtn();
        logger.info("Click on Next step button.");
        new ConfirmationDialog(driver).clickGreenBtn();
        logger.info("Click on Export file button.");
        return this;
    }

    public void clickSaveFacebookPixel() {
        commonAction.click(loc_btnSaveFacebookPixel);
        logger.info("Click on Save Facebook pixel.");
        commonAction.sleepInMiliSecond(1000);
    }

    public Configuration clickSaveGoogleTagManager() {
        commonAction.clickJS(loc_btnSaveGoogleTagMangager);
        logger.info("Click on Save Google Tab Manager.");
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public Configuration checkAndInputProductPhoneListing() {
        commonAction.checkTheCheckBoxOrRadio(loc_cntProductListing_chkPhone);
        commonAction.inputText(loc_cntProductListing_txtPhone, "09" + new DataGenerator().generateNumber(8));
        return this;
    }

    public Configuration checkAndInputServicePhoneListing() {
        commonAction.checkTheCheckBoxOrRadio(loc_cntServiceListing_chkPhone);
        commonAction.inputText(loc_cntServiceListing_txtPhone, "09" + new DataGenerator().generateNumber(8));
        return this;
    }

    public void inputDataBody(String bodyCode) {
        commonAction.inputText(loc_txaInputBody, bodyCode);
        logger.info("Input %s into body data textarea".formatted(bodyCode));
    }

    public void clickSaveCustomerWebsiteData() {
        commonAction.sleepInMiliSecond(1000);
        commonAction.click(loc_btnSaveCustomWebsiteData);
        logger.info("Click on Save customer website data.");
        commonAction.sleepInMiliSecond(1000);
    }

    public void refreshThisPage() {
        new HomePage(driver).waitTillLoadingDotsDisappear();
        commonAction.refreshPage();
        new HomePage(driver).waitTillSpinnerDisappear1();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        commonAction.sleepInMiliSecond(1000);
    }

    public void verifyPermissionToEnableFacebookMessenger(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickEnableFacebookMessengerToggle();
            commonAction.navigateBack();
            new ConfirmationDialog(driver).clickOKBtn();
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToEnableZaloOAMessenger(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickEnableZaloOAMessengerToggle();
            commonAction.navigateBack();
            new ConfirmationDialog(driver).clickOKBtn();
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToEnableFacebookLoginForOnlineStore(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickEnableFacebookLoginForOnlineStoreToggle();
            commonAction.navigateBack();
            new ConfirmationDialog(driver).clickOKBtn();
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToConfigureGoogleAnalytics(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
            inputGoogleAnalyticsCode("Test Permission");
            Assert.assertTrue(getGoogleAnalyticsCode().contentEquals("Test Permission"));
        } else if (permission.contentEquals("D")) {
            if (commonAction.getCurrentURL().contains(url)) {
                inputGoogleAnalyticsCode("Test Permission");
                Assert.assertTrue(getGoogleAnalyticsCode().contentEquals(""));
            }
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToConfigureGoogleShopping(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
            inputHTMLTag("Test Permission");
            Assert.assertTrue(getHTMLTag().contentEquals("Test Permission"));
        } else if (permission.contentEquals("D")) {
            if (commonAction.getCurrentURL().contains(url)) {
                inputHTMLTag("Test Permission");
                Assert.assertTrue(getHTMLTag().contentEquals(""));
            }
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToConfigureGoogleTagManager(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
            inputGoogleTagManager("Test Permission");
            Assert.assertTrue(getGoogleTagManager().contentEquals("Test Permission"));
        } else if (permission.contentEquals("D")) {
            if (commonAction.getCurrentURL().contains(url)) {
                inputGoogleTagManager("Test Permission");
                Assert.assertTrue(getGoogleTagManager().contentEquals(""));
            }
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToConfigureFacebookPixel(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
            inputFacebookPixel("Test Permission");
            Assert.assertTrue(getFacebookPixel().contentEquals("Test Permission"));
        } else if (permission.contentEquals("D")) {
            if (commonAction.getCurrentURL().contains(url)) {
                inputFacebookPixel("Test Permission");
                Assert.assertTrue(getFacebookPixel().contentEquals(""));
            }
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToConfigureFacebookAppID(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
            inputFacebookAppID("Test Permission");
            Assert.assertTrue(getFacebookAppID().contentEquals("Test Permission"));
        } else if (permission.contentEquals("D")) {
            if (commonAction.getCurrentURL().contains(url)) {
                inputFacebookAppID("Test Permission");
                Assert.assertTrue(getFacebookAppID().contentEquals(""));
            }
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public boolean isContentConfigInvisible(By locator) {
        String classValue = commonAction.getAttribute(locator, "class");
        return classValue.contains("invisible");
    }

    /***********************StaffPermission********************/
    public boolean hasEnableDisableFBChat() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableFBChat();
    }

    public boolean hasEnableDisableZaloChat() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableZaloChat();
    }

    public boolean hasEnableDisableFacebookLogin() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableFacebookLogin();
    }

    public boolean hasEnableDisableMulticurrency() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableMulticurrency();
    }

    public boolean hasEnableDisableGuestCheckout() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableGuestCheckout();
    }

    public boolean hasEnableDisableProductListing() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableProductListing();
    }

    public boolean hasEnableDisableServiceListing() {
        return allPermissions.getOnlineStore().getPreferences().isEnableDisableServiceListing();
    }

    public boolean hasAddRemoveGoogleAnalyticsCode() {
        return allPermissions.getOnlineStore().getPreferences().isAddRemoveGoogleAnalyticsCode();
    }

    public boolean hasAddRemoveGoogleShoppingCode() {
        return allPermissions.getOnlineStore().getPreferences().isAddRemoveGoogleShoppingCode();
    }

    public boolean hasExportGoogleShoppingProduct() {
        return allPermissions.getOnlineStore().getPreferences().isExportGoogleShoppingProduct();
    }

    public boolean hasAddRemoveGoogleTabManager() {
        return allPermissions.getOnlineStore().getPreferences().isAddRemoveGoogleTagManager();
    }

    public boolean hasAddRemoveFacebookPixel() {
        return allPermissions.getOnlineStore().getPreferences().isAddRemoveFacebookPixel();
    }

    public boolean hasUpdateCustomCode() {
        return allPermissions.getOnlineStore().getPreferences().isUpdateCustomCode();
    }

    public boolean hasViewProductList() {
        return allPermissions.getProduct().getProductManagement().isViewProductList();
    }

    public boolean hasPreferencesPermission() {
        boolean[] domainPermisison = {
                hasEnableDisableFBChat(),
                hasEnableDisableZaloChat(),
                hasEnableDisableFacebookLogin(),
                hasEnableDisableMulticurrency(),
                hasEnableDisableGuestCheckout(),
                hasEnableDisableProductListing(),
                hasEnableDisableServiceListing(),
                hasAddRemoveGoogleAnalyticsCode(),
                hasAddRemoveGoogleShoppingCode(),
                hasExportGoogleShoppingProduct(),
                hasAddRemoveGoogleTabManager(),
                hasAddRemoveFacebookPixel(),
                hasUpdateCustomCode()
        };
        for (boolean permission : domainPermisison) if (permission) return true;
        return false;
    }

    public void checkEnableDisableFBChat() {
        navigateByUrl();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        boolean fbChatConfigInvisible = isContentConfigInvisible(loc_ctnFacebookChatConfigDetail);
        if (hasEnableDisableFBChat()) {
            clickEnableFacebookMessengerToggle();
            if (fbChatConfigInvisible) {
                inputFacebookPageId(new DataGenerator().generateString(10));
                clickSaveAllConfig();
                refreshThisPage();
//                commonAction.sleepInMiliSecond(500);
                assertCustomize.assertFalse(isContentConfigInvisible(loc_ctnFacebookChatConfigDetail), "[Failed] FB config detail should be visible.");
            } else {
                clickSaveAllConfig();
                refreshThisPage();
                assertCustomize.assertTrue(isContentConfigInvisible(loc_ctnFacebookChatConfigDetail), "[Failed] FB config detail should be invisible.");
            }
        } else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnEnableFacebookMessengerToggle),
                    "[Failed] Restricted popup should be shown when click FB messenger toggle.");
        }
        logger.info("Verified enable/disable FB Chat permission.");
    }

    public void checkEnableDisableZaloChat() {
        navigateByUrl();
        boolean zaloConfigDetailInvisible = isContentConfigInvisible(loc_ctnZaloChatConfigDetail);
        if (hasEnableDisableZaloChat()) {
            clickEnableZaloOAMessengerToggle();
            if (zaloConfigDetailInvisible) {
                inputZaloOAMessengerConfig();
                clickSaveAllConfig();
//                commonAction.sleepInMiliSecond(1000);
                refreshThisPage();
                assertCustomize.assertFalse(isContentConfigInvisible(loc_ctnZaloChatConfigDetail), "[Failed] Zalo config detail should be visible.");
            } else {
                clickSaveAllConfig();
                refreshThisPage();
                assertCustomize.assertTrue(isContentConfigInvisible(loc_ctnZaloChatConfigDetail), "[Failed] Zalo config detail should be invisible");
            }
        } else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnEnableZaloOAToggle),
                    "[Failed] Restricted popup should be shown when click Zalo OA toggle");
        }
        logger.info("Verified Enable/Disable Zalo Chat permission.");
    }

    public void checkEnableDisableFacebookLogin() {
        navigateByUrl();
        boolean fbLoginConfigDetailInvisible = isContentConfigInvisible(loc_ctnFacebookLoginConfigDetail);
        if (hasEnableDisableFacebookLogin()) {
            clickEnableFacebookLoginForOnlineStoreToggle();
            if (fbLoginConfigDetailInvisible) {
                inputLoginFacebookConfig();
                clickSaveAllConfig();
                commonAction.sleepInMiliSecond(1000);
                refreshThisPage();
                commonAction.sleepInMiliSecond(2000);
                assertCustomize.assertFalse(isContentConfigInvisible(loc_ctnFacebookLoginConfigDetail), "[Failed] Login Facebook config detail should be visible");
            } else {
                clickSaveAllConfig();
                refreshThisPage();
                assertCustomize.assertTrue(isContentConfigInvisible(loc_ctnFacebookLoginConfigDetail), "[Failed] Login Facebook config detail should be invisible");
            }
        } else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnEnableLoginViaFacebookToggle),
                    "[Failed] Restricted popup should be shown when click Login FB toggle.");
        }
        logger.info("Verified Enable/Disable FB login permission.");
    }

    public void checkEnableDisableGuestChechout() {
        navigateByUrl();
        if (hasEnableDisableGuestCheckout()) {
            boolean currentStatus = new APIPreferences(loginInformation).getGuestCheckoutStatus();
            logger.info("Guest checkout: current status: "+currentStatus);
            clickOnGuestCheckoutToggle();
            commonAction.sleepInMiliSecond(1500,"Wait api called.");
            boolean newStatus = new APIPreferences(loginInformation).getGuestCheckoutStatus();
            logger.info("Guest checkout: new status: "+newStatus);
            assertCustomize.assertEquals(newStatus, !currentStatus,
                    "[Failed] Guest checkout status not updated");
        } else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnGuestCheckoutToggle),
                "[Failed] Restricted popup should be shown.");
        logger.info("Verified Enable/Disable Guest checkout permission.");
    }

    public void checkEnableDisableMulticurrency() {
        navigateByUrl();
        if (hasEnableDisableMulticurrency()) {
            boolean currencyTableShow = commonAction.getElements(loc_ctnMultipleCurrencyConfigDetail, 1).size() > 0 ? true : false;
            clickMultipleCurrencyToggle();
            commonAction.sleepInMiliSecond(500,"Wait multiple currency table show/hide");
            if (currencyTableShow) {
                assertCustomize.assertTrue(commonAction.getElements(loc_ctnMultipleCurrencyConfigDetail).isEmpty(),
                        "[Failed] Currency table should be closed when click on toggle.");
            } else
                assertCustomize.assertTrue(commonAction.getElements(loc_ctnMultipleCurrencyConfigDetail).size() > 0,
                        "[Failed] Currency table should be shown when click on toggle.");
        } else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnMultipleCurrencyToggle),
                    "[Failed] Restricted popup should be shown when click on Multiple currency toggle.");
        logger.info("Verified Enable/Disable Multicurrency permission.");
    }

    public void checkEnableDisableProductListing() {
        navigateByUrl();
        if (hasEnableDisableProductListing()) {
            boolean productListingToggleTurnOn = commonAction.isCheckedJS(loc_cntProductListing_toggleValue);
            clickProductListingToggle();
            if (productListingToggleTurnOn) {
                clickSaveListingWebsite();
                refreshThisPage();
                assertCustomize.assertFalse(commonAction.isCheckedJS(loc_cntProductListing_toggleValue),
                        "[Failed] Product listing toggle should be turn off");
            } else {
                if (!commonAction.isCheckedJS(loc_cntProductListing_chkPhone)) {
                    checkAndInputProductPhoneListing();
                }
                clickSaveListingWebsite();
                refreshThisPage();
                assertCustomize.assertTrue(commonAction.isCheckedJS(loc_cntProductListing_toggleValue),
                        "[Failed] Product listing toggle should be turn on.");
            }
        } else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_cntProductListing_btnToggleAction),
                    "[Failed] Restricted popup should be shown when click Product Listing toggle.");
        logger.info("Verified Enable/Disable product listing permission.");
    }

    public void checkEnableDisableServiceListing() {
        navigateByUrl();
        if (hasEnableDisableServiceListing()) {
            boolean serviceListingToggleTurnOn = commonAction.isCheckedJS(loc_cntServiceListing_toggleValue);
            clickServiceListingToggle();
            if (serviceListingToggleTurnOn) {
                clickSaveListingWebsite();
                refreshThisPage();
                assertCustomize.assertFalse(commonAction.isCheckedJS(loc_cntServiceListing_toggleValue),
                        "[Failed] Service listing toggle should be turn off");
            } else {
                if (!commonAction.isCheckedJS(loc_cntServiceListing_chkPhone)) {
                    checkAndInputServicePhoneListing();
                }
                clickSaveListingWebsite();
                refreshThisPage();
                commonAction.sleepInMiliSecond(1000);
                assertCustomize.assertTrue(commonAction.isCheckedJS(loc_cntServiceListing_toggleValue),
                        "[Failed] Service listing toggle should be turn on.");
            }
        } else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_cntServiceListing_btnToggleAction),
                    "[Failed] Restricted popup should be shown when click Service Listing toggle.");
        logger.info("Verified Enable/Disable service listing permission.");
    }

    public void checkAddRemoveGoogleAnalyticsCode() {
        if (hasAddRemoveGoogleAnalyticsCode()) {
            String random = new DataGenerator().generateString(10);
            inputGoogleAnalyticsCode(random);
            clickSaveGoogleAnalytics();
            refreshThisPage();
            assertCustomize.assertEquals(getGoogleAnalyticsCode(), random,
                    "[Failed] Google Analytics code should be updated, actual: %s, expect: %s".formatted(getGoogleAnalyticsCode(), random));
        } else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSaveGoogleAnalytics),
                "[Failed] Restricted popup should be shown when click on Save Google analytics code.");
        logger.info("Verified Add Remove Google Analytics Code");
    }

    public void checkAddRemoveGoogleShoppingCode() {
        if (hasAddRemoveGoogleShoppingCode()) {
            String googleShoppingCode = "google-site-verification=U11FQ1z9eOLWzkbjgB259W3O1j1EhOu6eOfZekBV65w";
            inputHTMLTag(googleShoppingCode);
            clickSaveGoogleShopping();
            refreshThisPage();
            assertCustomize.assertEquals(getHTMLTag(), googleShoppingCode,
                    "[Failed] Google shopping code should be updated.\nactual: %s.\nexpected: %s".formatted(getHTMLTag(), googleShoppingCode));

        } else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSaveGoogleShopping),
                "[Failed] Restricted popup should be shown when click save on Google Shopping.");
        logger.info("Verified Add Remove google shopping code");
    }

    public void checkExportGoogleShoppingProduct() {
        navigateByUrl();
        if (hasExportGoogleShoppingProduct()) {
            //Check export All product
            //Delete old file.
            new FileUtils().deleteFileInDownloadFolder("rss");
            //Download new file
            clickExportAllProduct();
            commonAction.sleepInMiliSecond(3000, "Waiting for download.");
            assertCustomize.assertTrue(new FileUtils().isDownloadSuccessful("rss"), "[Failed] Not found all product file in download folder.");
            //Check export Specific product
            if (hasViewProductList()) {
                //Delete old file.
                new FileUtils().deleteFileInDownloadFolder("rss");
                //Download new file
                clickExportSpecificProductBtn();
                selectProductToExport();
                commonAction.sleepInMiliSecond(3000, "Waiting for download.");
                assertCustomize.assertTrue(new FileUtils().isDownloadSuccessful("rss"), "[Failed] Not found specific product file in download folder.");
            }
        } else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_cntGoogleShopping_btnAllProducts),
                    "[Failed] Restricted popup should be shown when click Export all product.");
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_cntGoogleShopping_btnSpecificProduct),
                    "[Failed] Restricted popup should be shown when click Export specific product.");
        }
        logger.info("Verified export google shopping permission.");
    }

    public void checkAddRemoveGoogleTagManager() {
        navigateByUrl();
        if (hasAddRemoveGoogleTabManager()) {
            commonAction.inputText(loc_txtGoogleTagManager, new DataGenerator().generateString(5));
            clickSaveGoogleTagManager();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.preferences.update.success"),
                        "[Failed] Update success message should be shown when click Save google tab manager.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSaveGoogleTagMangager),
                    "[Failed] Restricted popup should be shown when click on Save Google tab manager.");
        }
        logger.info("Verified Add/Remove Google Tab Manager");
    }

    public void checkAddRemoveFacebookPixel() {
        navigateByUrl();
        String random = new DataGenerator().generateString(10);
        inputFacebookPixel(random);
        String appId = getClientId().equals("")?random:getClientId();
        inputFacebookAppID(appId);
        if (hasAddRemoveFacebookPixel()) {
            clickSaveFacebookPixel();
            refreshThisPage();
            assertCustomize.assertEquals(getFacebookPixel(), random,
                    "[Failed] Facebook pixel not updated correctly, actual: %s, expected: %s".formatted(getFacebookPixel(), random));
        } else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSaveFacebookPixel),
                "[Failed] Restricted popup should be shown when click save facebook pixel.");
        logger.info("Verified Add/Remove Facebook pixel permission.");
    }

    public void checkUpdateCustomCode() {
        navigateByUrl();
        String random = new DataGenerator().generateString(10);
        inputDataBody(random);
        if (hasUpdateCustomCode()) {
            clickSaveCustomerWebsiteData();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.preferences.update.success"),
                        "[Failed] Update success message should be shown when click save customer website.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSaveCustomWebsiteData),
                    "[Failed] Restricted popup should be shown when click Save Custom website data.");
        logger.info("Verified update custom website data permission");
    }

    public Configuration checkPreferencesPermission(AllPermissions allPermissions) {
        this.allPermissions = allPermissions;
        logger.info("PreferencesPermission: "+allPermissions.getOnlineStore().getPreferences());
        if(hasPreferencesPermission()) {
            checkEnableDisableFBChat();
            checkEnableDisableZaloChat();
            checkEnableDisableFacebookLogin();
            checkEnableDisableMulticurrency();
            checkEnableDisableGuestChechout();
            checkEnableDisableProductListing();
            checkEnableDisableServiceListing();
            checkAddRemoveGoogleAnalyticsCode();
            checkAddRemoveGoogleShoppingCode();
            checkExportGoogleShoppingProduct();
            checkAddRemoveGoogleTagManager();
            checkAddRemoveFacebookPixel();
            checkUpdateCustomCode();
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
                "[Failed] Restricted page should be shown when navigate to Preference url.");
        AssertCustomize.verifyTest();
        return this;
    }
}
