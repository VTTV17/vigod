package web.Dashboard.customers.segments.createsegment;

import org.openqa.selenium.By;

public class CreateSegmentElement {
	By loc_btnSave = By.cssSelector(".btn-save");
	By loc_btnCancel = By.cssSelector(".btn-cancel");
	By loc_txtSegmentName = By.id("segmentName");
	
	String loc_frmConditionFragmentsRoot = "(//div[@class='segment-condition-row row']//div[@class='form-group'])[%s]-precedingElement";
	String loc_frmConditionFragmentsSelectTag = loc_frmConditionFragmentsRoot.replace("-precedingElement", "//select");
	String loc_frmConditionFragmentsInputTag = loc_frmConditionFragmentsRoot.replace("-precedingElement", "//input");
	String loc_frmConditionFragmentsSpanTag = loc_frmConditionFragmentsRoot.replace("-precedingElement", "/preceding-sibling::span");
}
