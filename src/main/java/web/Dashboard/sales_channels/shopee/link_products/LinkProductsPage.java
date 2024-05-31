package web.Dashboard.sales_channels.shopee.link_products;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class LinkProductsPage extends LinkProductsElement {

	final static Logger logger = LogManager.getLogger(LinkProductsPage.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public LinkProductsPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    
	public LinkProductsPage inputSearchTerm(String searchTerm) {
		commonAction.inputText(loc_txtSearchBox, searchTerm);
        logger.info("Input '{}' into Search box.", searchTerm);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
    
}
