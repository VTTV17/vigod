package web.Dashboard.sales_channels.lazada.account_information;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;

public class AccountInformationPage extends AccountInformationElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(AccountInformationPage.class);
    public AccountInformationPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }
}
