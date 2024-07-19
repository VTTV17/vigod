package web.Dashboard.gowallet.transactionhistory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.enums.PaymentMethod;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.gowallet.transactionhistory.topup.TopupPage;
import web.Dashboard.marketing.affiliate.Customers.ResellerCustomers;

import java.util.List;

public class TransactionHistoryPage extends TransactionHistoryElement{
    final static Logger logger = LogManager.getLogger(TransactionHistoryPage.class);
    WebDriver driver;
    UICommonAction commons;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    public TransactionHistoryPage(WebDriver driver){
        this.driver = driver;
        commons =  new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + Links.GO_WALLET_PATH;
        commons.navigateToURL(url);
        logger.info("Navigate to url: "+url);
        commons.sleepInMiliSecond(500);
    }
    /*----------------------------Staff permission-----------------------*/
    public boolean hasViewGoWallet(){
        return allPermissions.getGoWallet().isViewGoWallet();
    }
    public boolean hasTopupGoWallet(){
        return allPermissions.getGoWallet().isTopUpGoWallet();
    }
    public boolean hasViewBanlance(){
        return allPermissions.getGoWallet().isViewBalance();
    }
    public void checkViewGoWallet(){
        if(hasViewGoWallet()){
            navigateByUrl();
            assertCustomize.assertTrue(commons.isElementDisplay(loc_ttlTransactionHistory),
                    "[Failed] Transaction history title should be shown." );
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(Links.DOMAIN + Links.GO_WALLET_PATH),
                "[Failed] Restricted page should be shown when naigate to Transaction history url");
        logger.info("Verified View GoWallet perrmission.");
    }
    public void checkTopupGoWallet(){
        if(hasViewGoWallet()){
            List<WebElement> topupBtnElements = commons.getElements(loc_lst_btnTopup,1);
            for (int i = 0; i<topupBtnElements.size();i++){
                navigateByUrl();
                if(hasTopupGoWallet()){
                    int currentSize = commons.getElements(loc_lstTransactionId,1).size();
                    commons.click(loc_lst_btnTopup,i);
                    commons.sleepInMiliSecond(2000);
                    new TopupPage(driver).makeATopup(PaymentMethod.ATM);
                    navigateByUrl();
                    assertCustomize.assertEquals(commons.getElements(loc_lstTransactionId,1).size(),currentSize+1,
                            "[Failed] New transaction should be shown in list");
                }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_btnTopup,i),
                        "[Failed] Restricted popup should be shown when click on Topup button "+i);
            }
        }else logger.info("Don't have View GoWallet permission, so no need check Topup GoWallet permission.");
        logger.info("Verified Topup GoWallet");
    }
    public void checkViewBalance(){
        if(hasViewGoWallet()){
            navigateByUrl();
            List<WebElement> eyeElements = commons.getElements(loc_lst_icnEye,1);
            for (int i = 0; i<eyeElements.size();i++){
                if(hasViewBanlance()){
                    commons.click(loc_lst_icnEye,i);
                    assertCustomize.assertFalse(commons.getText(loc_balance,i).contains("*"),
                            "[Failed] Balance not show when click on eye icon "+i);
                }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEye,i),
                        "Restricted popup should be shown when click on eye icon "+i);
            }
        }else logger.info("Don't have View GoWallet permission, so no need check View Balance  permission.");
    }
    public TransactionHistoryPage verifyGoWalletPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        checkViewGoWallet();
        checkTopupGoWallet();
        checkViewBalance();
        AssertCustomize.verifyTest();
        return this;
    }
}
