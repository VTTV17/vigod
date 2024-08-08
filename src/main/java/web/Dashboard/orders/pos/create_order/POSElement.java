package web.Dashboard.orders.pos.create_order;

import org.openqa.selenium.By;

public class POSElement {
    By loc_ddvSelectedBranch = By.cssSelector("div[class='uik-select__wrapper pos-selector'] div[class='uik-select__valueWrapper']");

    By loc_lstBranches(String branchName) {
        return By.xpath("(//div[text() = '%s'])[last()]");
    }

    By loc_txtProductSearchBox = By.cssSelector("#dropdownSuggestionProduct input");

    By loc_lstProductResult(String productBarcode) {
        return By.xpath("//code[text() = '%s']".formatted(productBarcode));
    }

    By loc_txtCustomerSearchBox = By.xpath("#dropdownSuggestionCustomer input");
    By loc_lstCustomerResult(int customerId) {
         return By.xpath("//*[@class = 'mobile-customer-profile-row__right' and contains(string(), '%s')]".formatted(customerId));
    }
}
