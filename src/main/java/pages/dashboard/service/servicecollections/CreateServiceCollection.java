package pages.dashboard.service.servicecollections;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;

import api.dashboard.services.ServiceInfoAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.Constant;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.file.FileNameAndPath;
import utilities.model.dashboard.services.ServiceCollectionsInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.sort.SortData;

public class CreateServiceCollection extends CreateEditServiceCollectionElement {

    final static Logger logger = LogManager.getLogger(CreateServiceCollection.class);


    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    public static Map<String, Integer> servicePriorityMap = new HashMap<>();

    SoftAssert soft = new SoftAssert();
    public static int serviceSelectedNumber = 0;
    DataGenerator generator;
    public CreateServiceCollection(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        generator = new DataGenerator();
        PageFactory.initElements(driver, this);
    }

    public CreateServiceCollection inputCollectionName(String name) {
        commonAction.inputText(COLLECTION_NAME, name);
        logger.info("Input '" + name + "' into Collection Name field.");
        return this;
    }
    /*------------------Edit---------------*/
    public List<String> getListServiceName(){
        List<String> serviceNameList = new ArrayList<>();
        while (SERVICE_NAME_LIST.size()>0){
            serviceNameList.add(commonAction.getText(SERVICE_NAME_LIST.get(0)));
            commonAction.clickElement(DELETE_BTN_LIST.get(0));
        }
        return serviceNameList;
    }
    public CreateServiceCollection verifyServiceShowInServiceList(String serviceName){
        Assert.assertTrue(getListServiceName().contains(serviceName),serviceName+": not show in collection");
        logger.info("Verify service name display in service list of collection");
        return this;
    }
    public CreateServiceCollection verifyServiceNotShowInServiceList(String serviceName){
        Assert.assertFalse(getListServiceName().contains(serviceName),serviceName+": show in collection");
        logger.info("Verify %s not display in service list of collection".formatted(serviceName));
        return this;
    }
    public CreateServiceCollection navigate(String languageDashboard) throws Exception {
        HomePage home = new HomePage(driver);
        home.waitTillSpinnerDisappear();
        home.navigateToPage("Services", "Service Collections");
        home.selectLanguage(languageDashboard);
        home.hideFacebookBubble();
        ServiceCollectionManagement serviceCollectionManagement = new ServiceCollectionManagement(driver);
        return serviceCollectionManagement.clickCreateServiceCollection();
    }
    public CreateServiceCollection uploadImages(String...fileNames){
        commonAction.uploadMultipleFile(IMAGE_INPUT,"service_collection",fileNames);
        logger.info("Upload multiple file: "+ Arrays.toString(fileNames));
        return this;
    }
    public CreateServiceCollection selectCollectionType(String collectionType) throws Exception {
        if (collectionType.equalsIgnoreCase(Constant.MANUAL_OPTION)) {
            commonAction.checkTheCheckBoxOrRadio(MANUAL_RADIO_VALUE, MANUAL_RADIO_ACTION);
        } else if (collectionType.equalsIgnoreCase(Constant.AUTOMATED_OPTION)) {
            commonAction.checkTheCheckBoxOrRadio(AUTOMATED_RADIO_VALUE, AUTOMATED_RADIO_ACTION);
        } else {
            throw new Exception("Input value does not match any of the accepted values: Manual/Automated");
        }
        logger.info("Select collection type: " + collectionType);
        return this;
    }
    public CreateServiceCollection clickOnSelectService() {
        commonAction.clickElement(SELECT_SERVICE_BTN);
        logger.info("Click on Select product button.");
        return this;
    }

    public CreateServiceCollection inputSearchKeyword(String keyword) {
        commonAction.inputText(SEARCH_FOR_PRODUCT_INPUT, keyword);
        logger.info("Search with keyword: " + keyword);
        return this;
    }
    public CreateServiceCollection selectAllProductInCurrentPage() {
        commonAction.checkTheCheckBoxOrRadio(SELECT_ALL_CBX_VALUE, SELECT_ALL_CBX_ACTION);
        logger.info("Select all producr in current page");
        return this;
    }
    public CreateServiceCollection clickOnOKBTN() {
        commonAction.clickElement(OK_BTN);
        logger.info("Click on OK button");
        return this;
    }
    public CreateServiceCollection selectServiceWithKeyword(String... keywords) {
        if (keywords.length == 0) {
            logger.info("No select product.");
            return this;
        }
        clickOnSelectService();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        for (String serviceName : keywords) {
            new CreateServiceCollection(driver).inputSearchKeyword(serviceName);
            new HomePage(driver).waitTillSpinnerDisappear();
            new CreateServiceCollection(driver).selectAllProductInCurrentPage();
        }
        clickOnOKBTN();
        serviceSelectedNumber = SERVICE_NAME_LIST.size();
        logger.info("Select product: " + Arrays.toString(keywords));
        return this;
    }
    public CreateServiceCollection clickOnSaveBTN() {
        commonAction.clickElement(SAVE_BTN);
        logger.info("Click on Save button");
        return this;
    }
    public CreateServiceCollection inputSEOTitle(String SEOTitle) {
        commonAction.inputText(SEO_TITLE, SEOTitle);
        logger.info("Input SEO title: " + SEOTitle);
        return this;
    }

    public CreateServiceCollection inputSEODescription(String SEODes) {
        commonAction.inputText(SEO_DESCRIPTION, SEODes);
        logger.info("Input SEO description: " + SEODes);
        return this;
    }

    public CreateServiceCollection inputSEOKeywords(String SEOKeywords) {
        commonAction.inputText(SEO_KEYWORD, SEOKeywords);
        logger.info("Input SEO keywords: " + SEOKeywords);
        return this;
    }

    public CreateServiceCollection inputSEOUrl(String SEOUrl) {
        commonAction.inputText(SEO_URL, SEOUrl);
        logger.info("Input SEO url: " + SEOUrl);
        return this;
    }

    public CreateServiceCollection inputSEOInfo(ServiceCollectionsInfo serviceCollectionsInfo) {
        inputSEOTitle(serviceCollectionsInfo.getSEOTitle());
        inputSEODescription(serviceCollectionsInfo.getSEODescription());
        inputSEOKeywords(serviceCollectionsInfo.getSEOKeywords());
        inputSEOUrl(serviceCollectionsInfo.getSEODescription());
        return this;
    }

    /**
     *
     * @param priorityForAll: input priority for all service in collection
     * @param canDuplicatePriority: priority can duplicate or not
     * @return
     */
    public Map<String, Integer> inputPriority(boolean priorityForAll, boolean canDuplicatePriority) {
        Map<String, Integer> servicePriorityMap = new HashMap<>();
        commonAction.sleepInMiliSecond(1000);
        int serviceListSize = SERVICE_NAME_LIST.size();
        List<Integer> priorityList;
        if (priorityForAll) {
            if (canDuplicatePriority) {
                priorityList = generator.randomListNumberCanDuplicate(serviceListSize);
            } else {
                priorityList = generator.randomListNumberWithNoDuplicate(serviceListSize);
            }
        } else {
            int random = generator.generatNumberInBound(1,serviceListSize-1);
            if (canDuplicatePriority) {
                priorityList = generator.randomListNumberCanDuplicate(serviceListSize - random);
            } else {
                priorityList = generator.randomListNumberWithNoDuplicate(serviceListSize - random);
            }
        }
        System.out.println("priorityList: "+priorityList);
        for (int i = 0; i < serviceListSize; i++) {
            if (i < priorityList.size()) {
                commonAction.sleepInMiliSecond(1000);
                commonAction.inputText(PRIORITIES_INPUT.get(i), String.valueOf(priorityList.get(i)));
                servicePriorityMap.put(commonAction.getText(SERVICE_NAME_LIST.get(i)).toLowerCase(), priorityList.get(i));
            } else {
                commonAction.sleepInMiliSecond(1000);
                commonAction.clearText(PRIORITIES_INPUT.get(i));
                servicePriorityMap.put(commonAction.getText(SERVICE_NAME_LIST.get(i)).toLowerCase(), serviceListSize+1);
            }
        }
        logger.info("Input product priority: " + servicePriorityMap);
        return servicePriorityMap;
    }
    public CreateServiceCollection selectConditionType(String conditionType) throws Exception {
        if (conditionType.equalsIgnoreCase(Constant.ALL_CONDITION)) {
            commonAction.checkTheCheckBoxOrRadio(ALL_CONDITION_RADIO_VALUE, ALL_CONDITION_RADIO_ACTION);
        } else if (conditionType.equalsIgnoreCase(Constant.ANY_CONDITION)) {
            commonAction.checkTheCheckBoxOrRadio(ANY_CONDITION_RADIO_VALUE, ANY_CONDITION_RADIO_ACTION);
        } else {
            throw new Exception("Input value does not match any of the accepted values: all conditions/any condition");
        }
        return this;
    }
    public CreateServiceCollection selectCondition(boolean hasAvailable, String[]conditions) {
        for (int i = 0; i < conditions.length; i++) {
            String[] conditionContent = conditions[i].split("-");
            String conditionValueInputed = commonAction.getText(CONDITION_VALUE_INPUT.get(i));
            if (hasAvailable && !conditionValueInputed.equals("")){
                commonAction.clickElement(ADD_MORE_CONDITION_BTN);
                continue;
            }
            for (int j = 0; j < 5; j++) {
                try {
                    commonAction.selectByVisibleText(new CreateEditServiceCollectionElement(driver).OPERATOR_DROPDOWN.get(i), conditionContent[1].trim());
                    break;
                } catch (StaleElementReferenceException ex) {
                    logger.debug("StaleElementReferenceException caught when selecting operator \n" + ex);
                }
            }

            commonAction.inputText(CONDITION_VALUE_INPUT.get(i), conditionContent[2].trim());
            if (i < conditions.length - 1) {
                commonAction.clickElement(ADD_MORE_CONDITION_BTN);
            }
            logger.info("Input condition: %s".formatted(conditions[i]));
        }
        commonAction.clickElement(AUTOMATED_RADIO_ACTION);//click outside
        return this;
    }
    public ServiceCollectionManagement clickOnClose() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commonAction.clickElement(wait.until(ExpectedConditions.visibilityOf(CLOSE_BTN)));
        logger.info("Click on Close button");
        return new ServiceCollectionManagement(driver);
    }
    public ServiceCollectionManagement createServiceCollection(ServiceCollectionsInfo serviceCollectionsInfo) throws Exception {
        inputCollectionName(serviceCollectionsInfo.getCollectionName());
        uploadImages(FileNameAndPath.FILE_NAME_IMAGE_SERVICE_COLLECTION_1);
        selectCollectionType(serviceCollectionsInfo.getCollectionType());
        if(serviceCollectionsInfo.getCollectionType().equalsIgnoreCase(Constant.MANUAL_OPTION)){
            if(serviceCollectionsInfo.getServiceList().length>0){
                selectServiceWithKeyword(serviceCollectionsInfo.getServiceList());
                if(serviceCollectionsInfo.isInputPriority()){
                    servicePriorityMap = inputPriority(serviceCollectionsInfo.isSetPriorityForAll(),serviceCollectionsInfo.isSetDuplicatePriority());
                    clickOnSaveBTN(); //tap outside
                }}
        }else {
            selectConditionType(serviceCollectionsInfo.getConditionType());
            selectCondition(false,serviceCollectionsInfo.getAutomatedConditions());
        }
        if(serviceCollectionsInfo.isInputSEO()){
            inputSEOInfo(serviceCollectionsInfo);
        }
        clickOnSaveBTN();
        clickOnClose();
        new HomePage(driver).waitTillSpinnerDisappear();
        return new ServiceCollectionManagement(driver);
    }
    public static List<String> sortServiceListByPriorityAndLastUpdatedDate(LoginInformation loginInformation, Map<String, Integer> servicePriorityMap, int collectionID) throws ParseException {
        logger.debug("Sort start.");
        Map<String, Integer> sortedMap = SortData.sortMapByValue(servicePriorityMap);
        List<String> sortedList = new ArrayList<>();
        List<Integer> values = sortedMap.values().stream().toList();
        Map<String, Date> serviceUpdatedMap = new HashMap<>();
        ServiceInfoAPI serviceInfoAPI = new ServiceInfoAPI(loginInformation);
        for (int i = 0; i < values.size(); i++) {
            String serviceKey1 = sortedMap.keySet().toArray()[i].toString();
            String serviceKey2 ;
            int value1 = values.get(i);
            int value2;
            if (i == values.size() - 1) {
                value2 = values.get(i - 1);
                serviceKey2 = sortedMap.keySet().toArray()[i-1].toString();
            } else {
                value2 = values.get(i + 1);
                serviceKey2 = sortedMap.keySet().toArray()[i+1].toString();
            }
            if (value1 == value2) {
                serviceUpdatedMap.putAll(serviceInfoAPI.getServiceLastModifiedDateMapByServiceName(collectionID, serviceKey1));
                serviceUpdatedMap.putAll(serviceInfoAPI.getServiceLastModifiedDateMapByServiceName(collectionID, serviceKey2));
                if(i == values.size()-1){
                    System.out.println(i+"----"+serviceUpdatedMap);
                    sortedList.addAll(serviceInfoAPI.getServiceListCollection_SortNewest(serviceUpdatedMap));
                }
            }else if (serviceUpdatedMap.isEmpty()) {
                sortedList.add(serviceKey1);
            } else {
                System.out.println(i+"----"+serviceUpdatedMap);
                sortedList.addAll(serviceInfoAPI.getServiceListCollection_SortNewest(serviceUpdatedMap));
                serviceUpdatedMap = new HashMap<>();
            }

        }
        logger.debug("Get sorted list by priority and created date: " + sortedList);
        return sortedList;
    }

    public List<String> servicesBelongCollectionExpected_MultipleCondition(LoginInformation loginInformation, String conditionType, String... conditions) throws Exception {
        ServiceInfoAPI serviceInfoAPI = new ServiceInfoAPI(loginInformation);
        int countItemExpected = 0;
        Map compareProductMap = new HashMap<>();
        Map compareCountItemMap = new HashMap<>();
        Map mergeProductMap = new HashMap<>();
        for (int i=0;i< conditions.length;i++) {
            String operater = conditions[i].split("-")[1];
            String value = conditions[i].split("-")[2];
           Map productCreatedDateMap =  serviceInfoAPI.getMapOfServiceLastModifiedDateMatchTitleCondition(operater, value);
            if (conditionType.equalsIgnoreCase(Constant.ANY_CONDITION)) {
                compareProductMap.putAll(productCreatedDateMap);
            } else if (conditionType.equalsIgnoreCase(Constant.ALL_CONDITION)) {
                if (i==0) {
                    compareProductMap.putAll(productCreatedDateMap);
                } else {
                    for (Object key : productCreatedDateMap.keySet()) {
                        if (compareProductMap.containsKey(key)) {
                            mergeProductMap.put(key, productCreatedDateMap.get(key));
                        }
                    }
                    compareProductMap=mergeProductMap;
                }
                mergeProductMap = new HashMap<>();
            }

        }
        Collection<Integer> values = compareCountItemMap.values();
        for (int v : values) {
            countItemExpected = countItemExpected + v;
        }
        List<String> productExpectedList = serviceInfoAPI.getServiceListCollection_SortNewest(compareProductMap);
        logger.info("Get service match multiple condition: "+productExpectedList);
        return productExpectedList;
    }
    public List<String> servicesBelongCollectionExpected_OneCondition(LoginInformation loginInformation, String condition) throws Exception {
        ServiceInfoAPI serviceInfoAPI = new ServiceInfoAPI(loginInformation);
        String operater = condition.split("-")[1];
        String value = condition.split("-")[2];
        List<String> productExpectedList = new ArrayList<>();
        Map serviceCollection = serviceInfoAPI.getMapOfServiceLastModifiedDateMatchTitleCondition(operater, value);
        productExpectedList = serviceInfoAPI.getServiceListCollection_SortNewest(serviceCollection);
        logger.info("Get product match 1 condition: "+productExpectedList);
        return productExpectedList;
    }

}
