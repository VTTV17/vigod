import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.service.CreateServicePage;
import pages.dashboard.service.ServiceManagementPage;
import pages.storefront.CollectionSFPage;
import pages.storefront.HeaderSF;
import pages.storefront.ServiceDetailPage;

import java.io.IOException;
import java.util.List;

import static utilities.links.Links.*;

public class CreateServiceTest extends BaseTest{
    LoginPage login;
    HomePage home;
    ServiceManagementPage serviceManagement;
    CreateServicePage createService;
    pages.storefront.login.LoginPage loginSF;
    HeaderSF headerSF;
    ServiceDetailPage serviceDetailPage;
    CollectionSFPage collectionSFPage;
    String serviceName= "";
    String listingPrice = "";
    String sellingPrice = "";
    String description="Service description";
    List<String> selectedCollection=null;
    String[] images={"cham-soc-mat.jpg","giam-beo-bung.jpg"};
    String[] locations={"Thu Duc", "Quan 9", "Quan 1", "Quan 2"};
    String[] timeSlots={"20:00","21:00","22:30"};
    String SEOTitle="SEO title service";
    String SEODesctiption="SEO description service";
    String SEOKeyword="seo keyword";
    String SEOUrl="";
    String userName = "0703618433";
    String passWord = "Psso124@";
    String SF_URL = SF_ShopVi;
    @Test (priority = 0)
    public void CS01_CreateService() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName,passWord);
        home =  new HomePage(driver);
        home.waitTillSpinnerDisappear().navigateToPage("Services");
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToCreateServicePage();
        createService = new CreateServicePage(driver);
        serviceName= "Automation Service SV"+ generate.generateString(10);
        listingPrice = "2"+generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice=  createService.inputSellingPrice(listingPrice,generate.generateNumber(2));
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
   @Test(priority = 1,dependsOnMethods = "CS01_CreateService")
    public void CS02_VerifyServiceOnSF() throws IOException {
       loginSF = new pages.storefront.login.LoginPage(driver);
       loginSF.navigate(SF_ShopVi);
       headerSF = new HeaderSF(driver);
       headerSF.searchWithFullName(serviceName)
               .verifySearchSuggestion(serviceName,sellingPrice)
               .clickSearchResult();
       serviceDetailPage= new ServiceDetailPage(driver);
       serviceDetailPage.verifyServiceName(serviceName)
               .verifyListingPrice(listingPrice)
               .verifySellingPrice(sellingPrice)
               .verifyLocations(locations)
               .verifyTimeSlots(timeSlots)
               .verifyBookNowAndAddToCartButtonDisplay()
               .verifyServiceDescription(description)
               .verifyCollectionLink(selectedCollection.size(),selectedCollection)
               .verifySEOInfo("","","",serviceName,description)
               .clickOnCollectionLink();
       collectionSFPage = new CollectionSFPage(driver);
       collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
               .verifyNewServiceDisplayInList(serviceName,sellingPrice,listingPrice);
   }
    @Test(priority = 2)
    public void CS03_CreateListingPriceService() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName,passWord);
        home =  new HomePage(driver);
        home.waitTillSpinnerDisappear().navigateToPage("Services");
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToCreateServicePage();
        createService = new CreateServicePage(driver);
        serviceName= "Automation Service SV"+ generate.generateString(10);
        listingPrice = "2"+generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice=  createService.inputSellingPrice(listingPrice,generate.generateNumber(2));
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
    @Test(priority = 3, dependsOnMethods = "CS03_CreateListingPriceService")
    public void CS04_VerifyListingPriceServiceOnSF() throws IOException {
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_ShopVi);
        headerSF = new HeaderSF(driver);
        headerSF.searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName,"")
                .clickSearchResult();
        serviceDetailPage= new ServiceDetailPage(driver);
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
    @Test (priority = 4)
    public void CS05_CreateServiceBelongToMultipleCollections() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName,passWord);
        home =  new HomePage(driver);
        home.waitTillSpinnerDisappear().navigateToPage("Services");
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToCreateServicePage();
        createService = new CreateServicePage(driver);
        serviceName= "Automation Service SV"+ generate.generateString(10);
        listingPrice = "2"+generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice=  createService.inputSellingPrice(listingPrice,generate.generateNumber(2));
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
       @Test(priority = 5, dependsOnMethods = "CS05_CreateServiceBelongToMultipleCollections")
    public void CS06_VerifyServiceBelongToMultipleCollectionsOnSF() throws IOException {
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName,sellingPrice)
                .clickSearchResult();
        serviceDetailPage= new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyLocations(locations)
                .verifyTimeSlots(timeSlots)
                .verifyBookNowAndAddToCartButtonDisplay()
                .verifyServiceDescription(description)
                .verifyCollectionLink(selectedCollection.size(),selectedCollection)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle("All Services")
                .verifyNewServiceDisplayInList(serviceName,sellingPrice,listingPrice);
    }
    @Test (priority = 6)
    public void CS07_CreateServiceWithSEOInfo() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userName,passWord);
        home =  new HomePage(driver);
        home.waitTillSpinnerDisappear().navigateToPage("Services");
        serviceManagement = new ServiceManagementPage(driver);
        serviceManagement.goToCreateServicePage();
        createService = new CreateServicePage(driver);
        serviceName= "Automation Service SV"+ generate.generateString(10);
        listingPrice = "2"+generate.generateNumber(5);
        createService.inputServiceName(serviceName)
                .inputListingPrice(listingPrice);
        sellingPrice=  createService.inputSellingPrice(listingPrice,generate.generateNumber(2));
        createService.uncheckOnShowAsListingService()
                .inputServiceDescription(description)
                .inputCollections(1);
        selectedCollection = createService.getSelectedCollection();
        SEOUrl= "serviceseourl"+generate.generateNumber(5);
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
    @Test(priority = 7, dependsOnMethods = "CS07_CreateServiceWithSEOInfo")
    public void CS08_VerifyServiceWithSEOInfoOnSF() throws IOException {
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(SF_URL);
        headerSF = new HeaderSF(driver);
        headerSF.searchWithFullName(serviceName)
                .verifySearchSuggestion(serviceName,sellingPrice)
                .clickSearchResult();
        serviceDetailPage= new ServiceDetailPage(driver);
        serviceDetailPage.verifyServiceName(serviceName)
                .verifyListingPrice(listingPrice)
                .verifySellingPrice(sellingPrice)
                .verifyLocations(locations)
                .verifyTimeSlots(timeSlots)
                .verifyBookNowAndAddToCartButtonDisplay()
                .verifyServiceDescription(description)
                .verifyCollectionLink(selectedCollection.size(),selectedCollection)
                .verifySEOInfo(SEOTitle,SEODesctiption,SEOKeyword,serviceName,description)
                .verifyNavigateToServiceDetailBySEOUrl(SF_ShopVi,SEOUrl,serviceName)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyNewServiceDisplayInList(serviceName,sellingPrice,listingPrice);
    }
}
