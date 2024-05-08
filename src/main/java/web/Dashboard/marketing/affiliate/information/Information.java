package web.Dashboard.marketing.affiliate.information;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.constant.Constant;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.OnlineStore.Domain;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.marketing.buylink.CreateBuyLink;
import web.Dashboard.promotion.discount.DiscountPage;

import java.util.List;

public class Information extends InformationElement{
    final static Logger logger = LogManager.getLogger(Information.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;

    public Information(WebDriver driver) {
        this.driver = driver;
        commonAction =  new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + "/affiliate";
        commonAction.navigateToURL(url);
        logger.info("Navigate to url: "+url);
    }
    public boolean hasViewDropshipInfo(){
        return allPermissions.getAffiliate().getDropshipInformation().isViewInformation();
    }
    public boolean hasViewResellerInfo(){
        return allPermissions.getAffiliate().getResellerInformation().isViewInformation();
    }
    public Information clickOnSecondTab(){
        commonAction.click(loc_tab_dropshipReseller,2);
        logger.info("Click on Reseller tab.");
        return this;
    }
    public void checkViewDropshipInfoPer() {
        String url = Links.DOMAIN + "/affiliate";
        if (hasViewDropshipInfo()){
            navigateByUrl();
            String currentTab = commonAction.getText(loc_tab_dropshipReseller, 0);
            try {
                assertCustomize.assertEquals(currentTab, PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.dropshipTab"),
                        "Dropship tab should be shown, but '%s' is shown".formatted(currentTab));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertCustomize.assertTrue(commonAction.getElements(loc_lst_lblNumber).size()>0,"All dropship numberic should be shown");
        }
        else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),"Restricted page should be shown when navigate to dropship info.");
        logger.info("Verified View dropship information.");
    }
    public void checkViewResellerInfoPer(){
        String url = Links.DOMAIN + "/affiliate";
        if (hasViewResellerInfo()){
            navigateByUrl();
            String currentTab = commonAction.getText(loc_tab_dropshipReseller, 0);
            try {
                assertCustomize.assertEquals(currentTab, PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.resellerTab"),
                        "Reseller tab should be shown, but '%s' is shown".formatted(currentTab));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertCustomize.assertTrue(commonAction.getElements(loc_lst_lblNumber).size()>0,"All reseller numberic should be shown");
        }
        else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),"Restricted page should be shown when navigate to reseller info.");
        logger.info("Verified View reseller information.");
    }
    public Information completeVerifyViewInformation() {
        logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
        if (assertCustomize.getCountFalse() > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
        }
        return this;
    }
    public void verifyViewDropshipInfo(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        checkViewDropshipInfoPer();
        completeVerifyViewInformation();
    }
    public void verifyViewResellerInfo(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        checkViewResellerInfoPer();
        completeVerifyViewInformation();
    }
}
