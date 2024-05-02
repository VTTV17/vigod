package web.Dashboard.reservation.POSReservation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.home.HomePageElement;
import web.Dashboard.service.ServiceManagementPage;

import java.util.Arrays;
import java.util.List;

public class POSCreateReservation extends POSReservationElement{
    WebDriver driver;
    UICommonAction commons;
    AssertCustomize assertCustomize;
    AllPermissions allPermissions;
    LoginInformation loginInformation;
    HomePageElement homePageEl;
    String urlPOSService = "/reservation/instore-purchase";

    final static Logger logger = LogManager.getLogger(POSCreateReservation.class);
    public POSCreateReservation(WebDriver driver, LoginInformation loginInformation) {
        super(driver);
        this.driver = driver;
        this.loginInformation = loginInformation;
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        homePageEl = new HomePageElement(driver);
    }
    public POSCreateReservation navigate(){
        commons.navigateToURL(Links.DOMAIN+urlPOSService);
        logger.info("Navigate to "+urlPOSService);
        commons.sleepInMiliSecond(1000);
        return this;
    }
    public POSCreateReservation clickOnSearchService(){
        commons.click(loc_blkSearchService);
        logger.info("Click on Search service.");
        return this;
    }
    public POSCreateReservation selectFirstService(){
        commons.click(loc_lst_lblServiceSuggestion,0);
        logger.info("Select first service name in suggestion list.");
        return this;
    }
    public POSCreateReservation selectLocation(String...location){
        if(location.length>0)
            commons.selectByVisibleText(loc_ddlSelectLocator, Arrays.toString(location));
        else commons.selectByIndex(loc_ddlSelectLocator,1);
        logger.info("Selected location.");
        return this;
    }
    public POSCreateReservation selectTimeSlot(String...timeslot){
        if(timeslot.length>0)
            commons.selectByVisibleText(loc_ddlSelectTime, Arrays.toString(timeslot));
        else commons.selectByIndex(loc_ddlSelectTime,1);
        logger.info("Selected time slot.");
        return this;
    }
    public POSCreateReservation inputQuantity(String quantity){
        commons.inputText(loc_txtQuantity,quantity);
        logger.info("Input quantity: "+quantity);
        return this;
    }
    public POSCreateReservation clickOnAddBtnOnServiceInfo(){
        commons.click(loc_dlgServiceInformatiion_btnAdd);
        logger.info("Click on Add button.");
        return this;
    }
    public POSCreateReservation inputToSearchCustomer(String customer){
        commons.inputText(loc_txtSearchCustomer,customer);
        logger.info("Input to search customer: "+customer);
        commons.sleepInMiliSecond(1000);
        return this;
    }
    public POSCreateReservation selectCustomerSuggestion(String customer){
        List<WebElement> customerSuggestions = commons.getElements(loc_lst_lblCustomerName);
        boolean isClicked = false;
        for(WebElement el: customerSuggestions){
            if(commons.getText(el).equalsIgnoreCase(customer)){
                commons.clickElement(el);
                isClicked = true;
                break;
            }
        }
        if(!isClicked){
            try {
                throw new Exception("Customer '%s' not found.".formatted(customer));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }
    public POSCreateReservation selectFistServiceAndAddToCart(){
        clickOnSearchService();
        commons.sleepInMiliSecond(200);
        selectFirstService();
        selectLocation();
        selectTimeSlot();
        clickOnAddBtnOnServiceInfo();
        return this;
    }
    public POSCreateReservation clickOnAddCustomer(){
        commons.click(loc_icnAddCustomer);
        return this;
    }
    public POSCreateReservation inputFullName(String fullName){
        commons.inputText(loc_dlgCreateCustomer_txtFullName,fullName);
        return this;
    }
    public POSCreateReservation inputPhoneNumber(String phoneNumber,String...phoneCode){
        if(phoneCode.length>0){
            //select phone code
            commons.click(loc_dlgCreateCustomer_btnPhoneCode);
            List<WebElement> phoneCodeEl = commons.getElements(loc_dlgCreateCustomer_lstPhoneCode);
            boolean isClicked = false;
            for (WebElement el: phoneCodeEl) {
                if(commons.getText(el).equalsIgnoreCase(phoneCode[0])){
                    commons.clickElement(el);
                    isClicked = true;
                    break;
                }
            }
            if(!isClicked){
                try {
                    throw new Exception("Phone code not found, so can't click it.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //input phone number
        commons.inputText(loc_dlgCreateCustomer_txtPhoneNumber,phoneNumber);
        return this;
    }
    public POSCreateReservation clickOnAddBtnOnCreateCustomer(){
        commons.click(loc_dlgCreateCustomer_btnAdd);
        return this;
    }
    public POSCreateReservation checkPermissionViewServiceList(){
        clickOnSearchService();
        commons.sleepInMiliSecond(500);
        List<WebElement> suggestionList = commons.getListElement(loc_lst_lblServiceSuggestion);
        if(allPermissions.getService().getServiceManagement().isViewListService()){
            logger.info("Check staff has View service list permission.");
            assertCustomize.assertTrue(suggestionList.size()>0,"[Failed]Sugesstion list not show or empty.");
            selectFistServiceAndAddToCart();
            clickOnCreateReservation();
            new HomePage(driver).waitTillLoadingDotsDisappear();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("reservations.pos.create.successfullMessage"),
                        "[Failed] Create successfull toast message should be shown, but '%s' message is shown.".formatted(toastMessage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            logger.info("Check staff has not View service list permission.");
            assertCustomize.assertTrue(suggestionList.isEmpty(),
                    "[Failed] All service suggestion should be not shown, but found: %s.".formatted(suggestionList.toString()));
        }
        logger.info("Verified permission service list in suggestion.");
        return this;
    }
    public POSCreateReservation checkPermissionViewCustomer(String customerNameAssignedStaff, String customerNameNotAssignedStaff){
        if(allPermissions.getCustomer().getCustomerManagement().isViewAllCustomerList()){
            logger.info("Check customer list when staff has View all customer permission.");
            inputToSearchCustomer(customerNameNotAssignedStaff);
            List<WebElement> customerSuggestionList = commons.getElements(loc_lst_lblCustomerName);
            assertCustomize.assertTrue(customerSuggestionList.size()>0,
                    "[Failed] All customer not show.");
        }else if(allPermissions.getCustomer().getCustomerManagement().isViewAssignedCustomerList()){
            logger.info("Check customer list when staff has View Assigned customer list.");
            inputToSearchCustomer(customerNameAssignedStaff);
            List<WebElement> customerSuggestionList = commons.getElements(loc_lst_lblCustomerName);
            assertCustomize.assertTrue(customerSuggestionList.size()>0,
                    "[Failed] Customer assigned: '%s' not show.".formatted(customerNameAssignedStaff));
        }else {
            logger.info("Check Customer list when staff doesn't have View customer permission.");
            inputToSearchCustomer(customerNameAssignedStaff);
            List<WebElement> customerSuggestionList = commons.getElements(loc_lst_lblCustomerName);
            assertCustomize.assertTrue(customerSuggestionList.size()==0,
                    "[Failed] Don't have permission View customer list but customer still show.".formatted(customerNameAssignedStaff));
        }
        return this;
    }
    public POSCreateReservation checkPermissionCreateCustomer(){
        clickOnAddCustomer();
        String phoneRandom = new DataGenerator().randomVNPhone();
        inputFullName( new DataGenerator().generateString(6));
        inputPhoneNumber(phoneRandom);
        if(allPermissions.getCustomer().getCustomerManagement().isAddCustomer()){
            logger.info("Check staff has Add customer permission.");
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_dlgCreateCustomer_btnAdd,homePageEl.loc_lblToastMessage),
                    "[Failed] Toast message should be shown");
        }else {
            logger.info("Check staff has not Add customer permission.");
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_dlgCreateCustomer_btnAdd),
                    "Restricted popup should be shown when click on Add customer");
        }
        return this;
    }
    public POSCreateReservation clickOnDiscount(){
        commons.click(loc_btnDiscount);
        logger.info("Click on Discount.");
        return this;
    }
    public POSCreateReservation inputDiscountCode(String discount){
        commons.click(loc_ddlDiscountType);
        commons.click(loc_ddvDiscountCode);
        commons.inputText(loc_txtDiscountCode,discount);
        logger.info("Input discount code: "+discount);
        return this;
    }
    public POSCreateReservation inputDiscountAmount(String discount){
        commons.click(loc_ddlDiscountType);
        commons.click(loc_ddvDiscountAmount);
        commons.inputText(loc_txtDiscountAmount,discount);
        logger.info("Input discount amount.");
        return this;
    }
    public POSCreateReservation inputDiscountPercent(String discount){
        commons.click(loc_ddlDiscountType);
        commons.click(loc_ddvDiscountPercent);
        commons.inputText(loc_txtDiscountPercent,discount);
        logger.info("Input discount percent.");
        return this;
    }
    public POSCreateReservation checkPermissionAddDirectDiscount(){

        if(allPermissions.getReservation().getPOSService().isCreateReservation()&&allPermissions.getService().getServiceManagement().isViewListService()){
            navigate();
            selectFistServiceAndAddToCart();
            clickOnDiscount();
            //check permission discount amount
            inputDiscountAmount("1000");
            if(allPermissions.getReservation().getPOSService().isApplyDirectDiscount()) {
                commons.click(loc_btnApply);
                assertCustomize.assertTrue(commons.isElementNotDisplay(loc_dlgDiscount),
                        "[Failed] Discount popup should not be shown.");
                assertCustomize.assertFalse(new CheckPermission(driver).isAccessRestrictedPresent(),
                        "[Failed] Restricted pop should not be shown.");
            }
            else
                new CheckPermission(driver).checkAccessRestricted(loc_btnApply);
            //check permission discount percent
            navigate();
            selectFistServiceAndAddToCart();
            clickOnDiscount();
            inputDiscountPercent("10");
            if(allPermissions.getReservation().getPOSService().isApplyDirectDiscount()) {
                commons.click(loc_btnApply);
                assertCustomize.assertTrue(commons.isElementNotDisplay(loc_dlgDiscount),
                        "[Failed] Discount popup should not be shown.");
                assertCustomize.assertFalse(new CheckPermission(driver).isAccessRestrictedPresent(),
                        "[Failed] Restricted pop should not be shown.");
            }
            else
                new CheckPermission(driver).checkAccessRestricted(loc_btnApply);
            logger.info("Verified Add direct discount permission.");
        }else
            logger.info("Don't has permission Create Booking and view service list, so no need check add direct discount permission.");
        return this;
    }
    public POSCreateReservation checkPermissionDiscountCode(){
        if(allPermissions.getReservation().getPOSService().isCreateReservation()&&allPermissions.getService().getServiceManagement().isViewListService()){
            navigate();
            selectFistServiceAndAddToCart();
            clickOnDiscount();
            //check permission discount code
            inputDiscountCode("test");
            if(allPermissions.getReservation().getPOSService().isApplyDiscountCode())
               assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnApply, loc_lblError),"[Failed] Discount Invalid Error should be shown");
            else
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnApply),"[Failed] Restricted popup should be shown when click on Apply button") ;
            logger.info("Verified Add discount code permission.");
        }else
            logger.info("Don't has permission Create Booking and view service list, so no need check add discount code permission.");
        return this;
    }
    public POSCreateReservation checkPermissionCreateReservation(String customerNameAssignedStaff, String customerNameNotAssignedStaff){
        if(allPermissions.getReservation().getPOSService().isCreateReservation()){
            navigate();
            checkPermissionViewServiceList();
            checkPermissionViewCustomer(customerNameAssignedStaff,customerNameNotAssignedStaff);
            checkPermissionCreateCustomer();
        }else
            new CheckPermission(driver).checkAccessRestricted(Links.DOMAIN+urlPOSService);
        return this;
    }
    public POSCreateReservation checkPermissionPOSService(AllPermissions allPermissions, String customerNameAssignedStaff, String customerNameNotAssignedStaff) {
        this.allPermissions = allPermissions;
        checkPermissionCreateReservation(customerNameAssignedStaff,customerNameNotAssignedStaff);
        checkPermissionAddDirectDiscount();
        checkPermissionDiscountCode();
        return this;
    }
    public POSCreateReservation completeVerifyStaffPermissionPOSService() {
        logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
        if (assertCustomize.getCountFalse() > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
        }
        return this;
    }
    public POSCreateReservation clickOnCreateReservation(){
        commons.click(loc_btnCreateReservation);
        logger.info("Click on Create Reservation button.");
        return this;
    }

}
