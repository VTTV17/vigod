package pages.dashboard.products.all_products;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductVerify extends ProductElement {
    static String language;
    WebDriverWait wait;

    public ProductVerify(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
}
