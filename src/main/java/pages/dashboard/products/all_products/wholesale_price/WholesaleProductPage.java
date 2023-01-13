package pages.dashboard.products.all_products.wholesale_price;

import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.setting.StoreInformation;
import api.storefront.login.LoginSF;
import api.storefront.signup.SignUp;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static api.dashboard.customers.Customers.segmentName;
import static java.lang.Thread.sleep;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static pages.dashboard.login.LoginPage.sellerAccount;
import static pages.dashboard.login.LoginPage.sellerPassword;
import static pages.dashboard.products.all_products.ProductPage.*;
import static utilities.links.Links.DOMAIN;

public class WholesaleProductPage extends WholesaleProductElement {
    String WHOLESALE_PRODUCT_PAGE_PATH = "/product/wholesale-price/create/%s";
    UICommonAction commonAction;
    WebDriverWait wait;
    Actions act;

    public static Map<String, List<Boolean>> uiWholesaleProductStatus;
    public static List<Integer> uiWholesaleProductPrice;
    public static List<Float> uiWholesaleProductRate;
    public static List<Integer> uiWholesaleProductStock;

    public WholesaleProductPage(WebDriver driver) {
        super(driver);
        commonAction = new UICommonAction(driver);
        act = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @SneakyThrows
    void createSegmentByAPI() {
        // login to dashboard
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        // get store information
        new StoreInformation().getStoreInformation();

        // sign up SF account
        new SignUp().signUpByPhoneNumber();

        // login SF to create new Customer in Dashboard
        new LoginSF().LoginByPhoneNumber();

        // wait customer is added
        commonAction.sleepInMiliSecond(3000);

        // add tag and create segment by tag name
        new Customers().addCustomerTag(SignUp.customerName).createSegment();
    }

    public WholesaleProductPage navigateToWholesaleProductPage() {
        // create segment for wholesale product config
        createSegmentByAPI();

        // navigate to wholesale product page by URL
        driver.get("%s%s".formatted(DOMAIN, WHOLESALE_PRODUCT_PAGE_PATH.formatted(uiProductID)));

        // wait wholesale product page loaded
        commonAction.verifyPageLoaded("Quay lại chi tiết sản phẩm", "Go back to product detail");

        // hide Facebook bubble
        commonAction.hideElement(driver.findElement(By.cssSelector("#fb-root")));

        return this;
    }

    int numOfWholesaleProduct;

    public WholesaleProductPage getWholesaleProductInfo() {
        numOfWholesaleProduct = nextInt(uiVariationList.size()) + 1;
        for (int i = 0; i < numOfWholesaleProduct; i++) {
            uiWholesaleProductPrice.set(i, nextInt(uiProductSellingPrice.get(i)) + 1);
            uiWholesaleProductStock.set(i, nextInt(Collections.max(uiProductStockQuantity.get(uiVariationList.get(i)))) + 1);
        }
        return this;
    }

    /* Without variation config */
    public void addWholesaleProductWithoutVariation() {
        // click add wholesale pricing button
        wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN)).click();

        // wait and input buy from
        wait.until(ExpectedConditions.visibilityOf(WITHOUT_VARIATION_BUY_FROM));
        act.moveToElement(WITHOUT_VARIATION_BUY_FROM).doubleClick().doubleClick().sendKeys(String.valueOf(uiWholesaleProductStock.get(0))).build().perform();

        // wait and input price per item
        wait.until(ExpectedConditions.visibilityOf(WITHOUT_VARIATION_PRICE_PER_ITEM));
        act.moveToElement(WITHOUT_VARIATION_PRICE_PER_ITEM).doubleClick().sendKeys(String.valueOf(uiWholesaleProductPrice.get(0))).build().perform();

        // open segment dropdown
        wait.until(ExpectedConditions.visibilityOf(WITHOUT_VARIATION_CUSTOMER_SEGMENT_DROPDOWN)).click();

        // search segment
        wait.until(ExpectedConditions.visibilityOf(CUSTOMER_SEGMENT_SEARCH_BOX));
        act.moveToElement(CUSTOMER_SEGMENT_SEARCH_BOX).doubleClick().sendKeys("%s\n".formatted(segmentName));

        // select segment
        wait.until(ExpectedConditions.visibilityOf(CUSTOMER_SEGMENT_CHECKBOX)).click();

        // close segment dropdown
        wait.until(ExpectedConditions.visibilityOf(WITHOUT_VARIATION_CUSTOMER_SEGMENT_DROPDOWN)).click();

        // complete config wholesale product
        wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SAVE_BTN)).click();
    }

    /* Variation config */
    List<String> variationSaleList = new ArrayList<>();
    void selectVariation() {
        for (int i = 0; i < numOfWholesaleProduct; i++) {
            // open Add variation popup
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_HEADER_ADD_VARIATION_BTN)).click();

            // wait popup visible
            wait.until(ExpectedConditions.visibilityOf(ADD_VARIATION_POPUP));

            // select variation
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commonAction.refreshListElement(VARIATION_ADD_VARIATION_POPUP_LIST_VARIATION_CHECKBOX).get(i));

            // close Add variation popup
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_ADD_VARIATION_POPUP_OK_BTN)).click();

            variationSaleList.add("%s,".formatted(uiVariationList.get(i).replace("|", " ")));
        }
    }

    public void addWholesaleProductVariation() {
        selectVariation();
        for (int i = 0; i < variationSaleList.size(); i++) {
            // get variation value
            String varValue = commonAction.refreshListElement(VARIATION_HEADER_VARIATION_VALUE).get(i).getText();

            // get variation index
            int varIndex = variationSaleList.indexOf(varValue);

            // click add wholesale pricing button
            wait.until(ExpectedConditions.elementToBeClickable(commonAction.refreshListElement(VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN).get(i))).click();

            // wait and input buy from
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_BUY_FROM.get(i))).sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
            VARIATION_BUY_FROM.get(i).sendKeys(String.valueOf(uiWholesaleProductStock.get(varIndex)));

            // wait and input price per item
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_PRICE_PER_ITEM.get(i))).sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
            VARIATION_PRICE_PER_ITEM.get(i).sendKeys(String.valueOf(uiWholesaleProductPrice.get(varIndex)));

            // open segment dropdown
            wait.until(ExpectedConditions.visibilityOf(commonAction.refreshListElement(VARIATION_CUSTOMER_SEGMENT_DROPDOWN).get(i))).click();

            // search segment
            commonAction.sleepInMiliSecond(1000);
            wait.until(ExpectedConditions.visibilityOf(CUSTOMER_SEGMENT_SEARCH_BOX));
            act.moveToElement(CUSTOMER_SEGMENT_SEARCH_BOX).doubleClick().sendKeys("%s\n".formatted(segmentName));

            // select segment
            wait.until(ExpectedConditions.visibilityOf(CUSTOMER_SEGMENT_CHECKBOX)).click();

            // close segment dropdown
            wait.until(ExpectedConditions.visibilityOf(commonAction.refreshListElement(VARIATION_CUSTOMER_SEGMENT_DROPDOWN).get(i))).click();
        }

        // complete config wholesale product
        wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SAVE_BTN)).click();
    }

}
