package web.Dashboard.marketing.affiliate.partnerinventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.commons.UICommonAction;
import utilities.links.Links;

import java.util.List;

public class TrackingStockPage extends TrackingStockElement{
    final static Logger logger = LogManager.getLogger(TrackingStockPage.class);
    WebDriver driver;
    UICommonAction common;

    public TrackingStockPage(WebDriver driver) {
        this.driver = driver;
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + Links.AFFILIATE_TRACKING_STOCK_PATH;
        common.navigateToURL(url);
        logger.info("Navigate to url: "+url);
    }
    public boolean isProductInventoryListShow(){
        List<WebElement> productList = common.getElements(loc_lst_productName,2);
        return productList.size() >0;
    }
}
