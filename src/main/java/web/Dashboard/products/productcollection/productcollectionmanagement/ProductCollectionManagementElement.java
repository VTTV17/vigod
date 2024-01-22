package web.Dashboard.products.productcollection.productcollectionmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductCollectionManagementElement {
    WebDriver driver;
    public ProductCollectionManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By loc_btnCreateProductCollection = By.cssSelector(".gs-content-header-right-el button");
    By loc_lst_lblCollectionName = By.cssSelector(".collection-name b");
    By loc_lst_lblTypes = By.xpath("//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][1]");
    By loc_lst_lblModes = By.xpath("//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][2]");
    By loc_lst_lblItems = By.xpath("//div[contains(@class,'products')]");
    By loc_lst_btnEdit = By.cssSelector(".actions .first-button");
    By loc_lst_btnDelete = By.cssSelector(".actions .lastest-button");
    By loc_dlgConfirmation_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    By loc_ttlPageTitleAndTotalNumber = By.xpath("//h5[@class='gs-page-title']/div");
    By loc_txtSearch = By.cssSelector(".gs-search-box__wrapper input");
    By loc_lblThumbnailCol = By.xpath("(//section[contains(@class,'gs-table-header-item')]/span)[1]");
    By loc_lblCollectionName = By.xpath("(//section[contains(@class,'gs-table-header-item')]/span)[2]");
    By loc_lblTypeCol = By.xpath("(//section[contains(@class,'gs-table-header-item')]/span)[3]");
    By loc_lblModeCol = By.xpath("(//section[contains(@class,'gs-table-header-item')]/span)[4]");
    By loc_lblItemsCol = By.xpath("(//section[contains(@class,'gs-table-header-item')]/span)[5]");
    By loc_lblActionsCol = By.xpath("(//section[contains(@class,'gs-table-header-item')]/span)[6]");
    By loc_dlgConfirmation_lblMessage = By.cssSelector(".modal-body");
    By loc_dlgConfirmation_lblTitle = By.cssSelector(".modal-title");
    By loc_dlgConfirmation_btnCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
}
