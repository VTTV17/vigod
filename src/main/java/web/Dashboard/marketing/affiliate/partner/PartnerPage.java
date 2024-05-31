package web.Dashboard.marketing.affiliate.partner;

import api.Seller.affiliate.partner.APIPartnerManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import utilities.utils.PropertiesUtil;
import web.Dashboard.exporthistory.ExportHistoryPage;
import web.Dashboard.home.HomePage;
import web.Dashboard.marketing.affiliate.general.AffiliateGeneral;

import java.util.List;

public class PartnerPage extends PartnerElement{
    final static Logger logger = LogManager.getLogger(PartnerPage.class);
    UICommonAction common;
    WebDriver driver;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    CreateEditPartnerPage createEditPartnerPage;
    LoginInformation loginInformation;
    AffiliateGeneral affiliateGeneral;
    public PartnerPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        createEditPartnerPage = new CreateEditPartnerPage(driver);
        affiliateGeneral = new AffiliateGeneral(driver);
    }
    public PartnerPage getLoginInfo(LoginInformation loginInformation){
        this.loginInformation = loginInformation;
        return this;
    }
    public boolean hasViewDropshipPartnerList(){
        return allPermissions.getAffiliate().getDropshipPartner().isViewDropshipPartnerList();
    }
    public boolean hasViewDropshipPartnerDetail(){
        return allPermissions.getAffiliate().getDropshipPartner().isViewDropshipPartnerDetail();
    }
    public boolean hasAddDropshipPartner(){
        return allPermissions.getAffiliate().getDropshipPartner().isAddDropshipPartner();
    }
    public boolean hasEditDropshipPartner(){
        return allPermissions.getAffiliate().getDropshipPartner().isEditDropshipPartner();
    }
    public boolean hasExportPartner(){
        return allPermissions.getAffiliate().getDropshipPartner().isExportPartner();
    }
    public boolean hasDownloadExportedFile(){
        return allPermissions.getAffiliate().getDropshipPartner().isDownloadExportedFile();
    }
    public boolean hasViewResellerPartnerList(){
        return allPermissions.getAffiliate().getResellerPartner().isViewResellerPartnerList();
    }
    public boolean hasViewResellerPartnerDetail(){
        return allPermissions.getAffiliate().getResellerPartner().isViewResellerPartnerDetail();
    }
    public boolean hasAddResellerPartner(){
        return allPermissions.getAffiliate().getResellerPartner().isAddResellerPartner();
    }
    public boolean hasEditResellerPartner(){
        return allPermissions.getAffiliate().getResellerPartner().isEditResellerPartner();
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + "/affiliate/partner";
        common.navigateToURL(url);
        common.sleepInMiliSecond(500);
        logger.info("Navigate to url: "+url);
    }
    public PartnerPage clickOnAddPartner(){
        common.click(loc_btnAddPartner);
        logger.info("Click on add partner button.");
        return this;
    }
    public PartnerPage clickOnExportBtn(){
        common.click(loc_btnExport);
        logger.info("Click on Export button.");
        return this;
    }
    public PartnerPage clickExportPartner(){
        common.click(loc_lst_btnExportOption,0);
        logger.info("Click on export partner.");
        return this;
    }
    public ExportHistoryPage clickExportHistory(){
        common.click(loc_lst_btnExportOption,1);
        logger.info("Click on Export history.");
        return new ExportHistoryPage(driver);
    }
    public void verifyViewPartnerListPers(boolean isDropship){
        navigateByUrl();
        affiliateGeneral.selectAffiliateTab(isDropship);
        List<WebElement> partnerList = common.getElements(loc_lstName,3);
        boolean viewPartnerPermission = isDropship? hasViewDropshipPartnerList() : hasViewResellerPartnerList();
        if(viewPartnerPermission){
            assertCustomize.assertFalse(partnerList.isEmpty(),"[Failed] %s Partner list should be show data".formatted(isDropship?"Dropship":"Reseller"));
        }else
            assertCustomize.assertTrue(partnerList.isEmpty(),"[Failed] %s Partner list should be empty, but It show %s partner.".formatted(isDropship?"Dropship":"Reseller",partnerList.size()));
        logger.info("Verified View %s partner list permission.".formatted(isDropship?"Dropship":"Reseller"));
    }
    public void verifyViewPartnerDetailPers(int id, boolean isDropship){
        String partnerDetailUrl = Links.DOMAIN + "/affiliate/partner/edit/"+ id;
        boolean viewPartnerDetailPermission = isDropship? hasViewDropshipPartnerDetail():hasViewResellerPartnerDetail();
        if(viewPartnerDetailPermission){
            assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(partnerDetailUrl,createEditPartnerPage.loc_txtName),
                    "[Failed] %s partner detail should be shown".formatted(isDropship?"Dropship":"Reseller"));
        }else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(partnerDetailUrl),
                    "[Failed] Restricted page should be shown when navigate to %s partner detail url: %s".formatted(isDropship?"Dropship":"Reseller",partnerDetailUrl));
        logger.info("Verified View %s partner detail permission.".formatted(isDropship?"Dropship":"Reseller"));
    }
    public void callRejectPartnerIfExceedNumber(boolean isDropship){
        boolean isExceed = new APIPartnerManagement(loginInformation).isExceedPartner(isDropship);
        if(isExceed) new APIPartnerManagement(loginInformation).rejectAddPartner(isDropship);
    }
    public void verifyAddPartnerPers(boolean isDropship){
        navigateByUrl();
        clickOnAddPartner();
        if (isDropship) {
            createEditPartnerPage.createSimpleDropshipVN(false);
        } else {
            createEditPartnerPage.createSimpleResellerVN(false);
        }
        boolean addPartnerPermission = isDropship? hasAddDropshipPartner(): hasAddResellerPartner();
        if(addPartnerPermission){
            callRejectPartnerIfExceedNumber(isDropship);
            createEditPartnerPage.clickOnSaveBtn();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.partner.create.successMessage"),
                        "[Failed] Create %s partner success message should be shown, but '%s' is shown.".formatted(isDropship?"Dropship":"Reseller",toastMessage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createEditPartnerPage.loc_btnSave),
                    "[Failed] Restricted page should be shown when click on Add %s partner button.".formatted(isDropship?"Dropship":"Reseller"));
        logger.info("Verified add %s partner permission.".formatted(isDropship?"Dropship":"Reseller"));
    }
    public void verifyEditPartnerPers(int id, boolean isDropship){
        if(hasViewDropshipPartnerDetail()){
            String partnerDetailUrl = Links.DOMAIN + "/affiliate/partner/edit/"+ id;
            common.navigateToURL(partnerDetailUrl);
            boolean editPartnerPermission = isDropship? hasEditDropshipPartner(): hasEditResellerPartner();
            if(editPartnerPermission){
                createEditPartnerPage.clickOnSaveBtn();
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.partner.update.successMessage"),
                            "[Failed] Update %s partner success message should be shown, but '%s' is shown.".formatted(isDropship?"Dropship":"Reseller",toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createEditPartnerPage.loc_btnSave),
                        "[Failed] Restricted popup should be shown when click save to edit %s partner.".formatted(isDropship?"Dropship":"Reseller"));
            logger.info("Verified Edit %s partner detail permission.".formatted(isDropship?"Dropship":"Reseller"));
        }else logger.info("Don't have View partner detail permission, so can't check edit partner.");
    }
    public void verifyExportPartner(){
        navigateByUrl();
        clickOnExportBtn();
        if(hasExportPartner()){
            clickExportPartner();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.partner.export.successMessage"),
                        "[Failed] Export partner success message should be shown, but '%s' is shown".formatted(toastMessage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_btnExportOption,0),
                    "[Failed] Restricted popup should be shown when click export partner.");
        logger.info("Verified Export partner permission.");
    }
    public void verifyDownloadExportedFile(){
        navigateByUrl();
        clickOnExportBtn();
        ExportHistoryPage exportHistoryPage = clickExportHistory();
        List<WebElement> exportDropshipPartnerFiles = common.getElements(exportHistoryPage.loc_lst_iconDownloadDropshipPartner,3);
        if (exportDropshipPartnerFiles.size()>0) {
            if (hasDownloadExportedFile()) {
                //Delete old file.
                new FileUtils().deleteFileInDownloadFolder("EXPORT_DROPSHIP_PARTNER");
                //Download new file
                exportHistoryPage.clickOnDownloadDropshipPartner();
                common.sleepInMiliSecond(3000, "Waiting for download.");
                assertCustomize.assertTrue(new FileUtils().isDownloadSuccessful("EXPORT_DROPSHIP_PARTNER"), "[Failed] Not found file in download folder.");
            } else {
                logger.info("Click on download icon");
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(exportHistoryPage.loc_lst_iconDownloadDropshipPartner, 0),
                        "[Failed] Restricted page should be shown when click on download icon.");
            }
            logger.info("Verified Download exported file.");
        }else logger.info("No data to download export dropship partner filed");
    }

    /**
     *
     * @param allPermissions
     * @param partnerId dropship or reseller id
     * @param isDropship true: if check dropship partner, false: if check reseller partner
     */
    public void verifyPartnerPermission(AllPermissions allPermissions,int partnerId, boolean isDropship){
        this.allPermissions = allPermissions;
        verifyViewPartnerListPers(isDropship);
        verifyViewPartnerDetailPers(partnerId,isDropship);
        verifyAddPartnerPers(isDropship);
        verifyEditPartnerPers(partnerId,isDropship);
        if(isDropship) {
            verifyExportPartner();
            verifyDownloadExportedFile();
        }
        AssertCustomize.verifyTest();
    }
}
