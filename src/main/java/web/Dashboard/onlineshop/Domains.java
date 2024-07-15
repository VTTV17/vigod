package web.Dashboard.onlineshop;

import api.Seller.sale_channel.onlineshop.APIDomain;
import api.Seller.setting.StoreInformation;
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
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class Domains {

    final static Logger logger = LogManager.getLogger(Domains.class);

    WebDriver driver;
    UICommonAction commonAction;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    LoginInformation sellerLoginInfo;


    public Domains(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public Domains getLoginInfo(LoginInformation sellerLoginInfo) {
        this.sellerLoginInfo = sellerLoginInfo;
        return this;
    }

    By loc_txtSubDomain = By.id("subDomain");
    By loc_txtNewDomain = By.id("newDomain");
    By loc_txtSampleDomain = By.cssSelector(".sub-domain__sample");
    By loc_btnSave = By.cssSelector(".gs-button__green");
    By loc_lblUpdateSuccess = By.cssSelector(".error-msg [type='success']");
    String url = Links.DOMAIN + "/channel/storefront/domain";

    public Domains inputSubDomain(String domain) {
        commonAction.sendKeys(loc_txtSubDomain, domain);
        logger.info("Input '" + domain + "' into Sub Domain field.");
        return this;
    }

    public String getGeneratedSampleDomain() {
        logger.info("Getting sample domain...");
        return commonAction.getText(loc_txtSampleDomain);
    }

    public Domains inputNewDomain(String domain) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtNewDomain).findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtNewDomain));
            return this;
        }
        commonAction.sendKeys(loc_txtNewDomain, domain);
        logger.info("Input '" + domain + "' into New Domain field.");
        return this;
    }

    public String getNewDomainValue() {
        logger.info("Getting new domain value...");
        return commonAction.getValue(loc_txtNewDomain);
    }

    public void clickOnSaveBtn() {
        commonAction.click(loc_btnSave);
        logger.info("Click on Save button.");
    }

    public void verifyPermissionToEditSubDomain(String permission) {
        if (permission.contentEquals("A")) {
            inputSubDomain("testpermission");
            Assert.assertTrue(getGeneratedSampleDomain().contains("testpermission"));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToEditNewDomain(String permission) {
        inputNewDomain("testpermission.com");
        String text = getNewDomainValue();
        if (permission.contentEquals("A")) {
            Assert.assertTrue(text.contentEquals("testpermission.com"));
        } else if (permission.contentEquals("D")) {
            Assert.assertTrue(text.contentEquals(""));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public Domains navigateByUrl() {
        commonAction.navigateToURL(url);
        logger.info("Navigate to url: " + url);
        return this;
    }

    public void callAPIUpdateSubDomain(String subDomain) {
        boolean isDomainExist = new APIDomain(sellerLoginInfo).checkSubDomainExist(subDomain);
        if (!isDomainExist) {
            new APIDomain(sellerLoginInfo).updateSubDomain(subDomain);
        } else logger.info("%s sub domain is exist, so no need update.".formatted(subDomain));
    }

    public void callAIPDeleteNewDomain() {
        int newDomainId = new APIDomain(sellerLoginInfo).getNewDomainId();
        if (newDomainId != 0) {
            new APIDomain(sellerLoginInfo).deleteNewDomain(newDomainId);
        } else logger.info("The store don't have custom domain, so no need delete custom domain.");
    }

    /**********************Staff permission***************/
    public boolean hasEditSubDomain() {
        return allPermissions.getOnlineStore().getDomain().isEditSubdomain();
    }

    public boolean hasEditCustomDomain() {
        return allPermissions.getOnlineStore().getDomain().isEditCustomDomain();
    }

    public boolean hasDomainPermission() {
        boolean[] domainPermisison = {
                hasEditSubDomain(),
                hasEditCustomDomain(),
        };
        for (boolean permission : domainPermisison) if (permission) return true;
        return false;
    }

    public void checkUpdateSubDomain() {
        inputSubDomain(new DataGenerator().generateString(10));
        if (hasEditSubDomain()) {
            String currentSubDomain = new StoreInformation(sellerLoginInfo).getInfo().getStoreURL();
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnSave, loc_lblUpdateSuccess),
                    "[Failed] Updated Success label should be shown");
            callAPIUpdateSubDomain(currentSubDomain);
        } else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSave),
                "[Failed] Restricted popup should be shown when click save update sub domain.");
        logger.info("Verified Update sub domain permission.");
    }

    public void checkEditCustomDomain() {
        navigateByUrl();
        inputNewDomain(new DataGenerator().generateString(5).toLowerCase()+".com");
        if (hasEditCustomDomain()) {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnSave, loc_lblUpdateSuccess),
                    "[Failed] Updated success custom domain label should be shown");
            callAIPDeleteNewDomain();
        } else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSave),
                "[Failed] Restricted popup should be shown when click save update custom domain.");
        logger.info("Verified Update custom domain permission.");
    }

    public Domains checkDomainPermission(AllPermissions allPermissions) {
        this.allPermissions = allPermissions;
        if (hasDomainPermission()) {
            new HomePage(driver).navigateToPage("Online Shop","Domains");
            commonAction.sleepInMiliSecond(2000);
            checkUpdateSubDomain();
            checkEditCustomDomain();
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
                "[Failed] Restricted page should be shown when don't have any permission in Domain permision group.");
        AssertCustomize.verifyTest();
        return this;
    }
}
