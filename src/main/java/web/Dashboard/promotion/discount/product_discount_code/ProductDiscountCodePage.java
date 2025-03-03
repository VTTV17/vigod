package web.Dashboard.promotion.discount.product_discount_code;

import static java.lang.Thread.sleep;
import static utilities.links.Links.DOMAIN;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

public class ProductDiscountCodePage extends ProductDiscountCodeElement {
    WebDriverWait wait;
    UICommonAction commons;
    public String discountCode;
    public int numberOfUsedTimes;
    public boolean isReward;
    public boolean isNoExpiry;

    public int discountType;

    public int discountValue;

    public boolean isLimitNumberOfUsedTimes;

    public boolean isLimitPerUser;

    public int segmentType;

    public int requirementType;

    public int minimumPurchaseAmount;

    public int minimumQuantity;

    public ProductDiscountCodePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
    }

    final static Logger logger = LogManager.getLogger(ProductDiscountCodePage.class);

    public ProductDiscountCodePage inputCampaignName() {
        String campaignName = RandomStringUtils.random(255, true, false);
        wait.until(ExpectedConditions.elementToBeClickable(CAMPAIGN_NAME)).sendKeys(campaignName);
        logger.info("Input campaign name: %s".formatted(campaignName));
        return this;
    }

    public boolean isSetDiscountCodeAsRewardCheckboxDisabled() {
		return !APPLY_DISCOUNT_CODE_AS_REWARD_CHECKBOX.findElement(By.xpath("./input[@type='checkbox']")).isEnabled();
    }    
    
    public ProductDiscountCodePage setDiscountCodeAsReward(boolean... isReward) {
        this.isReward = isReward.length == 0 ? RandomUtils.nextBoolean() : isReward[0];
        if (this.isReward) {
            String discountDescription = RandomStringUtils.randomAlphabetic(1000);
            wait.until(ExpectedConditions.elementToBeClickable(APPLY_DISCOUNT_CODE_AS_REWARD_CHECKBOX)).click();
            logger.info("Apply Discount Code as a Reward checkbox: checked");
            wait.until(ExpectedConditions.elementToBeClickable(DISCOUNT_DESCRIPTION)).sendKeys(discountDescription);
            logger.info("Input reward description: %s".formatted(discountDescription));
        }
        return this;
    }

    public ProductDiscountCodePage setDiscountCode(String... discountCode) {
        if (discountCode.length == 0) {
            wait.until(ExpectedConditions.elementToBeClickable(GENERATE_CODE_BTN)).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(DISCOUNT_CODE)).sendKeys(discountCode[0]);
        }
        this.discountCode = wait.until(ExpectedConditions.visibilityOf(DISCOUNT_CODE)).getAttribute("value");
        logger.info("Discount code: %s".formatted(this.discountCode));
        return this;
    }

    public ProductDiscountCodePage setPromotionDate(boolean... isNoExpiry) {
        this.isNoExpiry = isNoExpiry.length == 0 ? RandomUtils.nextBoolean() : isNoExpiry[0];
        if (this.isNoExpiry) {
            wait.until(ExpectedConditions.elementToBeClickable(NO_EXPIRY_DATE_CHECKBOX)).click();
            logger.info("No expiry date - checked");

            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            waitElementList(AVAILABLE_DATE);
            int startIn = RandomUtils.nextInt(AVAILABLE_DATE.size());
            logger.info("Start in %s days".formatted(startIn));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", AVAILABLE_DATE.get(startIn));
            logger.info("Active date: %s".formatted(LocalDate.now().plusDays(startIn)));

        } else {
            logger.info("No expiry date - no checked");
            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();

            waitElementList(AVAILABLE_DATE);
            int startIn = RandomUtils.nextInt(AVAILABLE_DATE.size());
            logger.info("Start in %s days".formatted(startIn));

            new Actions(driver).moveToElement(AVAILABLE_DATE.get(startIn)).doubleClick().build().perform();
            APPLY_BTN.click();
            logger.info("Active date: %s".formatted(LocalDate.now().plusDays(startIn)));

        }
        return this;
    }

    public ProductDiscountCodePage setDiscountTypeAndValue(int... discountType) {
        waitElementList(TYPE_OF_DISCOUNT_LABEL);
        this.discountType = discountType.length == 0 ? RandomUtils.nextInt(3) : discountType[0];
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", TYPE_OF_DISCOUNT_LABEL.get(this.discountType));

        DISCOUNT_VALUE.click();
        DISCOUNT_VALUE.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        switch (this.discountType) {
            case 0 -> {
                logger.info("Discount type: Percentage");

                discountValue = RandomUtils.nextInt(100) + 1;
                DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
                logger.info("Percentage: %s".formatted(discountValue));
            }
            case 1 -> {
                logger.info("Discount type: Fixed amount");

                discountValue = RandomUtils.nextInt(1000000000) + 1;
                DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
                logger.info("Fixed amount: %s".formatted(discountValue));
            }

            case 2 -> {
                logger.info("Discount type: Free shipping");

                waitElementList(SHIPPING_METHOD);
                for (WebElement element : SHIPPING_METHOD) {
                    element.click();
                }
                discountValue = RandomUtils.nextInt(1000000000) + 1;
                DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
                logger.info("Free shipping: %s".formatted(discountValue));
            }
        }
        commons.click(loc_lblPageTitle);

        return this;
    }

    public ProductDiscountCodePage setLimitNumberOfUsedTimes(boolean... isLimitNumberOfUsedTimes) {
        waitElementList(USAGE_LIMIT_CHECKBOX);
        this.isLimitNumberOfUsedTimes = isLimitNumberOfUsedTimes.length == 0 ? RandomUtils.nextBoolean() : isLimitNumberOfUsedTimes[0];

        if (this.isLimitNumberOfUsedTimes) {
            USAGE_LIMIT_CHECKBOX.get(0).click();
            logger.info("Limit number of times this coupon can be used: checked");

            NUMBER_OF_USED_TIMES.click();
            NUMBER_OF_USED_TIMES.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
            numberOfUsedTimes = RandomUtils.nextInt(1000000000) + 1;
            NUMBER_OF_USED_TIMES.sendKeys(String.valueOf(numberOfUsedTimes));
            logger.info("Number of used times: %s".formatted(this.numberOfUsedTimes));
        }

        commons.click(loc_lblPageTitle);
        return this;
    }

    public ProductDiscountCodePage setLimitPerUser(boolean... isLimitPerUser) {
        waitElementList(USAGE_LIMIT_CHECKBOX);
        this.isLimitPerUser = isLimitPerUser.length == 0 ? RandomUtils.nextBoolean() : isLimitPerUser[0];
        if (this.isLimitPerUser) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", USAGE_LIMIT_CHECKBOX.get(1));
            logger.info("Limit to one use per customer: checked");
        }
        return this;
    }

    public ProductDiscountCodePage setCustomerSegment(String... segmentList) throws InterruptedException {
        waitElementList(commons.getElements(loc_rdoSegmentOptions));
        segmentType = segmentList.length == 0 ? 0 : 1;
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commons.getElement(loc_rdoSegmentOptions, segmentType));

        if (segmentType == 0) {
            logger.info("Customer segment: All customers");
        } else {
            logger.info("Customer segment: %s".formatted((Object) segmentList));

            commons.click(loc_lnkAddSegment);
            logger.info("Open add segment popup");

            for (String segment : segmentList) {
                wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(segment);
                sleep(2000);
                wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                logger.info("Search and select segment with keyword: %s".formatted(segment));
            }

            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            logger.info("Close add segment popup");
        }
        return this;
    }

    public ProductDiscountCodePage setAppliesProduct(int appliesProductTypeID, String... productCollectionsOrName) throws InterruptedException {
        waitElementList(commons.getElements(loc_rdoApplyToOptions));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commons.getElement(loc_rdoApplyToOptions, appliesProductTypeID));

        switch (appliesProductTypeID) {
            case 1 -> {
                logger.info("Applies to: Specific product collections");

                commons.click(loc_lnkAddCollectionOrSpecificProduct);
                logger.info("Open add product collection popup");

                for (String collection : productCollectionsOrName) {
                    wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(collection);
                    sleep(2000);
                    wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                    logger.info("Search and select product collection with keyword: %s".formatted(collection));
                }

                wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
                logger.info("Close add product collection popup");


            }
            case 2 -> {
                logger.info("Applies to: Specific products");

                commons.click(loc_lnkAddCollectionOrSpecificProduct);
                logger.info("Open add product popup");

                for (String collection : productCollectionsOrName) {
                    wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(collection);
                    sleep(2000);
                    wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                    logger.info("Search and select product with keyword: %s".formatted(collection));
                }

                wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
                logger.info("Close add product popup");
            }
            default -> logger.info("Applies to: Entire order");
        }
        return this;
    }

    public ProductDiscountCodePage setMinimumRequirements(int... requirementType) {
        waitElementList(MINIMUM_REQUIREMENTS);
        this.requirementType = requirementType.length == 0 ? RandomUtils.nextInt(3) : requirementType[0];
        switch (this.requirementType) {
            case 1 -> {
                MINIMUM_REQUIREMENTS.get(1).click();
                logger.info("Minimum requirement: Minimum purchase amount");
                wait.until(ExpectedConditions.elementToBeClickable(MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY)).click();
                MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                minimumPurchaseAmount = RandomUtils.nextInt(1000000000);
                MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY.sendKeys(String.valueOf(minimumPurchaseAmount));
                logger.info("Minimum purchase: %s".formatted(minimumPurchaseAmount));
            }
            case 2 -> {
                MINIMUM_REQUIREMENTS.get(2).click();
                logger.info("Minimum requirement: Minimum quantity of items");
                wait.until(ExpectedConditions.elementToBeClickable(MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY)).click();
                MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                minimumQuantity = RandomUtils.nextInt(1000000) + 1;
                MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY.sendKeys(String.valueOf(minimumQuantity));
                logger.info("Minimum quantity of items: %s".formatted(minimumQuantity));
            }
            default -> logger.info("Minimum requirement: None");
        }
        commons.click(loc_lblPageTitle);
        return this;
    }

    public ProductDiscountCodePage setBranch(int branchTypeID, String... branchList) throws InterruptedException {
        waitElementList(APPLICABLE_BRANCH_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", APPLICABLE_BRANCH_LABEL.get(branchTypeID));
        if (branchTypeID == 0) {
            logger.info("Applicable branch: All branches");
        } else {
            logger.info("Applicable branch: %s".formatted((Object) branchList));

            wait.until(ExpectedConditions.elementToBeClickable(SELECT_BRANCH_BTN)).click();
            logger.info("Open add branch popup");

            for (String branch : branchList) {
                wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(branch);
                sleep(2000);
                wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                logger.info("Search and select product with keyword: %s".formatted(branch));
            }

            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            logger.info("Close add product popup");
        }
        return this;
    }

    public ProductDiscountCodePage setPlatforms(List<String> platforms) {
        waitElementList(PLATFORM);
        for (int i = 0; i < PLATFORM.size(); i++) {
            if (platforms.contains(PLATFORM.get(i).getText())) {
                PLATFORM.get(i).click();
            }
        }
        return this;
    }
    
    public boolean isPlatformDisabled(String platform) {
    	waitElementList(PLATFORM);
    	WebElement element = null;
    	switch (platform) {
    	case "web":
    		element = PLATFORM.get(0);
    		break;
    	case "App":
    		element = PLATFORM.get(1);
    		break;
    	case "In-store":
    		element = PLATFORM.get(2);
    		break;
    	}
    	
    	if (commons.isElementVisiblyDisabled(element.findElement(By.xpath("./parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(element));
    		return true;
    	}
		return false;
    }
    public void clickOnTheSaveBtn() {
        commons.click(loc_btnSave);
        logger.info("Create a new product discount campaign successfully");
    }

    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }

	public ProductDiscountCodePage navigateToCreateDiscountCodeScreenByURL() {
		String url = DOMAIN + "/discounts/create/COUPON/";
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	    
	
	public ProductDiscountCodePage navigateToDiscountCodeDetailScreenByURL(int productDiscountCodeId) {
		String url = DOMAIN + "/discounts/detail/COUPON/" + productDiscountCodeId;
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	    
	
	public ProductDiscountCodePage navigateToEditDiscountCodeScreenByURL(int productDiscountCodeId) {
		String url = DOMAIN + "/discounts/edit/COUPON/" + productDiscountCodeId;
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	    
    
    public String getPageTitle() {
    	String title = commons.getText(loc_lblPageTitle);
    	logger.info("Retrieved page title: " + title);
    	return title;
    }
	
	public ProductDiscountCodePage selectSegmentOption(int option) {
		commons.click(loc_rdoSegmentOptions, option);
		logger.info("Selected segment option: " + option);
		return this;
	}	    
	
	public ProductDiscountCodePage clickAddSegmentLink() {
		commons.click(loc_lnkAddSegment);
		logger.info("Clicked on Add Segments link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(loc_dlgSelectSegment).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Segment dialog to appear");
		}
		return this;
	}	    
	
	public ProductDiscountCodePage selectApplyToOption(int option) {
		commons.click(loc_rdoApplyToOptions, option);
		logger.info("Selected Apply To option: " + option);
		return this;
	}	
	
	public ProductDiscountCodePage clickAddCollectionLink() {
		commons.click(loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Collection link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(loc_dlgSelectCollection).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Collection dialog to appear");
		}
		return this;
	}		
	
	public ProductDiscountCodePage clickAddProductLink() {
		commons.click(loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Product link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(loc_dlgSelectProduct).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Product dialog to appear");
		}
		return this;
	}		
	
	public boolean isProductPresentInDialog() {
		commons.sleepInMiliSecond(1000, "Wait a little for products to appear in the Add Product dialog");
		return !commons.getElements(loc_tblProductNames).isEmpty();
	}		
	
	public boolean isCollectionPresentInDialog() {
		commons.sleepInMiliSecond(1000, "Wait a little for collections to appear in the Add Collection dialog");
		return !commons.getElements(loc_tblProductNames).isEmpty();
	}		

    public ProductDiscountCodePage inputSearchTermInDialog(String searchTerm) {
    	commons.inputText(loc_txtSearchInDialog, searchTerm);
        logger.info("Input search term: " + searchTerm);
        commons.sleepInMiliSecond(1000, "Wait a little inputSearchTermInDialog"); // Will find a better way to remove this sleep
        new HomePage(driver).waitTillSpinnerDisappear1();
        return this;
    }	
	
}
