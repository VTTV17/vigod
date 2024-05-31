package web.Dashboard.sales_channels.tiktok.account_information;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAction;

import java.time.Duration;

public class AccountInformationPage extends AccountInformationElement {

	final static Logger logger = LogManager.getLogger(AccountInformationPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public AccountInformationPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    
    public void clickDownloadShopeeProduct() {
    	commonAction.click(loc_btnDownloadShopeeProduct);
    	logger.info("Clicked on 'Download Shopee Product' button.");
    }
}
