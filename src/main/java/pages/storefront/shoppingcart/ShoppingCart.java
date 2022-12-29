package pages.storefront.shoppingcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.storeURL;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.branchName;
import static java.lang.Thread.sleep;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.SF_DOMAIN;

public class ShoppingCart extends ShoppingCartElement {
    final static Logger logger = LogManager.getLogger(ShoppingCart.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    /**
     * cartInfo.get(index), index:
     * <p>0: product name</p>
     * <p>1: variation value</p>
     * <p>2: unit price</p>
     * <p>3: coupon code</p>
     * <p>4: conversion unit</p>
     * <p>5: total price</p>
     */
    public static Map<String, List<List<String>>> cartInfo = new HashMap<>();

    public ShoppingCart(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);

        PageFactory.initElements(driver, this);
    }

    public CheckOutStep1 clickOnContinue() {
        commonAction.clickElement(CONTINUE_BTN);
        logger.info("Click on Continue button");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return new CheckOutStep1(driver);
    }

    public ShoppingCart navigateToShoppingCartByURL() {
        driver.get("https://%s%s/shopping-cart".formatted(storeURL, SF_DOMAIN));
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ShoppingCart getShoppingCartInfo() {
        BRANCH_INFO.forEach(branchInfo -> {
            String branchName = commonAction.getText(branchInfo.findElement(BRANCH_NAME));
            List<WebElement> productDetail = branchInfo.findElements(PRODUCT_INFO);
            List<String> productName = new ArrayList<>();
            List<String> variationValue = new ArrayList<>();
            List<String> unitPrice = new ArrayList<>();
            List<String> couponCode = new ArrayList<>();
            List<String> conversionUnit = new ArrayList<>();
            List<String> totalPrice = new ArrayList<>();
            productDetail.forEach(prodDetail -> {
                productName.add(commonAction.getText(prodDetail.findElement(PRODUCT_NAME)));
                try {
                    variationValue.add(commonAction.getText(prodDetail.findElement(VARIATION_VALUE)));
                } catch (NoSuchElementException ex) {
                    variationValue.add(null);
                }
                unitPrice.add(commonAction.getText(prodDetail.findElement(UNIT_PRICE)).replaceAll("\\D", ""));
                try {
                    couponCode.add(commonAction.getText(prodDetail.findElement(COUPON_CODE)));
                } catch (NoSuchElementException ex) {
                    couponCode.add(null);
                }
                conversionUnit.add(commonAction.getText(prodDetail.findElement(CONVERSION_UNIT)));
                totalPrice.add(commonAction.getText(prodDetail.findElement(TOTAL_PRICE)).replaceAll("\\D", ""));
            });
            cartInfo.put(branchName,
                    List.of(productName,
                            variationValue,
                            unitPrice,
                            couponCode,
                            conversionUnit,
                            totalPrice));
        });
        return this;
    }

    public void checkPrice() {
        Map<String, List<String>> salePriceMap = new ProductDetailPage(driver).getSalePriceMap();

        // set all cart stock = 1
        Map<String, List<Integer>> cartStock = productStockQuantity.keySet().stream()
                .collect(Collectors.toMap(varName -> varName, varName -> IntStream.range(0, productStockQuantity.get(varName).size()).mapToObj(i -> 1).toList(), (a, b) -> b));
        int id = 0;
        for (String brName : cartInfo.keySet()) {
            int branchIndex = branchName.indexOf(brName);
            for (int i = 0; i < cartInfo.get(brName).get(0).size(); i++) {
                String varName = cartInfo.get(brName).get(1).get(i);
                int varIndex = variationList.indexOf(varName);
                WebElement stockElement = BRANCH_INFO.get(id).findElement(PRODUCT_INFO).findElement(STOCK_QUANTITY);
                int stock;
                switch (salePriceMap.get(brName).get(varIndex)) {
                    case "FLASH SALE" -> stock = nextInt(flashSalePurchaseLimit.get(varIndex)) + 1;
                    case "DISCOUNT CAMPAIGN" -> stock = discountCampaignStock + nextInt(productStockQuantity.get(varName).get(branchIndex) - discountCampaignStock);
                    case "WHOLESALE PRODUCT" -> stock = wholesaleProductStock.get(varIndex) + nextInt(productStockQuantity.get(varName).get(branchIndex) - wholesaleProductStock.get(varIndex));
                    default -> stock = nextInt(productStockQuantity.get(varName).get(branchIndex)) + 1;
                }
                stockElement.clear();
                stockElement.sendKeys(String.valueOf(stock));
//                cartStock.get(varName).set(varIndex, stock);
            }
        }

    }

}
