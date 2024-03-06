package web.Dashboard.promotion.discount.product_discount_campaign;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.data.DataGenerator;
import utilities.links.Links;
import web.Dashboard.promotion.discount.DiscountPage;
import utilities.commons.UICommonAction;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.character_limit.CharacterLimit.*;

public class ProductDiscountCampaignPage extends ProductDiscountCampaignElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    public static String wholesaleCampaignName;
    public static boolean isNoExpiry;
    public static int startIn;
    public static int endIn;

    public static int discountType;

    public static int discountValue;

    public static int segmentType;
    public static List<String> segmentList;
    public static int appliesType;
    public static List<String> collectionList;
    public static List<String> productList;

    public ProductDiscountCampaignPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    Logger logger = LogManager.getLogger(ProductDiscountCampaignPage.class);

    /**
     * input discount campaign name
     */
    public ProductDiscountCampaignPage inputCampaignName(String... campaignName) {
        // get discount campaign name for another test
        wholesaleCampaignName = campaignName.length == 0
                ? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_NAME) + 1)
                : campaignName[0];

        // input discount campaign name
        wait.until(ExpectedConditions.elementToBeClickable(CAMPAIGN_NAME)).sendKeys(wholesaleCampaignName);

        // log
        logger.info("Input campaign name: %s".formatted(wholesaleCampaignName));

        return this;
    }


    /**
     * get date coordinates
     */
    private void selectDate(int numberOfNextDay) {
        // get will be selected date
        String date = DateTimeFormatter.ofPattern("dd").format(LocalDate.now().plusDays(numberOfNextDay));
        String monthAndYear = DateTimeFormatter.ofPattern("MMM yyyy").format(LocalDate.now().plusDays(numberOfNextDay));

        // get current month in calendar
        String currentMonth = wait.until(ExpectedConditions.visibilityOf(CURRENT_MONTH)).getText();

        // go to selected month and year
        while (!currentMonth.equals(monthAndYear)) {
            // go to next month
            wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN)).click();

            // get current month
            currentMonth = wait.until(ExpectedConditions.visibilityOf(CURRENT_MONTH)).getText();
        }

        // wait list available date in current month visible
        commonAction.waitElementList(AVAILABLE_DATE);

        // find and select date
        for (WebElement element : AVAILABLE_DATE) {

            // if date < 10 => add "0"
            String dateInCalendar = element.getText().length() == 1 ? "0" + element.getText() : element.getText();

            // check and select date
            if (dateInCalendar.equals(date)) {
                element.click();
                logger.info("Select date");
                break;
            }
        }


    }

    /**
     * set campaign time
     */
    @SafeVarargs
    public final ProductDiscountCampaignPage setPromotionDate(List<Serializable>... timeSetting) {
        // get time setting:
        List<Serializable> setting = timeSetting.length == 0
                ? List.of(RandomUtils.nextBoolean())
                : timeSetting[0];

        // isNoExpiry: Discount campaign has expiry date or not
        isNoExpiry = (setting.size() > 0) ? (boolean) setting.get(0) : RandomUtils.nextBoolean();

        // startIn: campaign will be started in startIn days
        startIn = (setting.size() > 1) ? (int) setting.get(1) : RandomUtils.nextInt(MAX_PROMOTION_DATE);

        // endIn: campaign will be ended in endIn day
        endIn = (setting.size() > 2) ? (int) setting.get(2) : (RandomUtils.nextInt(MAX_PROMOTION_DATE - startIn) + startIn);

        // No Expiry date checkbox has been checked
        if (isNoExpiry) {
            // check on No Expiry date checkbox
            wait.until(ExpectedConditions.elementToBeClickable(NO_EXPIRY_DATE_CHECKBOX)).click();
            logger.info("No expiry date - checked");

            // open calendar
            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            logger.info("Open calendar");

            // select start date
            selectDate(startIn);
            logger.info("Active date: %s".formatted(LocalDate.now().plusDays(startIn)));
        } else {
            // log
            logger.info("No expiry date - no checked");

            // open calendar
            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            logger.info("Open calendar");

            // select start date
            selectDate(startIn);

            // select end date
            selectDate(endIn);

            // complete select start - end date
            wait.until(ExpectedConditions.elementToBeClickable(APPLY_BTN)).click();
            logger.info("Active date: %s - %s".formatted(LocalDate.now().plusDays(startIn), LocalDate.now().plusDays(endIn)));
        }
        return this;
    }

    public ProductDiscountCampaignPage setDiscountTypeAndValue(int... typeOfDiscount) {
        // get discount type
        // 0: Percentage
        // 1: Fixed amount
        discountType = typeOfDiscount.length > 0 ? typeOfDiscount[0] : RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);

        // get discount value
        // max percentage value: 100
        // max fixed amount value: 1,000,000,000
        discountValue = (typeOfDiscount.length > 1) ? typeOfDiscount[1]
                : ((int) ((discountType == 0) ? (RandomUtils.nextInt(MAX_PERCENT_DISCOUNT) + 1) : (Math.random() * MAX_FIXED_AMOUNT)) + 1);

        // wait discount type element visible
        commonAction.waitElementList(TYPE_OF_DISCOUNT_LABEL);

        // select discount type
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", TYPE_OF_DISCOUNT_LABEL.get(discountType));

        // clear default discount
        wait.until(ExpectedConditions.elementToBeClickable(DISCOUNT_VALUE)).click();
        DISCOUNT_VALUE.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);

        // input new discount value
        if (discountType == 0) {
            // log
            logger.info("Discount type: Percentage");

            // validate discount value
            discountValue = Math.min(discountValue, MAX_PERCENT_DISCOUNT);

            // input discount value
            DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
            logger.info("Percentage: %s".formatted(discountValue));

        } else {
            // log
            logger.info("Discount type: Fixed amount");

            // validate discount value
            discountValue = Math.min(discountValue, MAX_FIXED_AMOUNT);

            // input discount value
            DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
            logger.info("Fixed amount: %s".formatted(discountValue));
        }

        // click around
        PAGE_TITLE.click();

        return this;
    }

    @SafeVarargs
    public final ProductDiscountCampaignPage setCustomerSegment(List<Object>... segmentCondition) throws InterruptedException {
        // get segment condition
        List<Object> segCondition = segmentCondition.length == 0 ? List.of() : segmentCondition[0];

        // get segment condition type
        // 0: All Customers
        // 1: Specific segments
        segmentType = (segCondition.size() > 0)
                ? (int) segCondition.get(0)
                : RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE);

        // get segment list
        List<String> segmentList = (segmentType != 0)
                // segment type = specific segment
                // check segment is provided or not
                // in case no segment provide => generate new segment for test
                ? ((segCondition.size() > 1) ? (List<String>) segCondition.get(1) : new DiscountPage(driver).generateSegmentForTest())
                // opposite, segment list = null
                : List.of();

        // wait segment condition element visible
        commonAction.waitElementList(CUSTOMER_SEGMENT_LABEL);

        // select segment condition
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", CUSTOMER_SEGMENT_LABEL.get(segmentType));

        // if segment condition is specific segment
        // select segment from list segment
        if (segmentType != 0) {
            // open select segment popup
            wait.until(ExpectedConditions.elementToBeClickable(ADD_SEGMENT_BTN)).click();
            logger.info("Open add segment popup");

            // init segment list and save select segment
            ProductDiscountCampaignPage.segmentList = new ArrayList<>();

            // search and select segment
            for (String segment : segmentList) {
                // input segment name into search box
                wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).clear();
                SEARCH_BOX.sendKeys(segment);

                // wait api return result
                sleep(2000);

                // check if segment is not added => add segment to segment list
                for (WebElement element : LIST_SEGMENT_NAME) {
                    if (!ProductDiscountCampaignPage.segmentList.contains(element.getText())) {
                        ProductDiscountCampaignPage.segmentList.add(element.getText());
                    }
                }

                // select segment
                wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                logger.info("Search and select segment with keyword: %s".formatted(segment));
            }

            // complete select segment
            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            logger.info("Close add segment popup");

            System.out.println(ProductDiscountCampaignPage.segmentList);
        } else {
            // debug log
            logger.info("Customer segment: All customers");
        }

        return this;
    }

    public ProductDiscountCampaignPage setAppliesProduct(List<Object>... appliesToCondition) throws InterruptedException {
        // get applies condition
        List<Object> appliesCondition = appliesToCondition.length == 0 ? List.of() : appliesToCondition[0];

        // get applies to condition type:
        // 0: All products
        // 1: Specific product collections
        // 2: Specific products
        appliesType = appliesCondition.size() > 0
                ? (int) appliesCondition.get(0)
                : RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLIES_TO_TYPE);

        collectionList = (appliesType == 1) ? (appliesCondition.size() > 1) ? (List<String>) appliesCondition.get(1) : List.of() : List.of();
        productList = (appliesType == 2) ? (appliesCondition.size() > 1) ? (List<String>) appliesCondition.get(1) : List.of() : List.of();


        commonAction.waitElementList(APPLIES_TO_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", APPLIES_TO_LABEL.get(appliesType));

        switch (appliesType) {
            case 1 -> {
                logger.info("Applies to: Specific product collections");

                wait.until(ExpectedConditions.elementToBeClickable(ADD_COLLECTION_OR_PRODUCT_BTN)).click();
                logger.info("Open add product collection popup");

                for (String collection : collectionList) {
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

                wait.until(ExpectedConditions.elementToBeClickable(ADD_COLLECTION_OR_PRODUCT_BTN)).click();
                logger.info("Open add product popup");

                for (String product : productList) {
                    wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(product);
                    sleep(2000);
                    wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                    logger.info("Search and select product with keyword: %s".formatted(product));
                }

                wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
                logger.info("Close add product popup");
            }
            default -> logger.info("Applies to: All products");
        }
        return this;
    }

    public ProductDiscountCampaignPage setMinimumQuantityOfProducts(int quantity) {
        MINIMUM_REQUIREMENTS.click();
        MINIMUM_REQUIREMENTS.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        MINIMUM_REQUIREMENTS.sendKeys(String.valueOf(quantity));
        logger.info("Set minimum quantity of products: %s".formatted(quantity));
        PAGE_TITLE.click();
        return this;
    }

    public ProductDiscountCampaignPage setBranch(int branchTypeID, String... branchList) throws InterruptedException {
        commonAction.waitElementList(APPLICABLE_BRANCH_LABEL);
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

	public ProductDiscountCampaignPage tickAppliesTo(int optionIndex) {
		commonAction.waitElementList(APPLIES_TO_LABEL);
		if (optionIndex ==0) {
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(optionIndex));
			logger.info("Ticked 'All Products' radio button.");
		} else if (optionIndex ==1) {
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(optionIndex));
			logger.info("Ticked 'Specific Product Collections' radio button.");
		} else if (optionIndex ==2) {
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(optionIndex));
			logger.info("Ticked 'Specific Products' radio button.");
		} else {
			logger.info("Input value is not in range (0:2). By default, 'All Products' radio button is ticked.");
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(0));
		}
		return this;
	}
    public ProductDiscountCampaignPage tickApplicableBranch(int optionIndex) {
        commonAction.waitVisibilityOfElementLocated(loc_cbxApplicableBranch);
        if (optionIndex ==0) {
            commonAction.checkTheCheckBoxOrRadio(loc_cbxApplicableBranch,optionIndex);
            logger.info("Ticked 'All branches' radio button.");
        } else if (optionIndex ==1) {
            commonAction.checkTheCheckBoxOrRadio(loc_cbxApplicableBranch,optionIndex);
            logger.info("Ticked 'Specific Branch' radio button.");
        } else {
            logger.info("Input value is not in range (0:1). By default, 'All Branches' radio button is ticked.");
            commonAction.checkTheCheckBoxOrRadio(loc_cbxApplicableBranch,0);
        }
        return this;
    }
    public void clickOnTheSaveBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        logger.info("Create a new product discount campaign successfully");
    }
    public String createDefaultCampaign(){
        String campaignName ="Discount campaign "+ new DataGenerator().generateString(10);
        inputCampaignName();
        setPromotionDate();
        clickOnTheSaveBtn();
        return campaignName;
    }
    public ProductDiscountCampaignPage navigateToCreateProductCampaignPageUrl(){
        String url = Links.DOMAIN + "/discounts/create/WHOLE_SALE";
        commonAction.navigateToURL(url);
        return this;
    }
    public ProductDiscountCampaignPage clickOnAddCollection(){
        commonAction.click(loc_btnAddCollection);
        logger.info("Click on Add collection.");
        for (int i=0; i<5; i++) {
            if (!commonAction.getElements(loc_dlgSelectCollection).isEmpty()) break;
            commonAction.sleepInMiliSecond(500, "Wait a little until the Add Collection dialog to appear");
        }
        return this;
    }
    public ProductDiscountCampaignPage clickOnAddSegment(){
        commonAction.click(loc_btnAddSegment);
        logger.info("Click on Add Segment.");
        for (int i=0; i<5; i++) {
            if (!commonAction.getElements(loc_dlgSelectSegment).isEmpty()) break;
            commonAction.sleepInMiliSecond(500, "Wait a little until the Add Segment dialog to appear");
        }
        return this;
    }
    public ProductDiscountCampaignPage clickOnAddProducts(){
        commonAction.click(loc_btnAddProduct);
        logger.info("Click on Add product.");
        for (int i=0; i<5; i++) {
            if (!commonAction.getElements(loc_dlgSelectProduct).isEmpty()) break;
            commonAction.sleepInMiliSecond(500, "Wait a little until the Add product dialog to appear");
        }
        return this;
    }
    public ProductDiscountCampaignPage clickOnSelectBranch(){
        commonAction.click(loc_btnSelectBranch);
        logger.info("Click on Select Branch.");
        return this;
    }
    public boolean isProductShowOnSelectProductList(String productName){
        commonAction.inputText(loc_txtSearch,productName);
        List<WebElement> productNames = commonAction.getElements(loc_lst_lblProductName);
        if (productNames.isEmpty()) return false;
        for (int i=0; i<productNames.size();i++) {
            if(commonAction.getText(loc_lst_lblProductName,i).equalsIgnoreCase(productName))
                return true;
        }
        return false;
    }
    public List<String> getBranchList(){
        List<WebElement> branchNamesEls = commonAction.getElements(loc_lst_lblBranchName);
        List<String> branchNames = new ArrayList<>();
        for (int i=0;i<branchNamesEls.size();i++) {
            branchNames.add(commonAction.getText(loc_lst_lblBranchName,i));
        }
        Collections.sort(branchNames);
        return branchNames;
    }
}
