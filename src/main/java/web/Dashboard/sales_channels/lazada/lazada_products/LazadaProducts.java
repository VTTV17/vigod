package web.Dashboard.sales_channels.lazada.lazada_products;

import api.Seller.sale_channel.lazada.APILazadaProducts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.enums.Domain;
import web.Dashboard.home.HomePage;
import web.Dashboard.sales_channels.lazada.synchronization.LazadaSynchronizationPage;

import java.util.List;

import static utilities.links.Links.DOMAIN;

public class LazadaProducts extends  LazadaProductElements{
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LazadaProducts.class);
    public LazadaProducts(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }
    public LazadaProducts navigateByURL(){
        String url = DOMAIN +"/channel/lazada/product";
        commonAction.navigateToURL(url);
        logger.info("Navigate to url: {}",url);
        return this;
    }
    public LazadaProducts selectProduct(List<Long> lazadaProductId){
        lazadaProductId.forEach(i-> {
            commonAction.click(loc_ckbSelectProduct(lazadaProductId));
            logger.info("Select lazada product id: "+i);
        });
        return this;
    }
    public LazadaProducts clickOnSelectAction(){
        commonAction.click(loc_btnSelectAction);
        logger.info("Click on Select Action button.");
        return this;
    }
    public LazadaProducts clickOnCreateToGoSell(){
        commonAction.click(loc_btnCreateProductToGoSell);
        logger.info("Click on Create product to GoSell");
        return this;
    }
    public LazadaProducts clickOnUpdateToGoSell(){
        commonAction.click(loc_btnUpdateProductToGoSell);
        logger.info("Click on Update product to GoSell.");
        return this;
    }
    public LazadaProducts waitToFetchProduct(){
        commonAction.waitInvisibilityOfElementLocated(loc_lblFetchProductStatus);
        return this;
    }
    public LazadaProducts createProductToGoSell(List<Long> lazadaProductId){
        selectProduct(lazadaProductId);
        clickOnSelectAction();
        clickOnCreateToGoSell();
        waitToFetchProduct();
        return this;
    }
    public LazadaProducts updateProductToGoSell(List<Long> lazadaProductId){
        selectProduct(lazadaProductId);
        clickOnSelectAction();
        clickOnUpdateToGoSell();
        waitToFetchProduct();
        return this;
    }
    public LazadaProducts clickDownloadProduct(List<Long> lazadaProductId){
        for (long productId:lazadaProductId) {
            commonAction.click(loc_icnDownloadProduct(productId));
            new HomePage(driver).waitTillLoadingDotsDisappear();
        }
        return this;
    }
}
