package pages.buyerapp.productDetail;

import api.dashboard.products.CreateProduct;
import api.dashboard.products.ProductInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.buyerapp.shopcart.BuyerShopCartPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.UICommonAction;
import utilities.UICommonMobile;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.promotion.DiscountCampaignInfo;
import utilities.model.dashboard.promotion.FlashSaleInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class BuyerProductDetailPage extends BuyerProductDetailElement {
    private WebDriver driver;
    private WebDriverWait wait;
    private Logger logger = LogManager.getLogger(BuyerProductDetailPage.class);

    private UICommonMobile commons;
    private ProductInfo productInfo;
    private BranchInfo brInfo;
    private StoreInfo storeInfo;
    private FlashSaleInfo flashSaleInfo;
    private DiscountCampaignInfo discountCampaignInfo;
    private WholesaleProductInfo wholesaleProductInfo;
    private int countFail;

    public BuyerProductDetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        commons = new UICommonMobile(driver);
    }

    void getInfo() {
        int productID = new ProductPage(driver).getProductID() != 0 ? new ProductPage(driver).getProductID() : new CreateProduct().getProductID();

        // get product information
        productInfo = new ProductInformation().getInfo(productID);
    }

    void checkProductName(String barcode, String language) {
        // get product name on dashboard
        String dbProductName = productInfo.getProductNameMap().get(barcode).get(language);

        // get product name on shop online
        String sfProductName = wait.until(visibilityOfElementLocated(PRODUCT_NAME)).getText();

        // check product name
        countFail = new AssertCustomize(driver).assertTrue(countFail, sfProductName.equals(dbProductName), "[Failed][Check product name] Product name should be %s but found %s.".formatted(dbProductName, sfProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    public void getReviews() {
        new UICommonMobile(driver).swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.5);

        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@resource-id,'product_detail_content_include_tabs_text_1')]"))).getText();
        System.out.println(text);
    }

    public BuyerShopCartPage buyNowProduct(int quantity){
        commons.clickElement(BUY_NOW_BTN);
        if(!commons.getText(BUY_NOW_POPUP_QUANTITY_TEXT_BOX).equals(String.valueOf(quantity))){
            commons.inputText(BUY_NOW_POPUP_QUANTITY_TEXT_BOX, String.valueOf(quantity));
        }
        commons.clickElement(BUY_NOW_POPUP_BUY_BTN);
        commons.waitSplashScreenLoaded();
        return new BuyerShopCartPage(driver);
    }

}
