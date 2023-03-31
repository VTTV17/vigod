package pages.dashboard.products.supplier.ui.management;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class UISupplierManagementElement {
    WebDriver driver;
    UISupplierManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".gs-page-title")
    WebElement UI_PAGE_TITLE;

    @FindBy(css = ".gs-button__green")
    WebElement UI_HEADER_ADD_SUPPLIER_BTN;

    @FindBy(css = ".d-mobile-none .gs-search-box__wrapper input")
    WebElement UI_SEARCH_BOX_PLACEHOLDER;

    @FindBy(css = ".d-mobile-none .gs-table-header-item")
    List<WebElement> UI_SUPPLIER_TABLE_COLUMN;

    @FindBy(css = ".d-mobile-none.gs-table +* img +*")
    WebElement UI_NO_RESULT;
}
