import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.service.CreateServicePage;
import pages.dashboard.service.ServiceManagementPage;
import pages.storefront.header.HeaderSF;
import pages.storefront.services.CollectionSFPage;
import pages.storefront.services.ServiceDetailPage;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;

import java.io.IOException;
import java.util.List;

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
    String serviceName = "";
    String listingPrice = "";
    String sellingPrice = "";
    String description = "Service description";
    List<String> selectedCollection = null;
    String[] images = {FILE_IMAGE_1, FILE_IMAGE_2};
    String[] locations = {"Thu Duc", "Quan 9", "Quan 1", "Quan 2"};
    String[] timeSlots = {"20:00", "21:00", "22:30"};
    String SEOTitle = "SEO title service";
    String SEODesctiption = "SEO description service";
    String SEOKeyword = "seo keyword";
    String SEOUrl = "";
    String userName;
    String passWord;
    String SF_URL = SF_ShopVi;
    String languageDB;
    String languageSF;
    String sfAllServicesTxt;
    String testCaseId;

    @BeforeClass
    public void beforeClass() throws Exception {
        userName = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passWord = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        languageDB = PropertiesUtil.getLanguageFromConfig("Dashboard");
        languageSF = PropertiesUtil.getLanguageFromConfig("Storefront");
        sfAllServicesTxt = PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.allServicesTxt");
    }

    @AfterMethod
    public void afterMethod(ITestResult result) throws IOException {
        writeResultToExcel(FILE_CREATE_SERVICE_TCS, 0, result, testCaseId);
    }

    public CreateServicePage loginDbAndGoToCreateServicePage() throws Exception {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName, passWord);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_PAGE_NAME);
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToCreateServicePage();
        return new CreateServicePage(driver);
    }

    @Test(priority = 1)
    public void CS01_VerifyText() throws Exception {
        testCaseId = "CS01";
        createService = loginDbAndGoToCreateServicePage();
        createService.verifyTextOfPage();
    }

    @Test(priority = 2)
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

    @Test(priority = 3)
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
    }
    @Test(priority = 4, dependsOnMethods = "CS03_CreateNormalService")
    public void CS04_VerifyServiceOnSF() throws Exception {
        testCaseId = "CS04";
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_ShopVi);
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
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(sfAllServicesTxt)
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test(priority = 5)
    public void CS05_CreateServiceBelongTo1Collection() throws Exception {
        testCaseId = "CS05";
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
    }

    @Test(priority = 6, dependsOnMethods = "CS05_CreateServiceBelongTo1Collection")
    public void CS06_VerifyServiceBelongTo1CollectionOnSF() throws Exception {
        testCaseId = "CS06";
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_ShopVi);
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
                .verifySEOInfo("", "", "", serviceName, description)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test(priority = 7)
    public void CS07_CreateServiceBelongToMultipleCollections() throws Exception {
        testCaseId = "CS07";
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
    }

    @Test(priority = 8, dependsOnMethods = "CS07_CreateServiceBelongToMultipleCollections")
    public void CS08_VerifyServiceBelongToMultipleCollectionsOnSF() throws Exception {
        testCaseId = "CS08";
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
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(sfAllServicesTxt)
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }

    @Test(priority = 9)
    public void CS09_CreateListingPriceService() throws Exception {
        testCaseId = "CS09";
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
    }

    @Test(priority = 10, dependsOnMethods = "CS09_CreateListingPriceService")
    public void CS10_VerifyListingPriceServiceOnSF() throws Exception {
        testCaseId = "CS10";
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_ShopVi);
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

    @Test(priority = 11)
    public void CS11_CreateServiceWithSEOInfo() throws Exception {
        testCaseId = "CS11";
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
    }

    @Test(priority = 12, dependsOnMethods = "CS11_CreateServiceWithSEOInfo")
    public void CS12_VerifyServiceWithSEOInfoOnSF() throws Exception {
        testCaseId = "CS12";
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
                .verifySEOInfo(SEOTitle, SEODesctiption, SEOKeyword, serviceName, description)
                .verifyNavigateToServiceDetailBySEOUrl(SF_ShopVi, SEOUrl, serviceName)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyNewServiceDisplayInList(serviceName, sellingPrice, listingPrice);
    }
}
