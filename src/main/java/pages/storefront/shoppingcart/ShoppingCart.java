package pages.storefront.shoppingcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.dashboard.login.Login.storeURL;
import static api.dashboard.products.CreateProduct.productName;
import static api.dashboard.products.CreateProduct.wholesaleProductRate;
import static java.lang.Thread.sleep;
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

        commonAction.verifyPageLoaded(productName, productName);

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ShoppingCart getShoppingCartInfo() {
        List<WebElement> branchInfo = driver.findElements(BRANCH_INFO);
        for (int i = 0; i < branchInfo.size(); i++) {

            String branchName = branchInfo.get(i).findElement(BRANCH_NAME).getText();

            List<String> productName = new ArrayList<>();
            List<String> variationValue = new ArrayList<>();
            List<String> unitPrice = new ArrayList<>();
            List<String> couponCode = new ArrayList<>();
            List<String> conversionUnit = new ArrayList<>();
            List<String> totalPrice = new ArrayList<>();

            for (int id = 0; id < branchInfo.get(i).findElements(PRODUCT_INFO).size(); id++) {
                // product name
                branchInfo = driver.findElements(BRANCH_INFO);
                commonAction.waitElementList(branchInfo, i + 1);
                try {
                    productName.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(PRODUCT_NAME))).getText());
                } catch (StaleElementReferenceException ex) {
                    logger.info(ex);
                    branchInfo = driver.findElements(BRANCH_INFO);
                    commonAction.waitElementList(branchInfo, i + 1);
                    productName.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(PRODUCT_NAME))).getText());
                }

                // variation
                branchInfo = driver.findElements(BRANCH_INFO);
                commonAction.waitElementList(branchInfo, i + 1);
                try {
                    try {
                        variationValue.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(VARIATION_VALUE))).getText());
                    } catch (NoSuchElementException e) {
                        variationValue.add(null);
                    }
                } catch (StaleElementReferenceException ex) {
                    logger.info(ex);
                    branchInfo = driver.findElements(BRANCH_INFO);
                    commonAction.waitElementList(branchInfo, i + 1);
                    try {
                        variationValue.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(VARIATION_VALUE))).getText());
                    } catch (NoSuchElementException e) {
                        variationValue.add(null);
                    }
                }

                // unit price
                branchInfo = driver.findElements(BRANCH_INFO);
                commonAction.waitElementList(branchInfo, i + 1);

                try {
                    unitPrice.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(UNIT_PRICE))).getText().replaceAll("\\D", ""));
                } catch (StaleElementReferenceException ex) {
                    logger.info(ex);
                    branchInfo = driver.findElements(BRANCH_INFO);
                    commonAction.waitElementList(branchInfo, i + 1);
                    unitPrice.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(UNIT_PRICE))).getText().replaceAll("\\D", ""));
                }

                // coupon code
                branchInfo = driver.findElements(BRANCH_INFO);
                commonAction.waitElementList(branchInfo, i + 1);
                try {
                    try {
                        couponCode.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(COUPON_CODE))).getText());
                    } catch (NoSuchElementException e) {
                        couponCode.add(null);
                    }
                } catch (StaleElementReferenceException ex) {
                    logger.info(ex);
                    branchInfo = driver.findElements(BRANCH_INFO);
                    commonAction.waitElementList(branchInfo, i + 1);
                    try {
                        couponCode.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(COUPON_CODE))).getText());
                    } catch (NoSuchElementException e) {
                        couponCode.add(null);
                    }
                }

                // conversion unit
                branchInfo = driver.findElements(BRANCH_INFO);
                commonAction.waitElementList(branchInfo, i + 1);

                try {
                    conversionUnit.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(CONVERSION_UNIT))).getText());
                } catch (StaleElementReferenceException ex) {
                    logger.info(ex);
                    branchInfo = driver.findElements(BRANCH_INFO);
                    commonAction.waitElementList(branchInfo, i + 1);
                    conversionUnit.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(CONVERSION_UNIT))).getText());

                }
                // total price
                branchInfo = driver.findElements(BRANCH_INFO);
                commonAction.waitElementList(branchInfo, i + 1);

                try {
                    totalPrice.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(TOTAL_PRICE))).getText().replaceAll("\\D", ""));
                } catch (StaleElementReferenceException ex) {
                    logger.info(ex);
                    branchInfo = driver.findElements(BRANCH_INFO);
                    commonAction.waitElementList(branchInfo, i + 1);
                    totalPrice.add(wait.until(ExpectedConditions.visibilityOf(branchInfo.get(i).findElements(PRODUCT_INFO).get(id).findElement(TOTAL_PRICE))).getText().replaceAll("\\D", ""));
                }
            }

            cartInfo.put(branchName,
                    List.of(productName,
                            variationValue,
                            unitPrice,
                            couponCode,
                            conversionUnit,
                            totalPrice));
            cartInfo.keySet().forEach(key -> {
                System.out.println(key);
                cartInfo.get(key).forEach(System.out::println);
            });
        }

        System.out.println(wholesaleProductRate);
        return this;
    }

}
