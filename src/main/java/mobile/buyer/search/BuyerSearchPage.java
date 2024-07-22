package mobile.buyer.search;

import mobile.buyer.productDetail.ProductDetailScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;
import utilities.utils.PropertiesUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BuyerSearchPage extends UICommonMobile{
    final static Logger logger = LogManager.getLogger(BuyerSearchPage.class);

    WebDriver driver;
    WebDriverWait wait;
    BuyerSearchElement searchPage;


    public BuyerSearchPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        searchPage = new BuyerSearchElement(driver);
    }
    public BuyerSearchDetailPage tapOnSearchBar(){
        clickElement(searchPage.SEARCH_BAR);
        return new BuyerSearchDetailPage(driver);
    }
    public ProductDetailScreen searchItem(String itemName){
        tapOnSearchBar()
                .inputKeywordToSearch(itemName)
                .verifySearchSuggestion(itemName)
                .tapSearchSuggestion();
        return new ProductDetailScreen(driver);
    }
    public BuyerSearchPage tapOnMenuDropdown(){
        clickElement(searchPage.MENU_DROPDOWN);
        logger.info("Tap on Menu dropdown");
        return this;
    }
    public BuyerSearchPage goToCollectionDetailOnSearchTab(String collectionName){
        tapOnMenuDropdown();
        clickElement(wait.until(ExpectedConditions.visibilityOf(getElementByXpath(searchPage.MENU_ITEM_XPATH.formatted(collectionName)))));
        logger.info("Tap on menue item: "+collectionName);
        return this;
    }
    public BuyerSearchPage verifyProductsInCollection(List<String> expected){
        if(expected.size()==0){
            verifyNoProductMessage();
        }
        List<WebElement> productNameElements = getElements(searchPage.PRODUCT_LIST);
        List<String> productNamesActual =new ArrayList<>();
        List<String> productNamesActualToLowerCase =new ArrayList<>();
        if (productNameElements.size() !=0){
            productNamesActual = getListElementText(searchPage.PRODUCT_LIST);
            for (String productName:productNamesActual) {
                productNamesActualToLowerCase.add(productName.toLowerCase());
            }
        }
        Assert.assertEquals(productNamesActualToLowerCase,expected);
        logger.info("Verify products in collection.");
        return this;
    }
    public BuyerSearchPage verifyNoProductMessage(){
        try {
            Assert.assertEquals(getText(searchPage.NO_PRODUCT_MESSAGE), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.search.NoProductMessage"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Verify no product message.");
        return this;
    }
    public BuyerSearchPage verifyMenuItemNotShowInList(String item){
        tapOnMenuDropdown();
        Assert.assertFalse(isElementDisplayInList(searchPage.MENU_ITEM,item));
        logger.info("Verify menu item not show: "+item);
        return this;
    }
}
