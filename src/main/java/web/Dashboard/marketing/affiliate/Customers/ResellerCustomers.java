package web.Dashboard.marketing.affiliate.Customers;

import api.Seller.affiliate.customers.APIResellerCustomers;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import web.Dashboard.marketing.affiliate.information.Information;

import java.util.List;

public class ResellerCustomers extends ResellerCustomersElement{
    final static Logger logger = LogManager.getLogger(ResellerCustomers.class);
    WebDriver driver;
    UICommonAction commons;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    public ResellerCustomers(WebDriver driver, LoginInformation loginInformation ){
        this.driver = driver;
        this.loginInformation = loginInformation;
        commons =  new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + Links.AFFILIATE_CUSTOMER_PATH;
        commons.navigateToURL(url);
        logger.info("Navigate to url: "+url);
        commons.sleepInMiliSecond(500);
    }

    public void clickExportBtn(){
        commons.click(loc_btnExport);
        logger.info("Click on Export button.");
    }
    public void clickOnExportCustomer(){
        commons.click(loc_lst_btnExportOptions,0);
        logger.info("Click on Export customer");
    }
    public void clickOnExportHistory(){
        commons.click(loc_lst_btnExportOptions,1);
        logger.info("Click on Export history");
    }
    public void selectAResellerOnSelectResellerModal(int index){
        commons.click(loc_dlgSelectReseller_lst_chkResellerName,index);
        logger.info("Click to select reseller, index = "+index);
    }
    public void clickOnExportBtnOnResellerModal(){
        commons.click(loc_dlgSelectReseller_btnExport);
        logger.info("Click on Export button on Reseller modal.");
    }
    /*---------------------Permission--------------------*/
    public boolean hasDownloadCustomer(){
        return allPermissions.getAffiliate().getResellerCustomer().isDownloadCustomer();
    }
    public boolean hasExportCustomer(){
        return allPermissions.getAffiliate().getResellerCustomer().isExportCustomer();
    }
    public boolean hasResellerCustomer(){
        boolean[] resellerCustomerPermisison = {
                hasExportCustomer(),
                hasDownloadCustomer(),
        };
        for(boolean permission : resellerCustomerPermisison) if (permission) return true;
        return false;
    }
    public void checkExportResellerCustomerPermission(){
        navigateByUrl();
        clickExportBtn();
        clickOnExportCustomer();
        selectAResellerOnSelectResellerModal(1);
        if (hasExportCustomer()){
            clickOnExportBtnOnResellerModal();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.customers.export.successMessage"),
                        "Export reseller customer success message should be shown, but '%s' is shown".formatted(toastMessage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_dlgSelectReseller_btnExport),
                    "Restricted popup should be shown when click on Export button on Select Reseller modal.");
            Response responseExport = new APIResellerCustomers(loginInformation).exportResellerCustomer();
            //Call API check 403
            assertCustomize.assertTrue(responseExport.statusCode()==403,"[Failed] Call API export customer should be response 403, but it response '%s'".formatted(responseExport.statusCode()));
        }
        logger.info("Verified Export reseller permission.");
    }
    public void checkDownloadCustomerPermission(){
        navigateByUrl();
        clickExportBtn();
        clickOnExportHistory();
        ExportHistoryPage exportHistoryPage = new ExportHistoryPage(driver);
        List<WebElement> exportCustomerFiles = commons.getElements(exportHistoryPage.loc_lst_iconDownloadResellerCustomer,3);
        if (exportCustomerFiles.size()>0) {
            if (hasDownloadCustomer()) {
                //Delete old file.
                new FileUtils().deleteFileInDownloadFolder("EXPORT_RESELLER_CUSTOMER");
                //Download new file
                exportHistoryPage.clickOnDownloadResellerCustomer();
                commons.sleepInMiliSecond(3000, "Waiting for download.");
                assertCustomize.assertTrue(new FileUtils().isDownloadSuccessful("EXPORT_RESELLER_CUSTOMER"), "[Failed] Not found file EXPORT_RESELLER_CUSTOMER in download folder.");
            } else {
                logger.info("Click on download EXPORT_RESELLER_CUSTOMER icon");
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(exportHistoryPage.loc_lst_iconDownloadResellerCustomer, 0),
                        "[Failed] Restricted page should be shown when click on download EXPORT_RESELLER_CUSTOMER icon.");
            }
            logger.info("Verified Download exported file.");
        }else logger.info("No data to download export reseller customer filed");
        logger.info("Verified Download customer permission.");
    }
    public ResellerCustomers checkResellerCustomerPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        if(hasResellerCustomer()) {
            checkDownloadCustomerPermission();
            checkExportResellerCustomerPermission();
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(Links.DOMAIN + Links.AFFILIATE_CUSTOMER_PATH),
                "Restricted page should be shown when navigate to Reseller customer url");
        AssertCustomize.verifyTest();
        return this;
    }
}
