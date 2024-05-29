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
import web.Dashboard.marketing.affiliate.general.AffiliateGeneral;
import web.Dashboard.marketing.affiliate.order.PartnerOrdersPage;
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
    AffiliateGeneral affiliateGeneral;

    public Information(WebDriver driver) {
        this.driver = driver;
        commonAction =  new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        affiliateGeneral = new AffiliateGeneral(driver);
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
    public void checkViewDropshipInfoPer() {
        String url = Links.DOMAIN + "/affiliate";
        if (hasViewDropshipInfo()){
            navigateByUrl();
            String currentTab = commonAction.getText(affiliateGeneral.loc_tab_dropshipReseller, 0);
            try {
                assertCustomize.assertEquals(currentTab, PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.dropshipTab"),
                        "[Failed] Dropship tab should be shown, but '%s' is shown".formatted(currentTab));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertCustomize.assertTrue(commonAction.getElements(loc_lst_lblNumber).size()>0,"All dropship numberic should be shown");
        }
        else {
            if(hasViewResellerInfo()){
                String currentTab = commonAction.getText(affiliateGeneral.loc_tab_dropshipReseller, 0);
                try {
                    assertCustomize.assertEquals(currentTab, PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.resellerTab"),
                            "[Failed] Reseller tab should be shown, but '%s' is shown".formatted(currentTab));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url), "[Failed] Restricted page should be shown when navigate to dropship info.");

        }
        logger.info("Verified View dropship information.");
    }
    public void checkViewResellerInfoPer(){
        String url = Links.DOMAIN + "/affiliate";
        if (hasViewResellerInfo()){
            navigateByUrl();
            affiliateGeneral.selectAffiliateTab(false);
            String currentTab = commonAction.getText(affiliateGeneral.loc_tabAffiliateActive);
            try {
                assertCustomize.assertEquals(currentTab, PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.resellerTab"),
                        "[Failed] Reseller tab should be shown, but '%s' is shown".formatted(currentTab));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertCustomize.assertTrue(commonAction.getElements(loc_lst_lblNumber).size()>0,"[Failed] All reseller numberic should be shown");
        }
        else {
            if(hasViewDropshipInfo()){
                navigateByUrl();
                affiliateGeneral.selectAffiliateTab(false);
                String currentTab = commonAction.getText(affiliateGeneral.loc_tabAffiliateActive);
                try {
                    assertCustomize.assertEquals(currentTab, PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.dropshipTab"),
                            "[Failed] Reseller tab should be shown, but '%s' is shown".formatted(currentTab));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url), "[Failed] Restricted page should be shown when navigate to reseller info.");
        }
            logger.info("Verified View reseller information.");
    }

    public void verifyViewDropshipInfo(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        checkViewDropshipInfoPer();
        AssertCustomize.verifyTest();
    }
    public void verifyViewResellerInfo(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        checkViewResellerInfoPer();
        AssertCustomize.verifyTest();
    }
}
