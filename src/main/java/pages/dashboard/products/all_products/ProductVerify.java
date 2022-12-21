package pages.dashboard.products.all_products;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.settings.branch_management.BranchPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.PRODUCT_DETAIL_PATH;

public class ProductVerify extends ProductElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    static int countFail = 0;

    public static String productID;

    public static Map<String, String> branchInfo;

    Logger logger = LogManager.getLogger(ProductVerify.class);

    public ProductVerify(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    public String getNewestProductID() {
        return wait.until(ExpectedConditions.visibilityOf(NEWEST_PRODUCT_ID)).getText();
    }

    public ProductVerify openProductDetailPage() {
        driver.get(DOMAIN + PRODUCT_DETAIL_PATH + getNewestProductID());
        return this;
    }

    public ProductVerify checkProductName(String productName) throws IOException {
        String actProductName = wait.until(ExpectedConditions.visibilityOf(PRODUCT_NAME)).getAttribute("value");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actProductName, productName, "[Failed] Product name does not match");
        return this;
    }

    public ProductVerify checkProductDescription(String productDescription) throws IOException {
        String actDescription = wait.until(ExpectedConditions.visibilityOf(PRODUCT_DESCRIPTION)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail, actDescription, productDescription, "[Failed] Product description does not match");
        return this;
    }

    public ProductVerify checkPrice(int listingPrice, int sellingPrice, int costPrice) throws IOException {
        List<Integer> priceList = List.of(listingPrice, sellingPrice, costPrice);
        for (int i = 0; i < NORMAL_PRODUCT_PRICE.size(); i++) {
            String actPrice = wait.until(ExpectedConditions.visibilityOf(NORMAL_PRODUCT_PRICE.get(i))).getAttribute("value").replace(",", "");
            String priceType;
            switch (i) {
                case 0 -> priceType = "Listing";
                case 1 -> priceType = "Selling";
                default -> priceType = "Cost";
            }
            countFail = new AssertCustomize(driver).assertEquals(countFail, actPrice, priceList.get(i).toString(), "[Failed] %s price does not match".formatted(priceType));
        }
        return this;
    }

    public ProductVerify checkVAT(String VAT) throws IOException {
        String actVAT = wait.until(ExpectedConditions.visibilityOf(PRODUCT_VAT_DROPDOWN)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail, actVAT, VAT, "[Failed] Product VAT does not match");
        return this;
    }

    public ProductVerify checkCollection(List<String> collectionList) throws IOException {
        for (WebElement element : SELECTED_COLLECTION_LIST) {
            String actCollection = wait.until(ExpectedConditions.visibilityOf(element)).getText();
            countFail = new AssertCustomize(driver).assertTrue(countFail, collectionList.contains(actCollection), "[Failed] Product collection does not match");
        }
        return this;
    }

    public ProductVerify checkStock(int stockQuantity) throws IOException {
        for (WebElement element : QUANTITY_STOCK_BY_BRANCH) {
            String actQuantity = wait.until(ExpectedConditions.visibilityOf(element)).getAttribute("default_value");
            countFail = new AssertCustomize(driver).assertEquals(countFail, actQuantity, String.valueOf(stockQuantity), "[Failed] Product stock does not match");
        }
        return this;
    }

    public ProductVerify checkDimension(int weight, int length, int width, int height) throws IOException {
        String actWeight = wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WEIGHT)).getAttribute("value");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actWeight, String.valueOf(weight), "[Failed] Product weight does not match");

        String actLength = wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LENGTH)).getAttribute("value");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actLength, String.valueOf(length), "[Failed] Product length does not match");

        String actWidth = wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WIDTH)).getAttribute("value");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actWidth, String.valueOf(width), "[Failed] Product width does not match");

        String actHeight = wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_HEIGHT)).getAttribute("value");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actHeight, String.valueOf(height), "[Failed] Product height does not match");
        return this;
    }

    public ProductVerify checkSelectedPlatform(List<String> platformList) throws IOException {
        for (int i = 0; i < PRODUCT_PLATFORM_LABEL.size(); i++) {
            if (platformList.contains(PRODUCT_PLATFORM_LABEL.get(i).getText())) {
                countFail = new AssertCustomize(driver).assertTrue(countFail, PRODUCT_PLATFORM_CHECKBOX.get(i).isSelected(), "[Failed] %s does not selected".formatted(PRODUCT_PLATFORM_LABEL.get(i).getText()));
            } else {
                countFail = new AssertCustomize(driver).assertFalse(countFail, PRODUCT_PLATFORM_CHECKBOX.get(i).isSelected(), "[Failed] %s does not deselected".formatted(PRODUCT_PLATFORM_LABEL.get(i).getText()));
            }
        }
        return this;
    }

    /**
     * <p> get branch information</p>
     * <p> get the just created ProductID to check access permission</p>
     * <p> craw Storefront URL on the header </p>
     */
    public void getURLAndNavigateToStoreFront() {
        productID = getNewestProductID();
        branchInfo = new BranchPage(driver).getBranchNameAndAddress();
        driver.get(wait.until(ExpectedConditions.visibilityOf(SF_URL)).getAttribute("href"));
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains("Copyright © 2022");
        });
    }

    public ProductVerify completeVerify() {
        if (countFail > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(countFail));
        }
        countFail = 0;
        return this;
    }
}
