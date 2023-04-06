import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.buyerapp.NavigationBar;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.service.CreateServicePage;
import pages.dashboard.service.ServiceManagementPage;
import pages.dashboard.service.servicecollections.ServiceCollectionManagement;
import pages.storefront.GeneralSF;
import pages.storefront.header.HeaderSF;
import pages.storefront.services.CollectionSFPage;
import pages.storefront.services.ServiceDetailPage;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.screenshot.Screenshot;

import java.io.IOException;
import java.util.List;

import static utilities.account.AccountTest.ADMIN_CREATE_NEW_SHOP_PASSWORD;
import static utilities.file.FileNameAndPath.*;
import static utilities.links.Links.*;

public class CreateServiceTest extends BaseTest {
    LoginPage login;
    HomePage home;
    ServiceManagementPage serviceManagement;
    CreateServicePage createService;
    pages.storefront.login.LoginPage loginSF;
    HeaderSF headerSF;
    ServiceDetailPage serviceDetailPage;
    CollectionSFPage collectionSFPage;
    GeneralSF generalSF;
    String serviceName = "";
    String listingPrice = "";
    String sellingPrice = "";
    String description = "Service description";
    List<String> selectedCollection = null;
    String[] images = {FILE_IMAGE_1, FILE_IMAGE_2};
    String[] imagesAddMore = {FILE_IMAGE_3, FILE_IMAGE_4};
    String[] locations = {"Thu Duc", "Quan 9", "Quan 1", "Quan 2"};
    String[] timeSlots = {"20:00", "21:00", "22:30"};
    String SEOTitle = "SEO title service";
    String SEODesctiption = "SEO description service";
    String SEOKeyword = "seo keyword";
    String SEOUrl = "";
    String userName;
    String passWord;
    String SF_URL;
    String languageDB;
    String languageSF;
    String sfAllServicesTxt;
    String htmlDescription;
    String passWordTestPermission;
    String serviceEdit;
    String serviceDelete;
    String serviceTestStatus;
    @BeforeClass
    public void beforeClass() throws Exception {
        userName = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passWord = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        languageDB = language;
        languageSF = language;
        sfAllServicesTxt = PropertiesUtil.getPropertiesValueBySFLang("serviceCollection.allServicesPageTitle");
        tcsFileName = FILE_CREATE_SERVICE_TCS;
        htmlDescription = "<div class=\"ct_box_detail width_common\">" +
                "<div style=\"font-size: 13px!important; font-family: arial, helvetica, sans-serif;color: black!important;\">" +
                "<p><strong>Sữa Chống Nắng Anessa Perfect UV Sunscreen Skincare Milk N SPF50+ PA++++ (New 2022) 60ml&nbsp;</strong>hiện đã có mặt tại <strong>Hasaki.</strong></p>" +
                "<p><strong><br></strong></p>" +
                "<p style=\"text-align: center;\"><img title=\"Sữa Chống Nắng Anessa Dưỡng Da Kiềm Dầu 60ml\" src=\"https://media.hasaki.vn/wysiwyg/HaNguyen1/sua-chong-nang-anessa-duong-da-kiem-dau-bao-ve-hoan-hao-1.jpg\" alt=\"Sữa Chống Nắng Anessa Dưỡng Da Kiềm Dầu 60ml (Mẫu Mới 2022)\" width=\"800\" class=\"loaded fr-fic fr-dii\" data-was-processed=\"true\"></p>" +
                "<h2 style=\"font-size: 17px!important;font-weight: bolder;\"><strong>Thông số sản phẩm:</strong></h2>" +
                "<p><strong>HSD:</strong> 3 năm kể từ ngày sản xuất</p>" +
                "<p>&nbsp;</p>" +
                "<p style=\"text-align: center;\"><span class=\"fr-video fr-fvc fr-dvi fr-draggable\" contenteditable=\"false\"><iframe src=\"https://www.youtube.com/embed/pIgHZv5MBxE\" frameborder=\"0\" width=\"425\" height=\"350\" class=\"fr-draggable\"></iframe></span></p>" +
                "</div></div>";
        passWordTestPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        serviceEdit = PropertiesUtil.getEnvironmentData("serviceTestEdit");
        SF_URL = SF_ShopVi;
        serviceTestStatus = PropertiesUtil.getEnvironmentData("serviceTestStatus");
        generate = new DataGenerator();
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser, headless);
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
//        if (driver != null) driver.quit();
    }

    public CreateServicePage loginDbAndGoToCreateServicePage() throws Exception {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName, passWord);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToCreateServicePage();
        return new CreateServicePage(driver);
    }

    public void checkPermisionCreateSVByPackage(String userName, boolean isPermission) throws Exception {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName, passWordTestPermission);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        serviceManagement = new ServiceManagementPage(driver);
        if (isPermission) {
            serviceManagement.goToCreateServicePage();
            createService = new CreateServicePage(driver);
            createService.createServiceWhenHasPermission()
                    .clickCloseBTNOnNotificationPopup();
        } else {
            serviceManagement.checkSalePitchWhenNoPermision();
        }
        home = new HomePage(driver);
        home.clickLogout();
    }

    public ServiceManagementPage loginAndNavigateToServiceManagement() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName, passWord);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        return new ServiceManagementPage(driver);
    }
    public void checkPermisionUpdateSVByPackage(String userName, boolean isPermission) throws Exception {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName, passWordTestPermission);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        serviceManagement = new ServiceManagementPage(driver);
        if (isPermission) {
            serviceManagement.goToEditService("");
            createService = new CreateServicePage(driver);
            createService.updateServiceWhenHasPermission().navigateToPageByURL();
        } else {
            serviceManagement.checkSalePitchWhenNoPermision();
        }
        home = new HomePage(driver);
        commonAction.sleepInMiliSecond(1000);
        home.clickLogout();
    }
    @Test()
    public void CS01_VerifyText() throws Exception {
        testCaseId = "CS01";
        createService = loginDbAndGoToCreateServicePage();
        createService.verifyTextOfPage();
    }

    @Test()
    public void CS02_CheckValidate() throws Exception {
        testCaseId = "CS02";
        createService = loginDbAndGoToCreateServicePage();
        createService.checkErrorSaveWithBlankField()
                .checkErrorWhenInputListingPriceOutOfRange()
                .checkErrorWhenInputSellingPriceOutOfRange()
                .checkMaximumCharacterForServiceNameField()
                .checkMaximumCharacterForSEOTitleField()
                .checkMaximumCharacterForSEODescriptionField();
    }

    @Test()
    public void CS03_CreateNormalService() throws Exception {
        testCaseId = "CS03";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description);
        selectedCollection = createService.getSelectedCollection();
        createService.uploadImages(images)
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
        serviceEdit = serviceName; //use for edit testcase.
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF)
                .searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyLocations(locations)
                .verifyTimeSlots(timeSlots)
                .verifyBookNowAndAddToCartButtonDisplay()
                .verifyServiceDescription(description)
                .verifySEOInfo("", "", "", serviceName, description)
                .verifyCollectionLink(selectedCollection.size(), selectedCollection)
                .verifyServiceImagesDisplay()
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(sfAllServicesTxt)
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test()
    public void CS04_CreateServiceBelongTo1Collection() throws Exception {
        testCaseId = "CS04";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description)
                .inputCollections(1);
        selectedCollection = createService.getSelectedCollection();
        createService.uploadImages(images)
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
        serviceDelete = serviceName;
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF)
                .searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyLocations(locations)
                .verifyTimeSlots(timeSlots)
                .verifyBookNowAndAddToCartButtonDisplay()
                .verifyServiceDescription(description)
                .verifyCollectionLink(selectedCollection.size(), selectedCollection)
                .verifyServiceImagesDisplay()
                .verifySEOInfo("", "", "", serviceName, description)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test()
    public void CS05_CreateServiceBelongToMultipleCollections() throws Exception {
        testCaseId = "CS05";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description)
                .inputCollections(2);
        selectedCollection = createService.getSelectedCollection();
        createService.uploadImages(images)
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
        serviceDelete = serviceDelete;
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF)
                .searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyLocations(locations)
                .verifyTimeSlots(timeSlots)
                .verifyBookNowAndAddToCartButtonDisplay()
                .verifyServiceDescription(description)
                .verifyServiceImagesDisplay()
                .verifyCollectionLink(selectedCollection.size(), selectedCollection)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(sfAllServicesTxt)
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test
    public void CS06_CreateListingPriceService() throws Exception {
        testCaseId = "CS06";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.checkOnShowAsListingService()
                .inputServiceDescription(description)
                .inputCollections(1);
        selectedCollection = createService.getSelectedCollection();
        createService.uploadImages(images)
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF)
                .searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, "")
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyBookNowAndAddToCartButtonNotDisplay()
                .verifyPriceNotDisplay()
                .verifyContactNowButtonDisplay()
                .verifyServiceDescription(description)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyListingServiceDisplayInList(serviceName);
    }

    @Test
    public void CS07_CreateServiceWithSEOInfo() throws Exception {
        testCaseId = "CS07";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description)
                .inputCollections(1);
        selectedCollection = createService.getSelectedCollection();
        SEOUrl = "serviceseourl" + generate.generateNumber(5);
        createService.uploadImages(images)
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .inputSEOTitle(SEOTitle)
                .inputSEODescription(SEODesctiption)
                .inputSEOKeyword(SEOKeyword)
                .inputSEOUrl(SEOUrl)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF)
                .searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyLocations(locations)
                .verifyTimeSlots(timeSlots)
                .verifyBookNowAndAddToCartButtonDisplay()
                .verifyServiceDescription(description)
                .verifyCollectionLink(selectedCollection.size(), selectedCollection)
                .verifyServiceImagesDisplay()
                .verifySEOInfo(SEOTitle, SEODesctiption, SEOKeyword, serviceName, description)
                .verifyNavigateToServiceDetailBySEOUrl(SF_URL, SEOUrl, serviceName)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test
    public void CS08_CreateServiceWithHTMLDescription() throws Exception {
        testCaseId = "CS08";
        //create service
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .uncheckOnShowAsListingService()
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uploadImages(images)
                .clickCodeViewInDescription()
                .inputDescriptionAsHTMLFormat(htmlDescription)
                .clickCodeViewInDescription()
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
        //check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyDescriptionAsHTMLFormat(htmlDescription);
    }

    @Test
    public void CS09_CheckServiceQuantity() throws Exception {
        testCaseId = "CS09";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description);
        selectedCollection = createService.getSelectedCollection();
        createService.uploadImages(images)
                .inputLocations("quan1")
                .inputTimeSlots("10:10")
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage()
                .clickCloseBTNOnNotificationPopup()
                .clickOnEditNewestService();
        int serviceId = createService.getServiceId();
        createService = new CreateServicePage(driver);
        createService.checkStockInventoryInDatabase(serviceId, 1000000);
    }

    @Test
    public void CS10_CheckCreateServiceWithMaximumVariation() throws Exception {
        testCaseId = "CS10";
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .uncheckOnShowAsListingService()
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uploadImages(images)
                .inputServiceDescription(description);
        createService.inputRandomLocations(11)
                .verifyMaximumErrorLocation()
                .inputLocations("")
                .inputRandomTimes(49)
                .verifyMaximumErrorTime()
                .inputTimeSlots("")
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage();
    }

    @Test
    public void CS11_CheckCreateSVPermision() throws Exception {
        testCaseId = "CS11";
        checkPermisionCreateSVByPackage(AccountTest.ADMIN_USERNAME_GOWEB, true);
        checkPermisionCreateSVByPackage(AccountTest.ADMIN_USERNAME_GOAPP, true);
        checkPermisionCreateSVByPackage(AccountTest.ADMIN_USERNAME_GOLEAD, false);
        checkPermisionCreateSVByPackage(AccountTest.ADMIN_USERNAME_GOPOS, false);
        checkPermisionCreateSVByPackage(AccountTest.ADMIN_USERNAME_GOSOCIAL, false);
    }

    @Test
    public void ES01_EditTranslation() throws Exception {
        testCaseId = "ES01";
        createService = loginAndNavigateToServiceManagement()
                .goToEditService(serviceEdit)
                .clickEditTranslation();
        String name = createService.getNameTranslate() + "updated en";
        String description = createService.getDescriptionTranslate() + "updated en";
        createService.inputNameTranslate(name)
                .inputDescriptionTranslate(description);
        List<String> locations = createService.inputLocationsTranslate();
        createService.clickSaveTranslateBTN()
                .verifyUpdateTranslateSuccessfulMessage();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("ENG")
                .searchWithFullName(name)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(name)
                .verifyServiceDescription(description)
                .verifyLocations(locations.toArray(new String[locations.size()]));
    }

    @Test
    public void ES02_AddEditRemoveSEOInfo() throws Exception {
        testCaseId = "ES02";
        createService = loginAndNavigateToServiceManagement()
                .goToEditService(serviceEdit);
        //add SEO info
        SEOTitle = "SEO title " + generate.generateString(5);
        SEODesctiption = "SEO description " + generate.generateString(5);
        SEOKeyword = "SEO keyword " + generate.generateString(5);
        SEOUrl = "serviceseourl" + generate.generateNumber(5);
        String SEOTitleTranslate = SEOTitle + " en";
        String SEODesctiptionTranslate = SEODesctiption + " en";
        String SEOKeywordTranslate = SEOKeyword + " en";
        String SEOUrlTranslate = SEOUrl + "en";
        createService.inputSEOTitle(SEOTitle)
                .inputSEODescription(SEODesctiption)
                .inputSEOKeyword(SEOKeyword)
                .inputSEOUrl(SEOUrl)
                .clickSaveBtn().verifyUpdateServiceSuccessfully()
                .clickCloseBTNOnNotificationPopup();
                new CreateServicePage(driver).clickEditTranslation()
                .inputSEOTitleTranslate(SEOTitleTranslate)
                .inputSEODescriptionTranslate(SEODesctiptionTranslate)
                .inputSEOKeywordTranslate(SEOKeywordTranslate)
                .inputSEOUrlTranslate(SEOUrlTranslate)
                .clickSaveTranslateBTN()
                .verifyUpdateTranslateSuccessfulMessage()
                .clickCloseBTNOnNotificationPopup();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("VIE")
                .searchWithFullName(serviceEdit)
                .waitDotLoadingDisappear();
        new HeaderSF(driver).clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifySEOInfo(SEOTitle, SEODesctiption, SEOKeyword, "", "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("ENG");
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifySEOInfo(SEOTitleTranslate, SEODesctiptionTranslate, SEOKeywordTranslate, "", "");
        //edit seo
        home = new HomePage(driver);
        home.navigateToPageByURL().waitTillSpinnerDisappear().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver).goToEditService(serviceEdit);
        SEOTitle = "SEO title update " + generate.generateString(5);
        SEODesctiption = "SEO description update " + generate.generateString(5);
        SEOKeyword = "SEO keyword update " + generate.generateString(5);
        SEOUrl = "serviceseourlupdate" + generate.generateNumber(5);
        SEOTitleTranslate = SEOTitle + " update en";
        SEODesctiptionTranslate = SEODesctiption + " update en";
        SEOKeywordTranslate = SEOKeyword + " update en";
        SEOUrlTranslate = SEOUrl + "updateen";
        createService.inputSEOTitle(SEOTitle)
                .inputSEODescription(SEODesctiption)
                .inputSEOKeyword(SEOKeyword)
                .inputSEOUrl(SEOUrl)
                .clickSaveBtn().verifyUpdateServiceSuccessfully()
                .clickCloseBTNOnNotificationPopup();
                new CreateServicePage(driver).clickEditTranslation()
                .inputSEOTitleTranslate(SEOTitleTranslate)
                .inputSEODescriptionTranslate(SEODesctiptionTranslate)
                .inputSEOKeywordTranslate(SEOKeywordTranslate)
                .inputSEOUrlTranslate(SEOUrlTranslate)
                .clickSaveTranslateBTN()
                .verifyUpdateTranslateSuccessfulMessage()
                .clickCloseBTNOnNotificationPopup();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("VIE")
                .searchWithFullName(serviceEdit)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifySEOInfo(SEOTitle, SEODesctiption, SEOKeyword, "", "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("ENG");
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifySEOInfo(SEOTitleTranslate, SEODesctiptionTranslate, SEOKeywordTranslate, "", "");
        //delete seo
        home = new HomePage(driver);
        home.navigateToPageByURL().waitTillSpinnerDisappear().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver).goToEditService(serviceEdit);
        String description = createService.getServiceDescription();
        createService.inputSEOTitle("")
                .inputSEODescription("")
                .inputSEOKeyword("")
                .inputSEOUrl("")
                .clickSaveBtn().verifyUpdateServiceSuccessfully()
                .clickCloseBTNOnNotificationPopup();
        new CreateServicePage(driver).clickEditTranslation();
        String descriptionTranslate = createService.getDescriptionTranslate();
        String serviceNameTranslate = createService.getNameTranslate();
        createService.inputSEOTitleTranslate("")
                .inputSEODescriptionTranslate("")
                .inputSEOKeywordTranslate("")
                .inputSEOUrlTranslate("")
                .clickSaveTranslateBTN()
                .verifyUpdateTranslateSuccessfulMessage()
                .clickCloseBTNOnNotificationPopup();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("VIE")
                .searchWithFullName(serviceEdit)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifySEOInfo("", "", "", serviceEdit, description);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("ENG");
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifySEOInfo("", "", "", serviceNameTranslate, descriptionTranslate);
    }

    @Test
    public void ES03_ActiveDeactiveService() throws Exception {
        testCaseId = "ES03";
        //update deactive
        loginAndNavigateToServiceManagement()
                .goToEditService(serviceTestStatus)
                .updateServiceStatus("inactive")
                .verifyUpdateServiceSuccessfully()
                .clickCloseBTNOnNotificationPopup();
        createService = new CreateServicePage(driver);
        createService.verifyServiceStatusInactive();
        String url = createService.getLivePreviewLink();
        sellingPrice = createService.getSellingPrice();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(url)
                .checkPageNotFound(SF_URL)
                .navigateToURL(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.searchWithFullName(serviceTestStatus)
                .verifySearchNotFound(serviceTestStatus);
        //update into active
        home = new HomePage(driver);
        home.navigateToPageByURL().waitTillSpinnerDisappear().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver).goToEditService(serviceTestStatus)
                .updateServiceStatus("active")
                .verifyUpdateServiceSuccessfully()
                .clickCloseBTNOnNotificationPopup();
        createService = new CreateServicePage(driver);
        createService.verifyServiceStatusActive();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(url);
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceTestStatus);
        new GeneralSF(driver).navigateToURL(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.searchWithFullName(serviceTestStatus)
                .verifySearchSuggestion(serviceTestStatus, sellingPrice);
    }

    @Test
    public void ES04_UpdateServiceInfor() throws Exception {
        testCaseId = "ES04";
        //create
        createService = loginDbAndGoToCreateServicePage();
        serviceName = "Automation Service SV" + generate.generateString(10);
        listingPrice = "2" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description)
                .uploadImages(images)
                .inputLocations(locations)
                .inputTimeSlots(timeSlots)
                .clickSaveBtn()
                .verifyCreateSeviceSuccessfulMessage()
                .clickCloseBTNOnNotificationPopup()
                .goToEditService(serviceName);
        //edit
        serviceName = "Automation update name " + generate.generateString(5);
        description = "Automation update description " + generate.generateString(5);
        listingPrice = "3" + generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputServiceDescription(description)
                .inputListingPrice(listingPrice);
        sellingPrice = createService.inputSellingPrice(listingPrice, generate.generateNumber(2));
        createService.clickSaveBtn().verifyUpdateServiceSuccessfully();
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        new GeneralSF(driver).waitTillLoaderDisappear();
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("VIE")
                .searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyServiceDescription(description);
    }

    @Test
    public void ES05_AddRemovePhotoForService() throws Exception {
        testCaseId = "ES05";
        //add more image
        createService = loginAndNavigateToServiceManagement()
                .goToEditService(serviceEdit)
                .uploadImages(imagesAddMore);
        int imageSize = createService.getImageListSize();
        sellingPrice = createService.getSellingPrice();
        createService.clickSaveBtn()
                .verifyUpdateServiceSuccessfully();
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon()
                .searchWithFullName(serviceEdit)
                .verifySearchSuggestion(serviceEdit, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceListSize(imageSize);
        //delete image
        home = new HomePage(driver);
        home.navigateToPageByURL().waitTillSpinnerDisappear().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver).goToEditService(serviceEdit)
                .removeAllImages()
                .uploadImages(images)
                .clickSaveBtn();
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon()
                .searchWithFullName(serviceEdit)
                .verifySearchSuggestion(serviceEdit, sellingPrice)
                .clickSearchResult();
        serviceDetailPage = new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceListSize(images.length);
    }

    @Test
    public void ES06_AddRemoveCollectionForService() throws Exception {
        testCaseId = "ES06";
        createService = loginAndNavigateToServiceManagement()
                .goToEditService(serviceEdit)
                .inputCollections(2);
        selectedCollection = createService.getSelectedCollection();
        createService.clickSaveBtn().verifyUpdateServiceSuccessfully();
        for (String collectionName : selectedCollection) {
            new HomePage(driver).navigateToPageByURL().navigateToPage("Services", "Service Collections");
            ServiceCollectionManagement serCollection = new ServiceCollectionManagement(driver);
            serCollection.goToEditServiceCollection(collectionName)
                    .verifyServiceShowInServiceList(serviceEdit);
        }
        new HomePage(driver).navigateToPageByURL().navigateToPage("Services");
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToEditService(serviceEdit)
                .removeAllCollection()
                .clickSaveBtn().verifyUpdateServiceSuccessfully();
        for (String collectionName : selectedCollection) {
            new HomePage(driver).navigateToPageByURL().navigateToPage("Services", "Service Collections");
            ServiceCollectionManagement serCollection = new ServiceCollectionManagement(driver);
            serCollection.goToEditServiceCollection(collectionName)
                    .verifyServiceNotShowInServiceList(serviceEdit);
        }
    }
    @Test
    public void ES07_DeleteServiceInServiceDetail() throws Exception {
        testCaseId = "ES07";
        loginAndNavigateToServiceManagement()
                .goToEditService(serviceDelete)
                .clickDeleteService()
                .verifyDeleteConfirmMessage()
                .clickOKBtnOnConfirmPopup()
                .verifyDeleteSuccessfullyMessage()
                .clickCloseBTNOnNotificationPopup()
                .verifyServiceNotDisplayInList(serviceDelete);
    }
    @Test
    public void ES08_CheckEditSVPermision() throws Exception {
        testCaseId = "ES08";
        checkPermisionUpdateSVByPackage(AccountTest.ADMIN_USERNAME_GOWEB, true);
        checkPermisionUpdateSVByPackage(AccountTest.ADMIN_USERNAME_GOAPP, true);
        checkPermisionUpdateSVByPackage(AccountTest.ADMIN_USERNAME_GOLEAD, false);
        checkPermisionUpdateSVByPackage(AccountTest.ADMIN_USERNAME_GOPOS, false);
        checkPermisionUpdateSVByPackage(AccountTest.ADMIN_USERNAME_GOSOCIAL, false);
    }
}
