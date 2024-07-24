package web.Dashboard.marketing.affiliate.payout.payoutinformation;

import api.Seller.affiliate.payoutinformation.APIPayoutInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.links.Links;
import utilities.model.dashboard.marketing.affiliate.PayoutByProductInfo;
import utilities.model.dashboard.marketing.affiliate.PayoutByRevenueInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.marketing.affiliate.general.AffiliateGeneral;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utilities.links.Links.AFFILIATE_PAYOUT_INFORMATION_PATH;

public class PayoutInformationPage extends PayoutInfomationElement {
    final static Logger logger = LogManager.getLogger(PayoutInformationPage.class);
    WebDriver driver;
    UICommonAction common;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    AffiliateGeneral affiliateGeneral;
    LoginInformation loginInformation;
    public PayoutInformationPage(WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        affiliateGeneral = new AffiliateGeneral(driver);
    }
    public PayoutInformationPage getLoginInfo(LoginInformation shopOwnerLoginInfor){
        this.loginInformation = shopOwnerLoginInfor;
        return this;
    }
    public boolean hasViewDropshipPayoutSummary() {
        return allPermissions.getAffiliate().getDropshipPayout().isViewPayoutSummary();
    }

    public boolean hasExportDropshipPayout() {
        return allPermissions.getAffiliate().getDropshipPayout().isExportPayout();
    }

    public boolean hasImportDropshipPayout() {
        return allPermissions.getAffiliate().getDropshipPayout().isImportPayout();
    }

    public boolean hasViewResellerPayoutSummary() {
        return allPermissions.getAffiliate().getResellerPayout().isViewPayoutSummary();
    }

    public boolean hasExportResellerPayout() {
        return allPermissions.getAffiliate().getResellerPayout().isExportPayout();
    }

    public boolean hasImportResellerPayout() {
        return allPermissions.getAffiliate().getResellerPayout().isImportPayout();
    }

    public boolean isNotInformationShowData() {
        waitNumbericLoaded();
        List<WebElement> numberic = common.getElements(loc_lst_lblPayoutInfoAmount);
        for (int i = 0; i < numberic.size(); i++) {
            Matcher m = Pattern.compile("\\d+").matcher(common.getText(loc_lst_lblPayoutInfoAmount, i));
            ArrayList<String> sub = new ArrayList<String>();
            while (m.find()) {
                sub.add(m.group());
            }
            if (!sub.get(0).equals("0")) {
                return false;
            }
        }
        return true;
    }

    public PayoutInformationPage navigateByUrl() {
        String url = Links.DOMAIN + AFFILIATE_PAYOUT_INFORMATION_PATH;
        common.navigateToURL(url);
        logger.info("Navigate to affiliate payout information page: " + url);
        common.sleepInMiliSecond(500);
        return this;
    }
    public void clickExportBtn(){
        common.click(loc_btnExport);
        logger.info("Click on Export button");
    }
    public void clickImportPayment(){
        common.click(loc_btnImport);
        logger.info("Click on import payment.");
    }
    public void importPayoutFile(String fileName){
        common.sleepInMiliSecond(200);
        String filePath = new DataGenerator().getPathOfFileInResourcesRoot(fileName);
        common.uploads(loc_txtUploadFile,filePath);
        logger.info("Upload payout file: "+fileName);
    }
    public void clickImportBtnOnImportModal(){
        common.click(loc_dlgImport_btnImport);
        logger.info("Click import button on import modal.");
    }
    public void waitNumbericLoaded(){
        WebDriverWait expliciWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        expliciWait.until(new Function<WebDriver, Boolean>() {
            List<String> listNumbericBefore = common.getListText(loc_lst_lblPayoutInfoAmount);
            @Override
            public Boolean apply(WebDriver driver) {
                List<String> listNumbericAfter =common.getListText(loc_lst_lblPayoutInfoAmount);
                if(listNumbericAfter.equals(listNumbericBefore)){
                    return true;
                }else {
                    listNumbericBefore = new ArrayList<>();
                    listNumbericBefore.addAll(listNumbericAfter);
                }
                return false;
            }
        });
        new WebDriverWait(driver, Duration.ofSeconds(0));
    }
    public String generatePayoutInfoFileForImportByProduct(boolean isDropship){
        Excel excel = new Excel();
        String fileName = isDropship? FileNameAndPath.FILE_IMPORT_PAYOUT_PRODUCT: FileNameAndPath.FILE_IMPORT_PAYOUT_RESELLER;
        PayoutByProductInfo payoutByProductInfos = new APIPayoutInformation(loginInformation).getAPayoutHasPayableAmount_CommissionByProduct(isDropship);
        if(payoutByProductInfos != null) {
            excel.writeCellValue(fileName, 0, 6, 1, payoutByProductInfos.getPartnerCode());
            excel.writeCellValue(fileName, 0, 6, 3, String.join("",payoutByProductInfos.getPartnerType().split("_")));
            excel.writeCellValue(fileName, 0, 6, 8, String.valueOf(payoutByProductInfos.getPayableAmount()));
            if (payoutByProductInfos.getPayableAmount() > 1000)
                excel.writeCellValue(fileName, 0, 6, 9, "1000");
            else excel.writeCellValue(fileName, 0, 6, 9, String.valueOf(payoutByProductInfos.getPayableAmount()));
        }else logger.info("Commission by Product - Don't have Payout info to generate.");
        return fileName;
    }
    public String generatePayoutInfoFileForImportByRevenue(){
        Excel excel = new Excel();
        String term = new DataGenerator().generatePreviousTerm("MM/yyyy");
        String fileName = FileNameAndPath.FILE_IMPORT_PAYOUT_REVENUE;
        PayoutByRevenueInfo payoutByRevenueInfo = new APIPayoutInformation(loginInformation).getAPayoutHasPayableAmount_CommissionByRevenue();
        excel.writeCellValue(fileName,0,0,1,term);
        if(payoutByRevenueInfo!=null) {
            excel.writeCellValue(fileName, 0, 2, 1, payoutByRevenueInfo.getPartnerCode());
            excel.writeCellValue(fileName, 0, 2, 17, String.valueOf(payoutByRevenueInfo.getPayableAmount()));
            if (payoutByRevenueInfo.getPayableAmount() > 1000)
                excel.writeCellValue(fileName, 0, 2, 18, "1000");
            else excel.writeCellValue(fileName, 0, 2, 18, String.valueOf(payoutByRevenueInfo.getPayableAmount()));
        }else logger.info("Commission by Revenue - Don't have payout revenue to generate.");
        return fileName;
    }
    public void verifyViewPayoutSummary(boolean isDropship) {
        boolean hasViewPayoutInfoPermission = isDropship? hasViewDropshipPayoutSummary(): hasViewResellerPayoutSummary();
        navigateByUrl();
        affiliateGeneral.selectAffiliateTab(isDropship);
        String currentTab = common.getText(affiliateGeneral.loc_tabAffiliateActive);
        String expectedTab = "";
        try {
            expectedTab = isDropship?PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.dropshipTab")
                    :PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.resellerTab");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //check active tab
        assertCustomize.assertEquals(currentTab, expectedTab, "[Failed] '%s' tab is shown, but '%s' is shown".formatted(expectedTab, currentTab));
        if (hasViewPayoutInfoPermission) {
            //on Commission by products tab
            //check summary data show
            assertCustomize.assertTrue(!isNotInformationShowData(), "[Failed] Payout summary should be shown on commission by product.");
            //check table data show
            List<WebElement> partnerCodeList_commissionByProduct = common.getElements(loc_lst_lblParnetCode, 3);
            assertCustomize.assertTrue(partnerCodeList_commissionByProduct.size() > 0,
                    "[Failed] Payout table data on Commision by product tab should be shown.");
            if(isDropship) {
                //on Commission by revenue tab
                affiliateGeneral.selectTabCommissionByRevenue();
                List<WebElement> partnerCodeList_commissionByRevenue = common.getElements(loc_lst_lblParnetCode, 3);
                //check summary data show
                assertCustomize.assertTrue(!isNotInformationShowData(), "[Failed] Payout summary should be shown on Commission by revenue.");
                //check table data
                assertCustomize.assertTrue(partnerCodeList_commissionByRevenue.size() > 0,
                        "[Failed] Payout table data on Commision by revenue tab should be shown.");
            }
        } else {
            //check summary data show
            assertCustomize.assertTrue(isNotInformationShowData(), "[Failed] Payout summary should not be shown on commission by product.");
            //check table data show
            List<WebElement> partnerCodeList_commissionByProduct = common.getElements(loc_lst_lblParnetCode, 3);
            assertCustomize.assertTrue(partnerCodeList_commissionByProduct.isEmpty(),
                    "[Failed] Payout table data on Commision by product tab should not be shown.");
            if(isDropship) {
                //on Commission by revenue tab
                affiliateGeneral.selectTabCommissionByRevenue();
                List<WebElement> partnerCodeList_commissionByRevenue = common.getElements(loc_lst_lblParnetCode, 3);
                //check summary data show
                assertCustomize.assertTrue(isNotInformationShowData(), "[Failed] Payout summary should not be shown on Commission by revenue.");
                //check table data
                assertCustomize.assertTrue(partnerCodeList_commissionByRevenue.isEmpty(),
                        "[Failed] Payout table data on Commision by revenue tab should not be shown.");
            }
        }
        logger.info("Verified View payout summary with isDropship = "+isDropship);
    }
    public boolean isDownloadExportPayoutSucess(){
        //Delete old file.
        new FileUtils().deleteFileInDownloadFolder("payout-export");

        clickExportBtn();

        //Download new file
        common.sleepInMiliSecond(3000, "Waiting for download.");
        return new FileUtils().isDownloadSuccessful("payout-export");
    }
    public void verifyExportPayout(boolean isDropshipTab){
        boolean hasExportPayoutPermission = isDropshipTab? hasExportDropshipPayout(): hasExportResellerPayout();
        navigateByUrl();
        affiliateGeneral.selectAffiliateTab(isDropshipTab);
        if(hasExportPayoutPermission){
            assertCustomize.assertTrue(isDownloadExportPayoutSucess(),
                    "[Failed] Not found file  payout-export in download folder.");
            if(isDropshipTab){
                affiliateGeneral.selectTabCommissionByRevenue();
                assertCustomize.assertTrue(isDownloadExportPayoutSucess(),
                        "[Failed] Not found file commission by revenue payout-export in download folder.");
            }
        }else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnExport),
                    "Restricted popup should be shown when click export.");
            if(isDropshipTab){
                affiliateGeneral.selectTabCommissionByRevenue();
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnExport),
                        "Restricted popup should be shown when click export.");
            }
        }
        logger.info("Verified Export payout with isDropship = "+isDropshipTab);
    }
    public void verifyImportPayout(boolean isDropship){
        navigateByUrl();
        boolean hasImportPer = isDropship? hasImportDropshipPayout(): hasImportResellerPayout();
        new AffiliateGeneral(driver).selectAffiliateTab(isDropship);
        String fileName = generatePayoutInfoFileForImportByProduct(isDropship);
        clickImportPayment();
        if (hasImportPer){
            importPayoutFile(fileName);
            clickImportBtnOnImportModal();
            new HomePage(driver).waitTillLoadingDotsDisappear();
            assertCustomize.assertTrue(common.isElementNotDisplay(loc_dlgImport_btnImport,2)|| common.getElements(loc_dlgImport_lst_lblError,1).size()>0,
                    "[Failed] Import commission by product popup should be closed or shown error.");
        }else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_dlgImport_btnImport),
                    "[Failed]Commission by product - Restricted popup should be shown when click on Import button on Import modal.");
        }
        if (isDropship) {
            navigateByUrl();
            new AffiliateGeneral(driver).selectTabCommissionByRevenue();
            fileName = generatePayoutInfoFileForImportByRevenue();
            clickImportPayment();
            if (hasImportPer){
                importPayoutFile(fileName);
                clickImportBtnOnImportModal();
                new HomePage(driver).waitTillLoadingDotsDisappear();
                assertCustomize.assertTrue(common.isElementNotDisplay(loc_dlgImport_btnImport,2)||common.getElements(loc_dlgImport_lst_lblError,1).size()>0,
                        "[Failed] Commission by revenue - Import popup should be closed or error show.");
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_dlgImport_btnImport),
                        "[Failed] Commission by revenue - Restricted popup should be shown when click on Import button on Import modal.");
            }
        }
        logger.info("Verified import payout with isDropship = "+isDropship);
    }
    public PayoutInformationPage verifyPayoutPermission(AllPermissions allPermissions, boolean isDropship){
        this.allPermissions = allPermissions;
        verifyViewPayoutSummary(isDropship);
        verifyExportPayout(isDropship);
        verifyImportPayout(isDropship);
        AssertCustomize.verifyTest();
        return this;
    }
}
