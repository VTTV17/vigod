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

    By loc_txtProductQuantity(String productName) {
        return By.xpath("//tr[td//div[text() ='%s']]//input".formatted(productName));
    }

    By loc_txtProductQuantity(String productName, String variationValue) {
        return By.xpath("//tr[td//div[text() ='%s'] and td//span[text() ='%s']]//input".formatted(productName, variationValue));
    }

    By loc_btnSelectIMEI(String productName) {
        return By.xpath("//tr[td//div[text() ='%s']]//*[@class='select-IMEI errorIMEI']".formatted(productName));
    }

    By loc_btnSelectIMEI(String productName, String variationValue) {
        return By.xpath("//tr[td//div[text() ='%s'] and td//span[text() ='%s']]//*[@class='select-IMEI errorIMEI']".formatted(productName, variationValue));
    }

    By loc_btnSelectLot(String productName) {
        return By.xpath("//tr[td//div[text() ='%s']]//following-sibling::tr[1]//img".formatted(productName));
    }

    By loc_btnSelectLot(String productName, String variationValue) {
        return By.xpath("//tr[td//div[text() ='%s'] and td//span[text() ='%s']]//following-sibling::tr[1]//img".formatted(productName, variationValue));
    }

    By loc_dlgSelectIMEI_lstIMEI = By.cssSelector(".content:not(.selected)");
    By loc_dlgSelectIMEI_btnSave = By.cssSelector(".modal-footer .gs-button__green");

    By loc_dlgSelectLot_txtQuantity = By.cssSelector(".modal-lot-select .get-quantity input");
    By loc_dlgSelectLot_btnSave = By.cssSelector(".modal-footer .gs-button__green");
}
