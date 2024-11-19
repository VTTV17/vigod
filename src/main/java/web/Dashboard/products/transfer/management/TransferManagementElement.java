package web.Dashboard.products.transfer.management;

import lombok.Getter;
import org.openqa.selenium.By;

public class TransferManagementElement {
    @Getter
    By loc_btnAddTransfer = By.xpath("//div[contains(@class,'gss-content-header')]//button");
}
