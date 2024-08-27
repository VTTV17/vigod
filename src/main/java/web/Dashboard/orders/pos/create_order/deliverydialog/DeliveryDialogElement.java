package web.Dashboard.orders.pos.create_order.deliverydialog;

import org.openqa.selenium.By;

public class DeliveryDialogElement {

	By txtCustomerName = By.id("customer_name");
	By txtCustomerPhone = By.id("customer_phone");
	By txtCustomerEmail = By.id("email");
	By ddlCountry = By.id("countryCode");
	By txtCustomerAddress = By.id("address");
	By ddlProvince = By.id("city");
	By ddlDistrict = By.id("district");
	By ddlWard = By.id("ward");
	
	By txtCustomerAddress2 = By.id("address2");
	By txtCity = By.id("cityName");
	By txtZipcode = By.id("zipCode");
	By ddlPhoneCodeValue = By.cssSelector(".phone-code .uik-select__valueWrapper");
	
}
