package pages.dashboard.products.supplier.function.management;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class SupplierManagementElement {
    WebDriver driver;
    SupplierManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".supplier-list-page .d-desktop-flex .uik-input__input")
    WebElement SEARCH_BOX;

    @FindBy(css = ".gs-button__green")
    WebElement HEADER_ADD_SUPPLIER_BTN;

    @FindBy(css = ".gs-table-body-items strong")
    List<WebElement> SUPPLIER_CODE;
}
