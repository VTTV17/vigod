package pages.sellerapp.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.marketing.buylink.CreateBuyLink;
import pages.dashboard.service.servicecollections.ServiceCollectionManagement;
import pages.sellerapp.general.SellerGeneral;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;

import java.time.Duration;
import java.util.List;

public class SellerProductCollection {
    final static Logger logger = LogManager.getLogger(SellerProductCollection.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public SellerProductCollection (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }

    By SEARCH_COLLECTION_INPUT = By.xpath("//*[ends-with(@resource-id,'edtCollectionSearch')]");
    By QUANTITY_LIST = By.xpath("//*[ends-with(@resource-id,'tvItemQuantity')]");
    By COLLECTION_TYPE = By.xpath("//*[ends-with(@resource-id,'tvCollectionType')]");
    By COLLECTION_NAME_LIST = By.xpath("//*[ends-with(@resource-id,'tvCollectionName')]");
    By DELETE_COLLECTION = By.xpath("//*[ends-with(@resource-id,'rlDelete')]");
    public SellerProductCollection verifyText() throws Exception {
        String titlePage = new SellerGeneral(driver).getHeaderTitle();
        Assert.assertEquals(titlePage, PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.pageTitle"));
        Assert.assertEquals(common.getText(SEARCH_COLLECTION_INPUT),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.searchHint"));
        return this;
    }
    public SellerCreateCollection tapCreateCollectionIcon(){
        new SellerGeneral(driver).tapHeaderRightIcon();
        return new SellerCreateCollection(driver);
    }
    public SellerProductCollection verifyCreateSuccessfullyMessage(){
        try {
            new SellerGeneral(driver).verifyToastMessage(PropertiesUtil.getPropertiesValueByDBLang("seller.toast.createSuccessfully"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Verify create successfully message show.");
        return this;
    }
    public String getCollectionNameNewest(){
        String collectName = common.getText(common.getElements(COLLECTION_NAME_LIST).get(0));
        logger.info("Newest collection name: "+collectName);
        return collectName;
    }
    public String getCollectionTypeNewest(){
        String collectName = common.getText(common.getElements(COLLECTION_TYPE).get(0));
        logger.info("Newest collection name: "+collectName);
        return collectName;
    }
    public SellerProductCollection verifyCollectionNameNewest(String expected){
        Assert.assertEquals(getCollectionNameNewest(),expected);
        logger.info("Verify collection name newset");
        return this;
    }

    /**
     *
     * @param expected: Manually, Automated
     * @return
     */
    public SellerProductCollection verifyCollectionTypeNewest(String expected){
        String type;
        try {
            type = PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manuallyLbl");
            if(expected.equalsIgnoreCase("Automated")){
                type = PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automatedLbl");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(getCollectionTypeNewest(),type);
        logger.info("Verify collection type newest.");
        return this;
    }
    public SellerProductCollection verifyQuantityNewest(int expected){
        try {
            Assert.assertEquals(common.getText(common.getElements(QUANTITY_LIST,3).get(0)),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.quantity").formatted(String.valueOf(expected)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Verify quantity of newest collection.");
        return this;
    }
    public SellerProductCollection refreshPage(){
        common.sleepInMiliSecond(1000);
        common.swipeByCoordinatesInPercent(0.5,0.25,0.5,0.5);
        common.sleepInMiliSecond(1000);
        return this;
    }
    public SellerProductCollection waitToUpdateCollection(int second){
        common.sleepInMiliSecond(1000*second);
        return this;
    }
    public SellerCreateCollection selectNewestCollection(){
        common.clickElement(common.getElements(QUANTITY_LIST).get(0));
        logger.info("Select newest collection");
        return new SellerCreateCollection(driver);
    }
    public SellerProductCollection verifyPageTitle() {
        try {
            Assert.assertEquals(new SellerGeneral(driver).getHeaderTitle(),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.pageTitle"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    public SellerProductCollection inputToSearch(String keyword){
        common.clickElement(SEARCH_COLLECTION_INPUT);
        common.inputText(SEARCH_COLLECTION_INPUT,keyword);
        logger.info("Input to search field: "+keyword);
        return this;
    }
    public SellerCreateCollection goToCollectionBySearch(String collectionName){
        common.sleepInMiliSecond(1000);
        inputToSearch(collectionName);
        List<WebElement> elements = common.getElements(COLLECTION_NAME_LIST,5);
        if(common.getText(elements.get(0)).equalsIgnoreCase(collectionName)){
            common.clickElement(elements.get(0));
        }else try {
            throw new Exception("Not found collection: "+collectionName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new SellerCreateCollection(driver);
    }
    public SellerProductCollection verifyUpdateSuccessfullyMessage(){
        try {
            new SellerGeneral(driver).verifyToastMessage(PropertiesUtil.getPropertiesValueByDBLang("seller.toast.updateSuccessfully"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Verify create successfully message show.");
        return this;
    }
    public SellerProductCollection deleteCollectionAndVerify(String collectionName){
        inputToSearch(collectionName);
        List<WebElement> elements = common.getElements(COLLECTION_NAME_LIST,5);
        boolean isDeleted = false;
        for (int i = 0; i< elements.size();i++){
            if(common.getText(elements.get(i)).equalsIgnoreCase(collectionName)){
                common.swipeHorizontalInPercent(common.getElements(COLLECTION_NAME_LIST,3).get(i),0.75,0.25);
                common.clickElement(DELETE_COLLECTION);
                new SellerGeneral(driver).tapRightBtnOnPopup();
                isDeleted = true;
                break;
            }
        }
        if(isDeleted){
            verifyAfterDeleteCollection(collectionName);
        }else {
            try {
                throw new Exception("Collection not found.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }
    public SellerProductCollection verifyAfterDeleteCollection(String collectionName){
        common.sleepInMiliSecond(1000);
        List<WebElement> elements = common.getElements(COLLECTION_NAME_LIST);
        Assert.assertTrue(elements.size()==0," Collection not deleted: "+collectionName);
        logger.info(collectionName+" is deleted.");
        return this;
    }
}
