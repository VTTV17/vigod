package web.Dashboard.customers.segments;

import org.openqa.selenium.By;

public class SegmentElement {
    By loc_btnCreateSegment = By.cssSelector(".segment-list .btn-create");
    By loc_txtSearchSegment = By.cssSelector(".gs-search-box__wrapper .uik-input__input");
    By loc_btnViewIcon = By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(4) > div > a:nth-child(1)");
    
    String loc_tblRow = "//div[contains(@class,'segment-list__widget-body')]//tbody/tr";
    By loc_tblSegmentIdColumn = By.xpath(loc_tblRow.concat("/td[1]"));
    By loc_tblSegmentNameColumn = By.xpath(loc_tblRow.concat("/td[2]"));
    By loc_tblQuantityColumn = By.xpath(loc_tblRow.concat("/td[3]"));
    
    String loc_icnView = loc_tblRow.concat("/td[position()=1 and text()='%s']/following-sibling::*//i[contains(@style,'icon-view')]");
    String loc_icnEdit = loc_tblRow.concat("/td[position()=1 and text()='%s']/following-sibling::*//i[contains(@style,'icon-edit')]");
    String loc_icnDelete = loc_tblRow.concat("/td[position()=1 and text()='%s']/following-sibling::*//i[contains(@style,'icon-delete')]");
}
 