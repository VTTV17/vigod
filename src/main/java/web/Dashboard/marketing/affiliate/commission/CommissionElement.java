package web.Dashboard.marketing.affiliate.commission;

import org.openqa.selenium.By;

public class CommissionElement {
    By log_lstCommissionName = By.xpath("//tr/td[1]");
    By loc_btnAddCommission = By.cssSelector(".btn-save");
    By loc_lst_icnEdit = By.xpath("//i[contains(@class,'gs-action-button')][1]");
    By loc_lst_icnDelete = By.xpath("//i[contains(@class,'gs-action-button')][2]");
}
