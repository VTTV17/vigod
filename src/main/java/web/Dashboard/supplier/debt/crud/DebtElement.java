package web.Dashboard.supplier.debt.crud;

import org.openqa.selenium.By;

public class DebtElement {
    By loc_btnDelete = By.cssSelector(".btn.cancel");
    By loc_btnPublic = By.cssSelector(".btn.public");
    By loc_dlgConfirmPublic = By.cssSelector(".modal-dialog");
    By loc_dlgConfirmPublic_btnOk = By.cssSelector(".modal-dialog .btn-primary");
    By loc_btnSave = By.cssSelector(".btn.save");
    By loc_btnMakeARepayment = By.cssSelector(".btn-make-payment");
    By loc_tblPaymentHistory_recordHistory = By.cssSelector("tbody > tr");
    By loc_dlgPayment = By.cssSelector(".making-a-payment-modal");
    By loc_dlgPayment_txtDescription = By.xpath("//*[@class = 'check-record-to-cashbook']/preceding-sibling::div//textarea");
    By loc_dlgPayment_btnConfirm = By.cssSelector(".making-a-payment-modal .button-v2");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
