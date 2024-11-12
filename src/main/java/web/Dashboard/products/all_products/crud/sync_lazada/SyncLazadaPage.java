package web.Dashboard.products.all_products.crud.sync_lazada;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.Domain;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.supplier.purchaseorders.crud.PurchaseOrderPage;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static utilities.links.Links.DOMAIN;

public class SyncLazadaPage extends SyncLazadaElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(PurchaseOrderPage.class);

    public SyncLazadaPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }
    public SyncLazadaPage clickOnCategory(){
        commonAction.click(loc_ddlCategory);
        return this;
    }
    public void selectCategory(){
        clickOnCategory();
        for (int i=1;i<6; i++){
            List<WebElement> cateList = commonAction.getElements(loc_categorySubmenu(i),3);
            if(cateList.isEmpty()) break;
            else commonAction.click(loc_categorySubmenu(i),0);
        }
    }
    public SyncLazadaPage inputSKU(){
        List<WebElement> skuList = commonAction.getElements(loc_lst_txtSKU,2);
        for (int i=0; i<skuList.size(); i++){
            if(commonAction.getValue(loc_lst_txtSKU,i).equals("")){
                commonAction.inputText(loc_lst_txtSKU, i, new DataGenerator().generateString(5));
            }
        }
        logger.info("Input all SKU.");
        return this;
    }
    @SneakyThrows
    public SyncLazadaPage selectBrand(){
        commonAction.click(loc_txtBrand);
        List<WebElement> brandList = commonAction.getElements(loc_lstBrand,3);
        if(brandList.isEmpty()) throw new Exception("Brand list not show.");
        else brandList.get(0).click();
        logger.info("Select brand");
        return this;
    }
    public SyncLazadaPage selectAllVariation(){
        commonAction.click(loc_ckbSelectAllVariation);
        logger.info("Select all variation");
        return this;
    }
    public SyncLazadaPage clickCreateBtn(){
        commonAction.click(loc_btnCreate);
        logger.info("Click create button.");
        return this;
    }
    @SneakyThrows
    public SyncLazadaPage verifyCreateToLazadaSuccess(){
        String toast = new HomePage(driver).getToastMessage();
        Assert.assertEquals(toast, PropertiesUtil.getPropertiesValueByDBLang("products.productDetail.syncLazada.create.success"));
        logger.info("Verify create success message.");
        return this;
    }
    public SyncLazadaPage waitLazadaAccountEnable(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                String selectedAccount = commonAction.getText(loc_selectedAccount);
                return !(selectedAccount.equals("Select account") || selectedAccount.equals("Chọn tài khoản"));
            }
        });
        return this;
    }
    public SyncLazadaPage inputDimension(){
        List<WebElement> iconDimensionList = commonAction.getElements(loc_lst_icnDimension,3);
        for (int i=0; i< iconDimensionList.size();i++){
            commonAction.click(loc_lst_icnDimension,i);
            commonAction.sendKeys(loc_txtLengthWidthHeightWeight,0,"10");
//            commonAction.sleepInMiliSecond(1000);
            commonAction.sendKeys(loc_txtLengthWidthHeightWeight,1,"10");
            commonAction.sendKeys(loc_txtLengthWidthHeightWeight,2,"10");
            commonAction.sendKeys(loc_txtLengthWidthHeightWeight,3,"0.1");
            commonAction.click(loc_icnCloseDimension);
        }
        logger.info("Input all dimension.");
        return this;
    }
    public SyncLazadaPage createProductToLazada(){
        waitLazadaAccountEnable();
        selectCategory();
        selectBrand();
        selectAllVariation();
        inputSKU();
        inputDimension();
        clickCreateBtn();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        return this;
    }
    public SyncLazadaPage updateProductToLazada(){
        waitLazadaAccountEnable();
        selectAllVariation();
        inputSKU();
        inputDimension();
        clickUpdateBtn();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        return this;
    }
    public SyncLazadaPage clickUpdateBtn(){
        commonAction.click(loc_btnUpdate);
        logger.info("Click update button.");
        return this;
    }
    @SneakyThrows
    public SyncLazadaPage verifyUpdateToLazadaSuccess(){
        String toast = new HomePage(driver).getToastMessage();
        Assert.assertEquals(toast, PropertiesUtil.getPropertiesValueByDBLang("products.productDetail.syncLazada.update.success"));
        logger.info("Verify update success message.");
        return this;
    }
}
