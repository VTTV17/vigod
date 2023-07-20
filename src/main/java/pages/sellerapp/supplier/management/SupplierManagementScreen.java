package pages.sellerapp.supplier.management;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;

import java.time.Duration;

public class SupplierManagementScreen extends SupplierManagementElement {
    WebDriver driver;
    WebDriverWait wait;
    public SupplierManagementScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    public void openCreateSupplierScreen() {
        wait.until(ExpectedConditions.presenceOfElementLocated(ADD_ICON)).click();
    }
}
