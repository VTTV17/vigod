import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.LoginPage;
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
    pages.storefront.LoginPage loginSF;
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
    @Test
    public void CS01_CreateService() {
        login = new LoginPage(driver);
        login.navigate().performLogin("0703618433","Psso124@");
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
   @Test
    public void CS02_VerifyServiceOnSF() throws IOException {
       loginSF = new pages.storefront.LoginPage(driver);
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
    @Test
    public void CS03_CreateListingPriceService() {
        login = new LoginPage(driver);
        login.navigate().performLogin("0703618433","Psso124@");
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
    @Test
    public void CS04_VerifyListingPriceServiceOnSF() throws IOException {
        loginSF = new pages.storefront.LoginPage(driver);
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
    @Test
    public void CS05_CreateServiceBelongToMultipleCollections() {
        login = new LoginPage(driver);
        login.navigate().performLogin("0703618433","Psso124@");
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
       @Test
    public void CS06_VerifyServiceBelongToMultipleCollectionsOnSF() throws IOException {
        loginSF = new pages.storefront.LoginPage(driver);
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
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle("All Services")
                .verifyNewServiceDisplayInList(serviceName,sellingPrice,listingPrice);
    }
    @Test
    public void CS07_CreateServiceWithSEOInfo() {
        login = new LoginPage(driver);
        login.navigate().performLogin("0703618433","Psso124@");
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
    @Test
    public void CS08_VerifyServiceWithSEOInfoOnSF() throws IOException {
        loginSF = new pages.storefront.LoginPage(driver);
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
                .verifySEOInfo(SEOTitle,SEODesctiption,SEOKeyword,serviceName,description)
                .clickOnCollectionLink();
        collectionSFPage = new CollectionSFPage(driver);
        collectionSFPage.verifyCollectionPageTitle(selectedCollection.get(0))
                .verifyNewServiceDisplayInList(serviceName,sellingPrice,listingPrice);
    }
}
