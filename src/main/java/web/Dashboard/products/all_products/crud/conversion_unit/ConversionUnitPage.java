package web.Dashboard.products.all_products.crud.conversion_unit;

import api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import api.Seller.products.all_products.ConversionUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.products.all_products.crud.ProductPage.updateProductPath;
import static web.Dashboard.products.all_products.crud.ProductPageElement.loc_btnConfigureAddConversionUnit;
import static web.Dashboard.products.all_products.crud.ProductPageElement.loc_chkAddConversionUnit;

public class ConversionUnitPage extends ConversionUnitElement {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(ConversionUnitPage.class);
    ProductInfoV2 productInfo;
    ConversionUnit unit;
    List<String> variationList;
    AssertCustomize assertCustomize;

    public ConversionUnitPage(WebDriver driver, LoginInformation loginInformation, ProductInfoV2 productInfo) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        this.productInfo = productInfo;
        unit = new ConversionUnit(loginInformation);
        assertCustomize = new AssertCustomize(driver);
    }

    public ConversionUnitPage navigateToConversionUnitPage() {
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath(productInfo.getId())));
        logger.info("Navigate to product detail page by URL, productId: {}", productInfo.getId());

        // If product has conversion unit, remove that to add new configuration
        if (commonAction.isCheckedJS(loc_chkAddConversionUnit)) {
            // uncheck to clear old configuration
            commonAction.clickJS(loc_chkAddConversionUnit);
        }

        // Check "Add Conversion Unit" checkbox to add new configuration
        commonAction.clickJS(loc_chkAddConversionUnit);
        if (productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"))
            logger.info("Not support conversion unit for product managed by IMEI/Serial at this time.");
        else {
            // click Configure button
            commonAction.click(loc_btnConfigureAddConversionUnit);

            // hide Facebook bubble
            commonAction.removeFbBubble();
        }
        return this;
    }

    /* Without variation config */
    public void addConversionUnitWithoutVariation() {
        if (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // click Select Unit button
            commonAction.click(withoutVariationSelectUnitBtn);
            logger.info("Add new conversion unit.");

            // select conversion unit
            commonAction.click(withoutVariationUnitTextBox);

            // get all conversion unit name on store
            List<String> unitNameList = unit.getListConversionUnitName();

            // get conversion name to assign to this product
            String unitName = unitNameList.isEmpty() ? unit.createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));
            commonAction.sendKeys(unitTextBoxOnSetupVariationConversionUnitPage, unitName);
            commonAction.click(By.xpath(unitLocator.formatted(unitName)));
            logger.info("Select conversion unit: {}", unitName);

            // input conversion unit quantity
            long quantity = Math.min(Math.max(Collections.max(productInfo.getProductStockQuantityMap().get(productInfo.getId())), 1), MAX_PRICE / productInfo.getOrgPrice());
            commonAction.sendKeys(withoutVariationQuantity, String.valueOf(quantity));
            logger.info("Conversion unit quantity: {}", quantity);

            // click Save button
            commonAction.click(withoutVariationSaveBtn);
        }
    }

    void selectVariation(String variation) {
        By locator = By.xpath(variationLocator.formatted(variation));
        commonAction.clickJS(locator);

        if (!commonAction.isCheckedJS(locator)) selectVariation(variation);
    }

    /* Variation config */
    public void addConversionUnitVariation() {
        if (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // number of conversion unit
            int numberOfConversionUnit = nextInt(productInfo.getVariationModelList().size()) + 1;

            // select variation
            for (int varIndex = 0; varIndex < numberOfConversionUnit; varIndex++) {
                // open Select Variation popup
                commonAction.openPopupJS(selectVariationBtn, selectVariationPopup);
                logger.info("Open select variation popup.");

                // get variation
                String variation = variationList.get(varIndex);

                // select variation
                selectVariation(variation);
                logger.info("Select variation: {}", variation);

                // close Add variation popup
                commonAction.closePopup(saveBtnOnSelectVariationPopup);
                logger.info("Close Select variation popup.");

                // add conversion unit configuration for variation
                commonAction.clickJS(variationConfigureBtn, varIndex);
                logger.info("Navigation to configure conversion unit for variation page.");

                // click Select Unit button
                commonAction.clickJS(selectUnitBtnOnSetupVariationConversionUnitPage);

                // get all conversion unit name on store
                List<String> unitNameList = unit.getListConversionUnitName();

                // get conversion name to assign to this product
                String unitName = unitNameList.isEmpty() ? unit.createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));

                // select conversion unit
                commonAction.sendKeys(withoutVariationUnitTextBox, unitName);
                commonAction.click(By.xpath(unitLocator.formatted(unitName)));
                logger.info("[{}] Select conversion unit: {}", variation, unitName);

                // input conversion unit quantity
                long quantity = MAX_PRICE / productInfo.getProductListingPrice().get(varIndex);
                commonAction.sendKeys(quantityOnSetupVariationConversionUnitPage, String.valueOf(quantity));
                logger.info("[{}] Conversion unit quantity: {}", variation, quantity);

                // click Save button on variation config
                commonAction.click(saveBtnOnSetupVariationConversionUnitPage);
                logger.info("[{}] Complete configure conversion unit.", variation);

                // wait conversion unit page loaded
                commonAction.waitURLShouldBeContains("/conversion-unit/variation/edit/");
                logger.info("[{}] Wait setup conversion unit page loaded.", variation);

            }

            // click Save button on setup conversion unit page
            commonAction.click(variationSaveBtn);
        }

    }
}
