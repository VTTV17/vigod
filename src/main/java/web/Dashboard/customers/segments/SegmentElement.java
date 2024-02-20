package web.Dashboard.customers.segments;

import org.openqa.selenium.By;

public class SegmentElement {
    By loc_btnCreateSegment = By.cssSelector(".segment-list .btn-create");
    By loc_txtSearchSegment = By.cssSelector(".gs-search-box__wrapper .uik-input__input");
    By loc_btnViewIcon = By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(4) > div > a:nth-child(1)");
    
    String loc_btnDelete = "//div[contains(@class,'segment-list__widget-body')]//tbody/tr[1]/td[position()=2 and text()='%s']/following-sibling::*//i[contains(@style,'icon-delete')]";
}
