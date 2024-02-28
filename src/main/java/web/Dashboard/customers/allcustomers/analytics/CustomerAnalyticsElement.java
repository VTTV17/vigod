package web.Dashboard.customers.allcustomers.analytics;

import org.openqa.selenium.By;

public class CustomerAnalyticsElement {
	By loc_lnkUpdate = By.cssSelector(".customer-overview-time-frame-wrapper a");
    By loc_btnCancel = By.cssSelector(".btn-cancel");
    By loc_btnConfirmPayment = By.id("btn-print");
    By loc_dlgConfirmPayment = By.cssSelector(".confirmation-payment-modal");
}
