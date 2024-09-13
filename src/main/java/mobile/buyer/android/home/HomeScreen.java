package mobile.buyer.android.home;

import api.Seller.products.all_products.APIProductDetailV2;
import app.Buyer.collection.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAndroid;

import java.time.Duration;

public class HomeScreen extends HomeElement {
    final static Logger logger = LogManager.getLogger(HomeScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAndroid commonAndroid;

    public HomeScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAndroid = new UICommonAndroid(driver);
    }

    public HomeScreen waitHomepageLoaded() {
        commonAndroid.getElement(loc_icnHeaderSearch);
        return this;
    }

    public boolean searchAndNavigateToProductScreenByName(APIProductDetailV2.ProductInfoV2 productInfo, String language) {
        // click Search icon
        commonAndroid.click(loc_icnHeaderSearch);
        logger.info("Open search screen");

        // Get product name
        String keywords = productInfo.getMainProductNameMap().get(language.equals("VIE") ? "vi" : "en");

        // input search keywords
        commonAndroid.sendKeys(loc_txtHeaderSearchBox, keywords);
        logger.info("Search with keywords: {}", keywords);

        // Navigate to product detail
        if (!commonAndroid.getListElement(loc_lstSearchResult(keywords)).isEmpty()) {
            // Navigate
            commonAndroid.click(loc_lstSearchResult(keywords));

            // Response result
            return true;
        } else return false;
    }

    public HomeScreen clickOnMenuItemByText(String menuItemByText) {
        String menuItemNewXpath = MENU_ITEM_XPATH.formatted(menuItemByText.toUpperCase());
        commonAndroid.click(By.xpath(menuItemNewXpath));
        logger.info("Click on menu: {}", menuItemByText);
        return this;
    }

    public HomeScreen clickOnMenuIcon(){
        commonAndroid.click(loc_icnMenu);
        logger.info("Click on menu icon");
        return this;
    }
    public Collection goToCollectionByMenuText(String text){
        clickOnMenuIcon();
        if(!commonAndroid.isShown(loc_lstMenuItems)){
            clickOnMenuIcon();
        }
        clickOnMenuItemByText(text);
        return new Collection(driver);
    }
    public HomeScreen verifyMenuItemNotShow(String item){
        clickOnMenuIcon();
//        Assert.assertFalse(commonAndroid.isTextShown(item));
        logger.info("Verify %s not display in list".formatted(item));
        return this;
    }
}
