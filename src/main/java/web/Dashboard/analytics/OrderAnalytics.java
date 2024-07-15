package web.Dashboard.analytics;

import api.Seller.customers.APIAllCustomers;
import api.Seller.login.Login;
import api.Seller.orders.pos.APICreateOrderPOS;
import api.Seller.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.api.API;
import utilities.assert_customize.AssertCustomize;
import utilities.enums.analytics.FilterType;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderAnalytics {

	final static Logger logger = LogManager.getLogger(OrderAnalytics.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    LoginInformation sellerLoginInfo, staffLoginInfo;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    String url = Links.DOMAIN+"/analytics/order";
    public OrderAnalytics(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public OrderAnalytics getLoginInfo(LoginInformation sellerLoginInfo, LoginInformation staffLoginInfo) {
        this.sellerLoginInfo = sellerLoginInfo;
        this.staffLoginInfo = staffLoginInfo;
        return this;
    }

    By loc_lnkRefresh = By.cssSelector(".time-frame-wrapper [href='#']");
    By loc_spnRefreshSpinner = By.xpath("//span[contains(@class,'spinner-border') and not(@hidden)]");
    By loc_lblOrderNumber = By.xpath("(//div[@class='metric-item'])[1]//div[@class='value']/span");
    By loc_lst_btnFilterType = By.cssSelector(".type-options .type-item");
    By loc_ddlAllBranch = By.cssSelector(".order-analytics-filter-by-type .component-select-wrapper");
    By loc_ddvBranchOptions = By.cssSelector(".order-analytics-filter-by-type .option");
    public OrderAnalytics clickRefresh() {
    	commonAction.click(loc_lnkRefresh);
    	logger.info("Clicked on 'Refresh' link text.");
    	commonAction.waitVisibilityOfElementLocated(loc_spnRefreshSpinner);
        assertCustomize = new AssertCustomize(driver);
    	return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToUseOrderAnalytics(String permission) {
		if (permission.contentEquals("A")) {
			clickRefresh();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/       
    public void selectFilterType(FilterType type){
        switch (type){
            case ALL -> commonAction.click(loc_lst_btnFilterType,0);
            case BRANCH -> commonAction.click(loc_lst_btnFilterType,1);
            case PLATFORMS -> commonAction.click(loc_lst_btnFilterType,2);
            case STAFF -> commonAction.click(loc_lst_btnFilterType,3);
            case PARTNER -> commonAction.click(loc_lst_btnFilterType,4);
            case PAYMENT_METHOD -> commonAction.click(loc_lst_btnFilterType,5);
        }
        logger.info("Select filter type: "+type);
    }
    public Integer getOrdersMetric(){
        String orderNumeric = commonAction.getText(loc_lblOrderNumber);
        logger.info("Orders metric: "+orderNumeric);
        return Integer.parseInt(orderNumeric);
    }
    public List<String> getBranchOptions(){
        commonAction.click(loc_ddlAllBranch);
        List<WebElement> optionElementList = commonAction.getElements(loc_ddvBranchOptions,1);
        List<String> options = new ArrayList<>();
        for (int i = 1;i<optionElementList.size();i++){
            options.add(commonAction.getText(loc_ddvBranchOptions,i));
        }
        Collections.sort(options);
        return options;
    }
    public void navigateByUrl(){
        commonAction.navigateToURL(url);
        logger.info("Navigate to url: "+url);
    }
    public void waitOrderMetricLoaded(){
        WebDriverWait expliciWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        expliciWait.until(new Function<WebDriver, Boolean>() {
            int orderMetricBefore = getOrdersMetric();
            @Override
            public Boolean apply(WebDriver driver) {
                commonAction.sleepInMiliSecond(500);
                int orderMetricAfter = getOrdersMetric();
                if(orderMetricAfter == orderMetricBefore){
                    return true;
                }else {
                    orderMetricBefore = orderMetricAfter;
                }
                return false;
            }
        });
        new WebDriverWait(driver, Duration.ofSeconds(0));
    }
    public int  waitOrderMetricUpdated(){
        int orderMetricBefore = getOrdersMetric();
        for(int i = 0;i <10; i++){
            commonAction.refreshPage();
            new HomePage(driver).waitTillSpinnerDisappear1();
            waitOrderMetricLoaded();
            int orderMetricAfter = getOrdersMetric();
            if(orderMetricAfter!= orderMetricBefore) return orderMetricAfter;
            commonAction.sleepInMiliSecond(500);
        }
        logger.info("Order Analytics still not updated.");
        return 0;
    }
    /**************Staff Permission*******************/
    public boolean hasViewDataOfAssignedBranch(){
        return allPermissions.getAnalytics().getOrdersAnalytics().isViewDataOfAssignedBranch();
    }
    public boolean hasViewCreatedData(){
        return allPermissions.getAnalytics().getOrdersAnalytics().isViewCreatedData();
    }
    public OrderAnalytics checkOrderAnalyticPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        commonAction.sleepInMiliSecond(2000,"Wait loading after login");
        if(hasViewDataOfAssignedBranch()){
            navigateByUrl();
            int customer = new APIAllCustomers(sellerLoginInfo).getAllAccountCustomerId().get(0);
            waitOrderMetricLoaded();
            int orderNumericBefore = getOrdersMetric();
            //Seller create order on all branches.
            List<Integer> branchIds = new BranchManagement(sellerLoginInfo).getInfo().getBranchID();
            branchIds.stream().forEach(n -> new APICreateOrderPOS(sellerLoginInfo).CreatePOSOrderByBranch(customer,n));
            //Get Assigned branch of staff
            List<String> staffBranch = new Login().getInfo(staffLoginInfo).getAssignedBranchesNames();
            int expectedOrderMetric = orderNumericBefore + staffBranch.size();
            waitOrderMetricUpdated();
            assertCustomize.assertEquals(getOrdersMetric(),expectedOrderMetric,
                    "[Failed] Has View data of assigned branch: Order metric actual: %s \nOrder metric expected: %s".formatted(getOrdersMetric(),expectedOrderMetric));
            //Verify branch list on Branch tag.
            selectFilterType(FilterType.BRANCH);
            Collections.sort(staffBranch);
            assertCustomize.assertEquals(getBranchOptions(),staffBranch,
                    "[Failed] Branch list actual: %s\nBranch list expected: %s".formatted(getBranchOptions(),staffBranch));
        }else {
            if (hasViewCreatedData()){
                navigateByUrl();
                int customer = new APIAllCustomers(sellerLoginInfo).getAllAccountCustomerId().get(0);
                waitOrderMetricLoaded();
                int orderNumericBefore = getOrdersMetric();
                //Seller create order
                new APICreateOrderPOS(sellerLoginInfo).CreatePOSOrder(customer);
                //Staff create order
                new APICreateOrderPOS(staffLoginInfo).CreatePOSOrder(customer);
                int expectedOrderMetric = orderNumericBefore + 1;
                waitOrderMetricUpdated();
                assertCustomize.assertEquals(getOrdersMetric(),expectedOrderMetric,
                        "[Failed] Has View Created Data: Order metric actual: %s \nOrder metric expected: %s".formatted(getOrdersMetric(),expectedOrderMetric));
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
                        "[Failed] Restricted page should be shown when navigate to analytics order url");
            }
        }
        AssertCustomize.verifyTest();
        return this;
    }
}
