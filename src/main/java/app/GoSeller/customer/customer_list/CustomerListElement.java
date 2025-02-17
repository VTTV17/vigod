package app.GoSeller.customer.customer_list;

import org.openqa.selenium.By;

public class CustomerListElement {
    By loc_icnCreateCustomer = By.xpath("//*[contains(@resource-id, 'ivActionBarIconRight')]");
    By loc_icnFilter = By.xpath("//*[contains(@resource-id, 'ivFilterButton')]");
    By loc_lstCustomerName = By.xpath("//*[contains(@resource-id, 'tvCustomerName')]");
}
