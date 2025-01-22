package web.Dashboard.service;

import api.Seller.services.CreateServiceAPI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.enums.DisplayLanguage;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.home.HomePage;
import utilities.constant.Constant;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.character_limit.CharacterLimit;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.file.FileNameAndPath.FILE_IMAGE_1;
import static utilities.file.FileNameAndPath.FILE_IMAGE_2;

@Slf4j
public class CreateServicePage extends HomePage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    DataGenerator generate;
    final static Logger logger = LogManager.getLogger(CreateServicePage.class);
    CreateServiceElement createServiceUI;
    PropertiesUtil propertiesUtil;
    String createSuccessfullyMess;

    public CreateServicePage(WebDriver driver) throws Exception {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        commons = new UICommonAction(driver);
        generate = new DataGenerator();
        createServiceUI = new CreateServiceElement(driver);
        PageFactory.initElements(driver, this);
        createSuccessfullyMess = PropertiesUtil.getPropertiesValueByDBLang("services.create.successullyMessage");
    }

    public CreateServicePage inputServiceName(String serviceName) {
        commons.sendKeys(createServiceUI.loc_txtServiceName, serviceName);
        logger.info("Input " + serviceName + " into Service name field");
        return this;
    }

    public CreateServicePage inputListingPrice(String listingPrice) {
        commons.sendKeys(createServiceUI.loc_txtListingPrice, listingPrice);
        logger.info("Input " + listingPrice + " into Listing price field");
        return this;
    }

    public String inputSellingPrice(String listingPrice, String discountPercent) {
        int listingPricePars = Integer.parseInt(listingPrice);
        int sellingPrice = listingPricePars - listingPricePars * Integer.parseInt(discountPercent) / 100;
        commons.sendKeys(createServiceUI.loc_txtSellingPrice, String.valueOf(sellingPrice));
        logger.info("Input " + sellingPrice + " into Selling price field");
        return String.valueOf(sellingPrice);
    }

    public String inputSellingPrice(String sellingPrice) {
        commons.sendKeys(createServiceUI.loc_txtSellingPrice, sellingPrice);
        logger.info("Input " + sellingPrice + " into Selling price field");
        return sellingPrice;
    }

    public CreateServicePage checkOnShowAsListingService() {
        commons.checkTheCheckBoxOrRadio(createServiceUI.loc_chbShowAsListingValue, createServiceUI.loc_chbShowAsListingAction);
        logger.info("Check on Show as listing service checkbox");
        return this;
    }

    public CreateServicePage uncheckOnShowAsListingService() {
        commons.sleepInMiliSecond(1000);
        commons.uncheckTheCheckboxOrRadio(createServiceUI.loc_chbShowAsListingValue, createServiceUI.loc_chbShowAsListingAction);
        logger.info("Uncheck on Show as listing service checkbox");
        return this;
    }

    public CreateServicePage inputServiceDescription(String description) {
        commons.sendKeys(createServiceUI.loc_txaServiceDescription, description);
        logger.info("Input " + description + " into description field");
        return this;
    }

    public CreateServicePage inputCollections(int quantity) {
        commons.sleepInMiliSecond(1000);
        for (int i = 0; i < quantity; i++) {
            commons.click(createServiceUI.loc_frmCollection);
            logger.info("Click on collection form");
            commons.click(createServiceUI.loc_lstCollectionSuggestion, 0);
            logger.info("Select collection");
        }
        return this;
    }

    public List<String> getSelectedCollection() {
        List<String> selectedCollections = new ArrayList<>();
        for (WebElement element : commons.getElements(createServiceUI.loc_lblSelectedCollection)) {
            selectedCollections.add(element.getText());
        }
        logger.debug("selectedCollections: " + selectedCollections);
        return selectedCollections;
    }

    public CreateServicePage uploadImages(String... fileNames) {
        commons.uploadMultipleFile(commons.getElement(createServiceUI.loc_txtUploadImage), "serviceimages", fileNames);
        logger.info("Upload multiple file: " + Arrays.toString(fileNames));
        return this;
    }

    public CreateServicePage inputLocations(String... locations) {
        for (String loctaion : locations) {
            commons.sendKeys(createServiceUI.loc_txtLocation, loctaion + "\n");
            logger.info("Input " + loctaion + " into Location field");
        }
        return this;
    }

    public CreateServicePage inputTimeSlots(String... timeSlots) {
        for (String timeSlot : timeSlots) {
            commons.sendKeys(createServiceUI.loc_txtTimeSlot, timeSlot + "\n");
            logger.info("Input %s into TimeSlot field".formatted(timeSlot));
            commons.sleepInMiliSecond(100);
        }
        return this;
    }

    public CreateServicePage inputSEOTitle(String SEOTitle) {
        commons.sleepInMiliSecond(500);
        commons.sendKeys(createServiceUI.loc_txtSEOTitle, SEOTitle);
        logger.info("Input " + SEOTitle + " into SEO title field");
        return this;
    }

    public CreateServicePage inputSEODescription(String SEODesciption) {
        commons.sendKeys(createServiceUI.loc_txtSEODescription, SEODesciption);
        logger.info("Input " + SEODesciption + " into SEO description field");
        return this;
    }

    public CreateServicePage inputSEOKeyword(String SEOKeyword) {
        commons.sendKeys(createServiceUI.loc_txtSEOKeyWords, SEOKeyword);
        logger.info("Input " + SEOKeyword + " into SEO keyword field");
        return this;
    }

    public CreateServicePage inputSEOUrl(String SEOUrl) {
        commons.sendKeys(createServiceUI.loc_txtSEOUrl, SEOUrl);
        logger.info("Input " + SEOUrl + " into SEO url field");
        return this;
    }

    public CreateServicePage clickSaveBtn() {
        commons.sleepInMiliSecond(1000);
        commons.click(createServiceUI.loc_btnSave);
        ;
        logger.info("Click on Save button");
        return this;
    }

    public String getSEOTitle() {
        String title = commons.getAttribute(createServiceUI.loc_txtSEOTitle, "value");
        logger.info("Retrieved SEO Title: %s".formatted(title));
        return title;
    }

    public CreateServicePage verifyCreateSeviceSuccessfulMessage() {
        commons.waitForElementVisible(commons.getElement(createServiceUI.loc_dlgNotification_lblMessage));
        String message = commons.getText(createServiceUI.loc_dlgNotification_lblMessage);
        Assert.assertEquals(message, createSuccessfullyMess);
        logger.info("Create service successfully popup is shown");
        return this;
    }

    public void verifyTextOfPage() throws Exception {
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblCreateNewService), propertiesUtil.getPropertiesValueByDBLang("services.create.pageTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_btnSave), propertiesUtil.getPropertiesValueByDBLang("services.create.saveBtn"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_btnCancel), propertiesUtil.getPropertiesValueByDBLang("services.create.cancelBtn"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblBasicInformationTitle), propertiesUtil.getPropertiesValueByDBLang("services.create.basicInformationTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblServiceName), propertiesUtil.getPropertiesValueByDBLang("services.create.serviceNameLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblListingPrice), propertiesUtil.getPropertiesValueByDBLang("services.create.listingPriceLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblSellingPrice), propertiesUtil.getPropertiesValueByDBLang("services.create.sellingPriceLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_chbShowAsListingAction), propertiesUtil.getPropertiesValueByDBLang("services.create.showAsListingServiceTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblDescription), propertiesUtil.getPropertiesValueByDBLang("services.create.descriptionLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblCollection), propertiesUtil.getPropertiesValueByDBLang("services.create.collectionLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_plhSelectCollections), propertiesUtil.getPropertiesValueByDBLang("services.create.selectCollectionsHintTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblImages), propertiesUtil.getPropertiesValueByDBLang("services.create.imagesLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblDrapAndDrop), propertiesUtil.getPropertiesValueByDBLang("services.create.drapAndDropTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblLocationsAndTimesTitleAndDescription), propertiesUtil.getPropertiesValueByDBLang("services.create.locations&TimesTitleAndDescription"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblLocations), propertiesUtil.getPropertiesValueByDBLang("services.create.locationsLbl"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_txtLocation, "placeholder"), propertiesUtil.getPropertiesValueByDBLang("services.create.inputLocationHintTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblTimeSlots), propertiesUtil.getPropertiesValueByDBLang("services.create.timeSlotsLbl"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_tltTimeSlots, "data-original-title"), propertiesUtil.getPropertiesValueByDBLang("services.create.titmeSlotsTooltipTxt"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_txtTimeSlot, "placeholder"), propertiesUtil.getPropertiesValueByDBLang("services.create.inputTimeSlotsHintTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblListOfLocationsAndTimeslots), propertiesUtil.getPropertiesValueByDBLang("services.create.listLocationAndTimeslotsTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblLocation), propertiesUtil.getPropertiesValueByDBLang("services.create.locationLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblTimeSlot), propertiesUtil.getPropertiesValueByDBLang("services.create.timeSlotLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblNoLocationTimeSlot), propertiesUtil.getPropertiesValueByDBLang("services.create.noLocationAndTimeSlotTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblSEOSettings), propertiesUtil.getPropertiesValueByDBLang("services.create.seoSettingsTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblLivePreview), propertiesUtil.getPropertiesValueByDBLang("services.create.livePreviewLbl"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_tltLivePreview, "data-original-title"), propertiesUtil.getPropertiesValueByDBLang("services.create.livePreviewTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblSEOTitle), propertiesUtil.getPropertiesValueByDBLang("services.create.seoTitleLbl"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_tltSEOTitle, "data-original-title"), propertiesUtil.getPropertiesValueByDBLang("services.create.seoTitleTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblSEODescription), propertiesUtil.getPropertiesValueByDBLang("services.create.seoDescriptionLbl"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_tltSEODescription, "data-original-title"), propertiesUtil.getPropertiesValueByDBLang("services.create.seoDescriptionTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblSEOKeywords), propertiesUtil.getPropertiesValueByDBLang("services.create.seoKeywordsLbl"));
        Assert.assertEquals(commons.getAttribute(createServiceUI.loc_tltSEOKeywords, "data-original-title"), propertiesUtil.getPropertiesValueByDBLang("services.create.seoKeywordsTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblURLLink), propertiesUtil.getPropertiesValueByDBLang("services.create.urlLinkLbl"));
    }

    public CreateServicePage checkErrorSaveWithBlankField() throws Exception {
        clickSaveBtn();
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageServiceName), PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        Assert.assertTrue(commons.isElementNotDisplay(createServiceUI.loc_lblErrorMessageListingPrice));
        Assert.assertTrue(commons.isElementNotDisplay(createServiceUI.loc_lblErrorMessageSellingPrice));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageDescription), PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageImages), PropertiesUtil.getPropertiesValueByDBLang("services.create.imagesFieldEmptyError"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageLocations), PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageTimeSlots), PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        return this;
    }

    public CreateServicePage checkErrorWhenInputListingPriceOutOfRange() throws Exception {
        inputListingPrice("-1");
        clickSaveBtn();
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageListingPrice), PropertiesUtil.getPropertiesValueByDBLang("services.create.listingPrice.minimumRequiredError"));
        commons.sleepInMiliSecond(500);
        inputListingPrice("100000000000");
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageListingPrice), PropertiesUtil.getPropertiesValueByDBLang("services.create.listingPrice.maximumRequiredError"));
        return this;
    }

    public CreateServicePage checkErrorWhenInputSellingPriceOutOfRange() throws Exception {
        String listingPrice = "10000";
        inputListingPrice(listingPrice);
        inputSellingPrice("-1");
        clickSaveBtn();
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageSellingPrice), PropertiesUtil.getPropertiesValueByDBLang("services.create.sellingPrice.minimumRequiredError"));
        commons.sleepInMiliSecond(500);
        inputSellingPrice("1000000");
        String sellingPriceActual = String.join("", commons.getText(createServiceUI.loc_lblErrorMessageSellingPrice).split(","));
        Assert.assertEquals(sellingPriceActual, PropertiesUtil.getPropertiesValueByDBLang("services.create.sellingPrice.maximumRequiredError").formatted(listingPrice));
        return this;
    }

    public CreateServicePage checkMaximumCharacterForServiceNameField() {
        inputServiceName(Constant.TEXT_101_CHAR);
        String serviceNameDisplay = commons.getAttribute(createServiceUI.loc_txtServiceName, "value");
        Assert.assertEquals(serviceNameDisplay.length(), CharacterLimit.MAX_CHAR_SERVICE_NAME);
        return this;
    }

    public CreateServicePage checkMaximumCharacterForSEOTitleField() {
        inputSEOTitle(Constant.TEXT_201_CHAR);
        String SEOTitleDisplay = getSEOTitle();
        Assert.assertEquals(SEOTitleDisplay.length(), CharacterLimit.MAX_CHAR_SEO_TITLE);
        return this;
    }

    public CreateServicePage checkMaximumCharacterForSEODescriptionField() {
        inputSEODescription(Constant.TEXT_326_CHAR);
        String SEODescriptionDisplay = commons.getAttribute(createServiceUI.loc_txtSEODescription, "value");
        Assert.assertEquals(SEODescriptionDisplay.length(), CharacterLimit.MAX_CHAR_SEO_DESCRIPTION);
        return this;
    }

    public CreateServicePage clickCodeViewInDescription() {
        commons.sleepInMiliSecond(1000);
        commons.click(createServiceUI.loc_txaDescription_icnViewMore);
        commons.click(createServiceUI.loc_txaDescription_btnCodeView);
        logger.info("Select code view.");
        return this;
    }

    public CreateServicePage inputDescriptionAsHTMLFormat(String html) {
        commons.sendKeys(createServiceUI.loc_txaCodeViewDescription, html);
        logger.info("Input HTML format for description");
        return this;
    }

    public void checkStockInventoryInDatabase(int itemId, int expected) throws SQLException {
        String query = "SELECT * FROM \"fr_01_item_services\".inventory WHERE item_id =%s".formatted(itemId);
        ResultSet resultSet = new InitConnection().createConnection(DB_HOST, DB_USER, DB_PASS).prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getInt("stock"));
            Assert.assertEquals(resultSet.getInt("stock"), expected);
        }
    }

    public CreateServicePage inputRandomLocations(int quantity) {
        for (int i = 1; i <= quantity; i++) {
            String locationRd = generate.generateString(5) + generate.generateString(5);
            inputLocations(locationRd);
        }
        return this;
    }

    public CreateServicePage inputRandomTimes(int quantity) {
        List<String> addedTime = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            String timeRd = generate.generatNumberInBound(10, 23) + ":" + generate.generatNumberInBound(10, 59);
            while (addedTime.contains(timeRd)) {
                logger.debug("Duplicate: " + timeRd);
                timeRd = generate.generatNumberInBound(10, 23) + ":" + generate.generatNumberInBound(10, 59);
            }
            inputTimeSlots(timeRd);
            addedTime.add(timeRd);
        }
        return this;
    }

    public CreateServicePage verifyMaximumErrorLocation() throws Exception {
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblMaximumErrorLocation), PropertiesUtil.getPropertiesValueByDBLang("services.create.locations.maximumError"));
        logger.info("Verify maximum error in locations");
        return this;
    }

    public CreateServicePage verifyMaximumErrorTime() throws Exception {
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblMaximumErrorTimeSlot), PropertiesUtil.getPropertiesValueByDBLang("services.create.timeslots.maximumErrorTime"));
        logger.info("Verify maximum error in locations");
        return this;
    }

    public CreateServicePage createServiceWhenHasPermission() {
        String[] images = {FILE_IMAGE_1, FILE_IMAGE_2};
        String[] locations = {"Thu Duc", "Quan 9", "Quan 1", "Quan 2"};
        String[] timeSlots = {"20:00", "21:00", "22:30"};
        String serviceName = "Automation Service SV" + generate.generateString(10);
        String description = "Automation sevices description";
        String listingPrice = "2" + generate.generateNumber(5);
        inputServiceName(serviceName);
        inputListingPrice(listingPrice);
        inputSellingPrice(listingPrice, generate.generateNumber(2));
        uncheckOnShowAsListingService();
        inputServiceDescription(description);
        uploadImages(images);
        inputLocations(locations);
        inputTimeSlots(timeSlots);
        clickSaveBtn();
        verifyCreateSeviceSuccessfulMessage();
        return this;
    }

    public ServiceManagementPage clickCloseBTNOnNotificationPopup() {
        commons.click(createServiceUI.loc_dlgNotification_btnClose);
        logger.info("Click on Close button on Notification pop up.");
        waitTillLoadingDotsDisappear();
//        commons.sleepInMiliSecond(1000);
        return new ServiceManagementPage(driver);
    }

    /*------------Edit---------------------*/
    public CreateServicePage clickEditTranslation() {
//        waitTillLoadingDotsDisappear();
        commons.sleepInMiliSecond(1000);
        commons.click(createServiceUI.loc_btnEditTranslation);
        logger.info("Click on edit translation button.");
        return this;
    }

    public int getServiceId() {
        waitTillSpinnerDisappear();
        String url = commons.getCurrentURL();
        String[] urlSplits = url.split("/");
        String id = urlSplits[urlSplits.length - 1];
        return Integer.parseInt(id);
    }

    public String getNameTranslate() {
        commons.sleepInMiliSecond(2000);
        String name = commons.getAttribute(createServiceUI.loc_dlgTranslate_txtName, "value");
        logger.info("Get name translate: ");
        return name;
    }

    public String getDescriptionTranslate() {
        String description = commons.getText(createServiceUI.loc_dlgTranslate_txaDescription);
        logger.info("Get description in translate popup");
        return description;
    }

    public CreateServicePage inputNameTranslate(String nameTranslate) {
        commons.sendKeys(createServiceUI.loc_dlgTranslate_txtName, nameTranslate);
        logger.info("Input name in translate.");
        return this;
    }

    public CreateServicePage inputDescriptionTranslate(String descriptionTranslate) {
        commons.sendKeys(createServiceUI.loc_dlgTranslate_txaDescription, descriptionTranslate);
        logger.info("Input description in translate.");
        return this;
    }

    public List<String> inputLocationsTranslate() {
        List<String> locations = new ArrayList<>();
        for (WebElement el : commons.getElements(createServiceUI.loc_dlgTranslate_txtLocations)) {
            String locationRd = generate.generateString(5) + " " + generate.generateString(5);
            commons.inputText(el, locationRd);
            locations.add(locationRd);
            logger.info("Input location: " + locationRd);
        }
        return locations;
    }

    public CreateServicePage clickSaveTranslateBTN() {
        commons.sleepInMiliSecond(2000);
        commons.click(createServiceUI.loc_dlgTranslate_btnSave);
        logger.info("Click on Save button in translate.");
        return this;
    }

    public CreateServicePage verifyUpdateTranslateSuccessfulMessage() throws Exception {
        Assert.assertEquals(getToastMessage(), PropertiesUtil.getPropertiesValueByDBLang("services.create.updateTranslationSuccessully"));
        logger.info("Update translate successfully message is show.");
        return this;
    }

    public CreateServicePage inputSEOTitleTranslate(String SEOTitleTranslate) {
        commons.sendKeys(createServiceUI.loc_dlgTranslate_txtSEOTitle, SEOTitleTranslate);
        logger.info("Input SEO title translate: " + SEOTitleTranslate);
        return this;
    }

    public CreateServicePage inputSEODescriptionTranslate(String SEODescriptionTranslate) {
        commons.sendKeys(createServiceUI.loc_dlgTranslate_txtSEODescription, SEODescriptionTranslate);
        logger.info("Input SEO description translate: " + SEODescriptionTranslate);
        return this;
    }

    public CreateServicePage inputSEOKeywordTranslate(String SEOKeywordTranslate) {
        commons.sendKeys(createServiceUI.loc_dlgTranslate_txtSEOKeywords, SEOKeywordTranslate);
        logger.info("Input SEO Keyword translate: " + SEOKeywordTranslate);
        return this;
    }

    public CreateServicePage inputSEOUrlTranslate(String SEOUrlTranslate) {
        commons.sendKeys(createServiceUI.loc_dlgTranslate_txtURLLink, SEOUrlTranslate);
        logger.info("Input SEO Keyword translate: " + SEOUrlTranslate);
        return this;
    }

    public String getServiceDescription() {
        String des = commons.getText(createServiceUI.loc_txaServiceDescription);
        logger.info("Get Service description");
        return des;
    }

    public CreateServicePage updateServiceStatus(String activeOrDeactive) {
        String activeTxt = "";
        String inactiveTxt = "";
        try {
            activeTxt = PropertiesUtil.getPropertiesValueByDBLang("services.create.activeStatus");
            inactiveTxt = PropertiesUtil.getPropertiesValueByDBLang("services.create.inactiveStatus");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (activeOrDeactive.equalsIgnoreCase("active")) {
            if (commons.getText(createServiceUI.loc_lblStatus).equalsIgnoreCase(inactiveTxt)) {
                commons.click(createServiceUI.loc_btnActiveDeactive);
            }
        } else if (activeOrDeactive.equalsIgnoreCase("inactive")) {
            if (commons.getText(createServiceUI.loc_lblStatus).equalsIgnoreCase(activeTxt)) {
                commons.click(createServiceUI.loc_btnActiveDeactive);
            }
        }
        commons.click(createServiceUI.loc_btnSave);
        return this;
    }

    public CreateServicePage verifyServiceStatusActive() throws Exception {
        commons.sleepInMiliSecond(1000);
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblStatus), PropertiesUtil.getPropertiesValueByDBLang("services.create.activeStatus"));
        logger.info("Verify service status show active");
        return this;
    }

    public CreateServicePage verifyServiceStatusInactive() throws Exception {
        commons.sleepInMiliSecond(1000);
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblStatus), PropertiesUtil.getPropertiesValueByDBLang("services.create.inactiveStatus"));
        logger.info("Verify service status show inactive");
        return this;
    }

    public CreateServicePage verifyUpdateServiceSuccessfully() throws Exception {
        Assert.assertEquals(commons.getText(createServiceUI.loc_dlgNotification_lblMessage), PropertiesUtil.getPropertiesValueByDBLang("services.update.successfullyMessage"));
        logger.info("Verify service status show inactive");
        return this;
    }

    public String getLivePreviewLink() {
        String url = commons.getAttribute(createServiceUI.loc_urlLivePreview, "href");
        return url;
    }

    public String getSellingPrice() {
        String sellingPrice = commons.getAttribute(createServiceUI.loc_txtSellingPrice, "value");
        return sellingPrice;
    }

    public int getImageListSize() {
        commons.sleepInMiliSecond(1000);
        return commons.getElements(createServiceUI.loc_lstImage).size();
    }

    public CreateServicePage removeAllImages() throws Exception {
        int size = commons.getElements(createServiceUI.loc_lstIconRemoveImage, 3).size();
        for (int i = 0; i <= size; i++) {
            if (!commons.getElements(createServiceUI.loc_lstIconRemoveImage, 1).isEmpty())
                commons.click(createServiceUI.loc_lstIconRemoveImage, 0);
            logger.info("Remove image: " + i);
            commons.sleepInMiliSecond(200);
        }
        Assert.assertEquals(commons.getText(createServiceUI.loc_lblErrorMessageImages), PropertiesUtil.getPropertiesValueByDBLang("services.create.imagesFieldEmptyError"));
        logger.info("Removed alll images");
        return this;
    }

    public CreateServicePage removeAllCollection() {
        int size = commons.getElements(createServiceUI.loc_lstIconDeleteCollection, 2).size();
        for (int i = 0; i < size; i++) {
            commons.click(createServiceUI.loc_lstIconDeleteCollection, 0);
        }
        logger.info("Remove all collection");
        return this;
    }

    public CreateServicePage clickDeleteService() {
        commons.click(createServiceUI.loc_btnDeleteService);
        logger.info("Click on Delete button");
        return this;
    }

    public CreateServicePage verifyDeleteConfirmMessage() throws Exception {
        Assert.assertEquals(commons.getText(createServiceUI.loc_dlgNotification_lblMessage), PropertiesUtil.getPropertiesValueByDBLang("services.delete.confirmMessage"));
        logger.info("Verify delete comfirmation message.");
        return this;
    }

    public CreateServicePage clickOKBtnOnConfirmPopup() {
        commons.click(createServiceUI.loc_dlgNotification_btnOK);
        logger.info("Click on OK button on Confirmation popup.");
        return this;
    }

    public CreateServicePage verifyDeleteSuccessfullyMessage() throws Exception {
        commons.sleepInMiliSecond(2000);
        Assert.assertEquals(commons.getText(createServiceUI.loc_dlgNotification_lblMessage), PropertiesUtil.getPropertiesValueByDBLang("services.delete.successfullyMessage"));
        logger.info("Verify delete successfully message.");
        return this;
    }

    public CreateServicePage updateServiceWhenHasPermission() throws Exception {
        String serviceName = "Automation update name " + generate.generateString(5);
        String description = "Automation update description " + generate.generateString(5);
        String listingPrice = "3" + generate.generateNumber(5);
        commons.sleepInMiliSecond(2000);
        inputServiceName(serviceName);
        inputServiceDescription(description);
        inputListingPrice(listingPrice);
        inputSellingPrice(listingPrice, generate.generateNumber(2));
        clickSaveBtn();
        verifyUpdateServiceSuccessfully();
        return this;
    }

    public ServiceInfo callAPICreateService(LoginInformation loginInformation) {
        ServiceInfo serviceInfo = new ServiceInfo();
        new CreateServiceAPI(loginInformation).createService(serviceInfo);
        return serviceInfo;
    }

    @SneakyThrows
    public CreateServicePage selectLanguageTranslate(DisplayLanguage language) {
        commons.click(createServiceUI.loc_dlgTranslate_btnLanguage);
        List<WebElement> languageOptions = commons.getElements(createServiceUI.loc_dlgTranslate_ddlLanguageOptions, 2);
        for (int i = 0; i < languageOptions.size(); i++) {
            switch (language) {
                case ENG -> {
                    if (commons.getText(languageOptions.get(i)).equals("English") || commons.getText(languageOptions.get(i)).equals("Tiếng Anh"))
                        commons.click(createServiceUI.loc_dlgTranslate_ddlLanguageOptions, i);
                }
                case VIE -> {
                    if (commons.getText(languageOptions.get(i)).equals("Vietnamese") || commons.getText(languageOptions.get(i)).equals("Tiếng Việt"))
                        commons.click(createServiceUI.loc_dlgTranslate_ddlLanguageOptions, i);
                }
                default -> throw new Exception("Language: %s not define".formatted(language.name()));
            }
        }
        return this;
    }
}
