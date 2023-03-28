package pages.dashboard.products.supplier.ui.management;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class UISupplierManagementElement {
    WebDriver driver;
    UISupplierManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".supplier-list-page .d-desktop-flex .uik-input__input")
    WebElement SEARCH_BOX;

    @FindBy(css = ".gs-button__green")
    WebElement HEADER_ADD_SUPPLIER_BTN;
}
