package web.Dashboard.sales_channels.lazada.synchronization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;

public class LazadaSynchronizationPage extends LazadaSynchronizationElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LazadaSynchronizationPage.class);
    public LazadaSynchronizationPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }
}
