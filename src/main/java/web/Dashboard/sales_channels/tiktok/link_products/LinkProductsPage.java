package web.Dashboard.sales_channels.tiktok.link_products;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

import java.time.Duration;

public class LinkProductsPage extends LinkProductsElement {
	final static Logger logger = LogManager.getLogger(LinkProductsPage.class);
    WebDriver driver;
    UICommonAction commonAction;

    public LinkProductsPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
}
