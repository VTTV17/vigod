package web.Dashboard.products.all_products.crud.wholesale_price;

import api.Seller.customers.APISegment;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.products.all_products.crud.ProductPage.updateProductPath;
import static web.Dashboard.products.all_products.crud.ProductPageElement.loc_btnConfigureWholesalePricing;
import static web.Dashboard.products.all_products.crud.ProductPageElement.loc_chkAddWholesalePricing;

public class WholesaleProductPage extends WholesaleProductElement {
    UICommonAction commonAction;
    WebDriver driver;
    Logger logger = LogManager.getLogger(WholesaleProductPage.class);

    private List<Long> wholesaleProductPrice;
    private List<Integer> wholesaleProductStock;
    int productId;
    List<String> variationList;
    List<String> variationModelList;
    Map<String, List<Integer>> productStockQuantity;
    AssertCustomize assertCustomize;
    List<Long> productSellingPrice;
    LoginInformation loginInformation;

    public WholesaleProductPage(WebDriver driver, LoginInformation loginInformation, int productId) {
        this.driver = driver;
        this.loginInformation = loginInformation;
        commonAction = new UICommonAction(driver);
        this.productId = productId;
        ProductInfo productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        variationList = productInfo.getVariationValuesMap().get(new StoreInformation(loginInformation).getInfo().getDefaultLanguage());
        variationModelList = productInfo.getVariationModelList();
        productStockQuantity = productInfo.getProductStockQuantityMap();
        productSellingPrice = productInfo.getProductSellingPrice();
        assertCustomize = new AssertCustomize(driver);
    }

    public WholesaleProductPage navigateToWholesaleProductPage(){
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath(productId)));
        driver.navigate().refresh();
        logger.info("Navigate to create wholesale product page, productId: %s.".formatted(productId));

        // if 'Add Wholesale Pricing' checkbox is not checked, check and click on 'Configure' button
        if (!commonAction.isCheckedJS(loc_chkAddWholesalePricing))
            commonAction.clickJS(loc_chkAddWholesalePricing);

        // click Configure button
        commonAction.click(loc_btnConfigureWholesalePricing);

        // hide Facebook bubble
        commonAction.removeFbBubble();

        return this;
    }

    int numOfWholesaleProduct;

    public WholesaleProductPage getWholesaleProductInfo() {
        wholesaleProductPrice = new ArrayList<>(productSellingPrice);
        wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, wholesaleProductPrice.size()).forEachOrdered(i -> wholesaleProductStock.add(0));
        numOfWholesaleProduct = nextInt(variationList.size()) + 1;
        IntStream.range(0, numOfWholesaleProduct).forEach(varIndex -> {
            wholesaleProductPrice.set(varIndex, nextLong(productSellingPrice.get(varIndex)) + 1);
            wholesaleProductStock.set(varIndex, nextInt(Math.max(Collections.max(productStockQuantity.get(variationModelList.get(varIndex))), 1)) + 1);
        });
        return this;
    }

    /* Without variation config */
    public void addWholesaleProductWithoutVariation(){
        // click add wholesale pricing button
        commonAction.click(withoutVariationAddWholesalePricingBtn);
        logger.info("Open setup wholesale price table.");

        // wait and input buy from
        commonAction.sendKeys(withoutVariationBuyFrom, String.valueOf(wholesaleProductStock.get(0)));
        logger.info("Input buy from: %s.".formatted(wholesaleProductStock.get(0)));

        // wait and input price per item
        commonAction.sendKeys(withoutVariationWholesalePrice, String.valueOf(wholesaleProductPrice.get(0)));
        logger.info("Input price per item: {}", String.format("%,d", wholesaleProductPrice.get(0)));

        // open segment dropdown
        commonAction.click(withoutVariationSegmentDropdown);
        logger.info("Open segment dropdown.");

        // select segment
        List<Integer> listSegmentIdInStore = new APISegment(loginInformation).getListSegmentIdInStore();
        if (listSegmentIdInStore.isEmpty()) {
            // if store do not have any segment, select All customers option
            logger.info("Select segment: %s.".formatted(commonAction.getText(allCustomerTextInDropdown)));
            commonAction.click(allCustomerCheckbox);
        } else {
            // in-case store have some segment, select any segment.
            int segmentId = listSegmentIdInStore.get(0);
            logger.info("Select segment: %s.".formatted(commonAction.getText(By.cssSelector(segmentText.formatted(segmentId)))));
            commonAction.click(By.cssSelector(segmentLocator.formatted(segmentId)));
        }

        // close segment dropdown
        commonAction.click(withoutVariationSegmentDropdown);
        logger.info("Close segment dropdown.");

        // complete config wholesale product
        commonAction.click(saveBtn);
    }

    /* Variation config */
    void selectVariation(String variation) {
        By locator = By.xpath(variationLocator.formatted(variation));
        commonAction.clickJS(locator);

        if (!commonAction.isCheckedJS(locator)) selectVariation(variation);

    }

    List<String> addConfigureForVariation() {
        List<String> variationSaleList = new ArrayList<>();
        for (int varIndex = 0; varIndex < numOfWholesaleProduct; varIndex++) {
            // get variation
            String variation = variationList.get(varIndex).replace(" ", "|");

            // open Add variation popup
            commonAction.openPopupJS(variationAddVariationBtn, addVariationPopup);
            logger.info("Open select variation popup on wholesale config page.");

            // select variation
            selectVariation(variation);
            logger.info("Add new wholesale pricing configure for '%s' variation.".formatted(variation));

            // close Add variation popup
            commonAction.closePopup(okBtnOnAddVariationPopup);

            // add variation to sale list
            variationSaleList.add("%s,".formatted(variation));
        }
        return variationSaleList;
    }

    public void addWholesaleProductVariation(){
        // get list variation has wholesale pricing config
        List<String> variationSaleList = addConfigureForVariation();

        // add config for each variation
        for (int index = 0; index < variationSaleList.size(); index++) {
            // get variation value
            String value = commonAction.getText(variationValue, index);

            // get variation index
            int varIndex = variationSaleList.indexOf(value);

            // click add wholesale pricing button
            commonAction.clickJS(variationAddWholesalePricingBtn, index);

            // wait and input buy from
            commonAction.sendKeys(variationBuyFrom, index, String.valueOf(wholesaleProductStock.get(varIndex)));
            logger.info("[%s] Input buy from: %s.".formatted(value, wholesaleProductStock.get(varIndex)));

            // wait and input price per item
            commonAction.sendKeys(variationWholesalePrice, index, String.valueOf(wholesaleProductPrice.get(varIndex)));
            logger.info("[%s] Input price per item: %,d.".formatted(value, wholesaleProductPrice.get(varIndex)));

            // open segment dropdown
            commonAction.click(variationSegmentDropdown, index);

            // select segment
            List<Integer> listSegmentIdInStore = new APISegment(loginInformation).getListSegmentIdInStore();
            if (listSegmentIdInStore.isEmpty()) {
                // if store do not have any segment, select All customers option
                logger.info("Select segment: %s.".formatted(commonAction.getText(allCustomerTextInDropdown)));
                commonAction.click(allCustomerCheckbox);
            } else {
                // in-case store have some segment, select any segment.
                int segmentId = listSegmentIdInStore.get(0);
                logger.info("Select segment: %s.".formatted(commonAction.getText(By.cssSelector(segmentText.formatted(segmentId)))));
                commonAction.click(By.cssSelector(segmentLocator.formatted(segmentId)));
            }

            // close segment dropdown
            commonAction.click(variationSegmentDropdown, index);
        }

        // complete config wholesale product
        commonAction.click(saveBtn);
    }

}
