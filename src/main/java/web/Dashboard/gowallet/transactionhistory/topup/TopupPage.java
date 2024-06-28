package web.Dashboard.gowallet.transactionhistory.topup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.PaymentMethod;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.Home.Home;
import utilities.thirdparty.ATM;
import utilities.thirdparty.VISA;
import web.Dashboard.gowallet.transactionhistory.TransactionHistoryPage;
import web.Dashboard.home.HomePage;

public class TopupPage extends TopupElement{
    final static Logger logger = LogManager.getLogger(TopupPage.class);
    WebDriver driver;
    UICommonAction commons;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    public TopupPage(WebDriver driver ){
        this.driver = driver;
        commons =  new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public void selectPaymentMethod(PaymentMethod paymentMethod){
        switch (paymentMethod){
            case VISA -> commons.click(loc_lst_onlinePayment,1);
            case PAYPAL -> commons.click(loc_lst_onlinePayment,2);
            default -> logger.info("Payment method is select as default");
        }
    }
    public void inputPaymentAmount(String amount){
        commons.inputText(loc_txt_paymentAmount,amount);
        logger.info("Input payment amount: "+amount);
    }
    public void clickOnProcessPaymentBtn(){
        commons.click(loc_btnProcessToPayment);
        logger.info("Click on Procee to payment button");
    }
    public void makeATopup(PaymentMethod paymentMethod){
        inputPaymentAmount("100000");
        selectPaymentMethod(paymentMethod);
        clickOnProcessPaymentBtn();
        commons.sleepInMiliSecond(2000);
        String currentWindowHandle = commons.getCurrentWindowHandle();
        int currentNumberOfWindows = commons.getAllWindowHandles().size();
        commons.switchToWindow(1);
        new HomePage(driver).waitTillSpinnerDisappear1();
        switch (paymentMethod){
            case ATM -> new ATM(driver).completePayment();
            case VISA -> new VISA(driver).completePayment();
            // add new case if you need
        }
        //Wait till the latest tab is closed
        for (int i=9; i>=0; i--) {
            if (commons.getAllWindowHandles().size() != currentNumberOfWindows) {
                break;
            }
            commons.sleepInMiliSecond(2000);
        }
        commons.switchToWindow(currentWindowHandle);
    }
}
