package web.Dashboard.gowallet.transactionhistory.topup;

import org.openqa.selenium.By;

public class TopupElement {
    By loc_txt_paymentAmount = By.cssSelector("#amount");
    By loc_lst_onlinePayment = By.cssSelector(".payment-method-item");
    By loc_btnProcessToPayment = By.cssSelector(".gs-button__deep-blue");
}
