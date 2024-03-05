package web.Dashboard.customers.segments.createsegment;

import org.openqa.selenium.By;

public class CreateSegmentElement {
	By loc_btnSave = By.cssSelector(".btn-save");
	By loc_btnCancel = By.cssSelector(".btn-cancel");
	By loc_txtSegmentName = By.id("segmentName");
	
	String loc_frmConditionFragmentsRoot = "(//div[@class='segment-condition-row row']//div[@class='form-group'])[%s]-followingElement";
	String loc_frmConditionFragmentsSelectTag = loc_frmConditionFragmentsRoot.replace("-followingElement", "//select");
	String loc_frmConditionFragmentsInputTag = loc_frmConditionFragmentsRoot.replace("-followingElement", "//input");
	String loc_frmConditionFragmentsSpanTag = loc_frmConditionFragmentsRoot.replace("-followingElement", "/preceding-sibling::span");
	
	By loc_dlgSelectProduct = By.cssSelector(".select-product-modal");
	public By loc_tblProduct = By.cssSelector(".select-collection-row");
	By loc_txtSearchProduct = By.cssSelector("input.search-input");
	
}
