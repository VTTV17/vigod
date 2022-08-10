package pages.dashboard.products.all_products;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.dashboard.home.HomePage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ProductPage extends ProductVerify {
    public ProductPage(WebDriver driver) {
        super(driver);
    }

    Logger logger = LogManager.getLogger(LogManager.class);

    public ProductPage navigate() throws InterruptedException, IOException {
        new HomePage(driver).selectLanguage(language).navigateToAllProductsPage();
        logger.info("Navigate to All Products Page");
        wait.until(ExpectedConditions.titleIs("Admin Staging - Products"));
        logger.info("Title of Setting page is %s".formatted(driver.getTitle()));
        return this;
    }

    public ProductPage clickOnTheCreateProductBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PRODUCT_BTN)).click();
        logger.info("Click on the Create Product button");
        return this;
    }

    public ProductPage inputProductName(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_NAME)).sendKeys(Keys.CONTROL + "a" + Keys.DELETE + productName);
        logger.info("Input product name: %s".formatted(productName));
        return this;
    }

    public ProductPage inputProductDescription(String productDescription) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).sendKeys(Keys.CONTROL + "a" + Keys.DELETE + productDescription);
        logger.info("Input product descriptions: %s".formatted(productDescription));
        return this;
    }

    public ProductPage uploadProductImage(String imageFileName) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_IMAGE));
        PRODUCT_IMAGE.sendKeys(Paths.get(System.getProperty("user.dir") + "/src/main/resources/uploadfile/product_images/img.jpg".replace("/", File.separator)).toString());
        logger.info("Upload product image");
        return this;
    }

    public ProductPage inputPriceNormalProduct(String listingPrice, String sellingPrice, String costPrice) {
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(0))).sendKeys(Keys.CONTROL + "a" + Keys.DELETE + listingPrice);
        logger.info("Input listing price: %s".formatted(listingPrice));
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(1))).sendKeys(Keys.CONTROL + "a" + Keys.DELETE + sellingPrice);
        logger.info("Input selling price: %s".formatted(sellingPrice));
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(2))).sendKeys(Keys.CONTROL + "a" + Keys.DELETE + costPrice);
        logger.info("Input cost price: %s".formatted(costPrice));
        return this;
    }

    public ProductPage selectProductVAT(int vatID) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_VAT_DROPDOWN));
        return this;
    }
}
