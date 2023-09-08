package pages.dashboard.service.servicecollections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.dashboard.service.CreateServicePage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.dashboard.services.ServiceCollectionsInfo;

import java.time.Duration;
import java.util.*;

public class EditServiceCollection extends CreateServiceCollection{
    WebDriver driver;
    CreateEditServiceCollectionElement ui;
    final static Logger logger = LogManager.getLogger(EditServiceCollection.class);
    ServiceCollectionManagement serviceCollectionManagement;
    public EditServiceCollection(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        ui = new CreateEditServiceCollectionElement(driver);
        generator = new DataGenerator();
        PageFactory.initElements(driver, this);
    }
    public EditServiceCollection navigateEditServiceCollection(String collectioName) throws Exception {
        HomePage home = new HomePage(driver);
        home.waitTillSpinnerDisappear();
        home.navigateToPage("Services", "Service Collections");
        home.hideFacebookBubble();
        serviceCollectionManagement = new ServiceCollectionManagement(driver);
        return serviceCollectionManagement.goToEditServiceCollection(collectioName);
    }
    public ServiceCollectionManagement editServicePriorityInCollection() {
        CreateServiceCollection.servicePriorityMap = inputPriority(false, true);
        clickOnSaveBTN();//Click outside
        clickOnSaveBTN();
        logger.info("Edit priority of product.");
        return clickOnClose();
    }
    public ServiceCollectionManagement editServiceListInManualCollection(String[] newServiceList, boolean hasDeleteProduct, boolean hasInputPriority){
        int serviceSize = DELETE_BTN_LIST.size();
        if(hasDeleteProduct) {
            for(int i=0; i< serviceSize;i++){
                commonAction.clickElement(DELETE_BTN_LIST.get(0));
                commonAction.sleepInMiliSecond(500);
            }
        }
        selectServiceWithKeyword(newServiceList);
        if(hasInputPriority){
            CreateProductCollection.productPriorityMap = inputPriority(false,true);
            clickOnSaveBTN();//Click outside
        }else {
            CreateProductCollection.productPriorityMap = getProductPriorityMapBefore();
        }
        clickOnSaveBTN();
        logger.info("Edit product list in manual collection.");
        return clickOnClose();
    }
    public Map<String, Integer> getProductPriorityMapBefore() {
        Map<String, Integer> productPriorityMap = new HashMap<>();
        int productListSize = SERVICE_NAME_LIST.size();
        for (int i = 0; i < productListSize; i++) {
            String priority = commonAction.getElementAttribute(PRIORITIES_INPUT.get(i),"value");
            if (priority.equals("")) {
                productPriorityMap.put(commonAction.getText(SERVICE_NAME_LIST.get(i)).toLowerCase(), productListSize);

            } else {
                productPriorityMap.put(commonAction.getText(SERVICE_NAME_LIST.get(i)).toLowerCase(),Integer.parseInt(priority));

            }
        }
        return productPriorityMap;
    }
    public String[] getCollectionConditionBefore(){
        List<String> conditionList = new ArrayList<>();
        int conditionSize = CONDITION_VALUE_INPUT.size();
        for (int i=0; i<conditionSize;i++){
            String condition = commonAction.getDropDownSelectedValue(CONDITION_DROPDOWN.get(i));
            String operate = commonAction.getDropDownSelectedValue(OPERATOR_DROPDOWN.get(i));
            String value = commonAction.getElementAttribute(CONDITION_VALUE_INPUT.get(i),"value");
            String aCondition = condition+"-"+operate+"-"+value;
            conditionList.add(aCondition);
        }
        logger.info("Conditions selected before: "+conditionList);
        String[] conditions = new String[conditionList.size()];
        return conditionList.toArray(conditions);
    }
    public String[] editAutomationCollection(String conditionType, String...conditions) throws Exception {
        commonAction.sleepInMiliSecond(1000);
        selectConditionType(conditionType);
        String[] conditionsAvailable = getCollectionConditionBefore();
        List<String> allList = new ArrayList<>();
        allList.addAll(Arrays.stream(conditionsAvailable).toList());
        allList.addAll(Arrays.stream(conditions).toList());
        System.out.println("allList: "+allList);
        String[] allConditionArr = new String[allList.size()];
        allList.toArray(allConditionArr);
        selectCondition(true,allList.toArray(allConditionArr));
        clickOnSaveBTN();
        clickOnClose();
        logger.info("Update Automated collection successfully.");
        return allConditionArr;
    }
    public EditServiceCollection clickEditTranslationBtn(){
        commonAction.clickElement(EDIT_TRANSLATION_BTN);
        logger.info("Click Edit translation button.");
        return this;
    }
    public EditServiceCollection inputNameTranslation(String name){
        commonAction.inputText(NAME_TRANSLATION,name);
        logger.info("Input name in edit translation popup: "+name);
        return this;
    }
    public EditServiceCollection inputSEOTitleTranslation(String SEOTitle){
        commonAction.inputText(SEO_TITLE_TRANSLATE,SEOTitle);
        logger.info("Input SEO title to translation popup: "+SEOTitle);
        return this;
    }
    public EditServiceCollection inputSEODescriptionTranslation(String SEODescription){
        commonAction.inputText(SEO_DESCRIPTION_TRANSLATE,SEODescription);
        logger.info("Input SEO description to translation popup: "+SEODescription);
        return this;
    }
    public EditServiceCollection inputSEOKeywordTranslation(String SEOKeyword){
        commonAction.inputText(SEO_KEYWORDS_TRANSLATE,SEOKeyword);
        logger.info("Input SEO keyword to translation popup: "+SEOKeyword);
        return this;
    }
    public EditServiceCollection inputSEOUrlTranslation(String SEOUrl){
        commonAction.inputText(SEO_URL_TRANSLATE,SEOUrl);
        logger.info("Input SEO url to translation popup: "+SEOUrl);
        return this;
    }
    public EditServiceCollection clickSaveTranslation(){
        commonAction.clickElement(SAVE_TRANSLATE_BTN);
        logger.info("Click on Save translation button.");
        return this;
    }
    public EditServiceCollection editTranslation(ServiceCollectionsInfo serviceCollectionsInfo){
        inputNameTranslation(serviceCollectionsInfo.getCollectionName());
        inputSEOTitleTranslation(serviceCollectionsInfo.getSEOTitleTranslation());
        inputSEODescriptionTranslation(serviceCollectionsInfo.getSEODescriptionTranslation());
        inputSEOKeywordTranslation(serviceCollectionsInfo.getSEOKeywordTranslation());
        inputSEOUrlTranslation(serviceCollectionsInfo.getSEOUrlTranslation());
        clickSaveTranslation();
        return this;
    }
    public EditServiceCollection verifyUpdateTranslateSuccessfulMessage() throws Exception {
        String toast = new HomePage(driver).getToastMessage();
        Assert.assertEquals(toast, PropertiesUtil.getPropertiesValueByDBLang("services.create.updateTranslationSuccessully"));
        logger.info("Update translate successfully message is show.");
        return this;
    }
}
