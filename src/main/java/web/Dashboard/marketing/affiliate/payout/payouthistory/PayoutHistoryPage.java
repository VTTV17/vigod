package web.Dashboard.marketing.affiliate.payout.payouthistory;

import com.github.dockerjava.api.model.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.marketing.affiliate.Customers.ResellerCustomers;
import web.Dashboard.marketing.affiliate.general.AffiliateGeneral;

import java.util.List;

public class PayoutHistoryPage extends PayoutHistoryElement{
    final static Logger logger = LogManager.getLogger(PayoutHistoryPage.class);
    WebDriver driver;
    UICommonAction commons;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    public PayoutHistoryPage(WebDriver driver, LoginInformation loginInformation ){
        this.driver = driver;
        this.loginInformation = loginInformation;
        commons =  new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + Links.AFFILIATE_PAYOUT_HISTORY_PATH;
        commons.navigateToURL(url);
        logger.info("Navigate to url: "+ url);
        commons.sleepInMiliSecond(500);
    }
    public boolean isPayoutHistoryShow(){
        List<WebElement> payoutHistoryList = commons.getListElement(loc_lstPayoutRecord,2);
        return payoutHistoryList.size()>0;
    }
    /*------------------------Permission------------------*/
    public PayoutHistoryPage checkViewPayoutHistoryPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        navigateByUrl();
        if(allPermissions.getAffiliate().getPayoutHistory().isViewPayoutHistory()){
            assertCustomize.assertTrue(isPayoutHistoryShow(),"Payout history record should be shown");
        }else assertCustomize.assertTrue(new AffiliateGeneral(driver).isRestrictedComponentShow(),
                "Restricted component should be shown when navigate to Payout History url");
        AssertCustomize.verifyTest();
        return this;
    }
}
