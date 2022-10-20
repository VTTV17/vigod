package pages.dashboard.customers.segments.customer_in_segment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.dashboard.customers.segments.Segments;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static pages.dashboard.customers.segments.CreateSegment.segmentName;

public class SegmentCustomerPage extends SegmentCustomerElement {
    List<String> customerList;

    public SegmentCustomerPage(WebDriver driver) {
        super(driver);
    }

    public void getListCustomerInSegment(String... segment) throws InterruptedException {
        // init customer list
        customerList = new ArrayList<>();

        // get segment list
        String[] segmentList = segment.length == 0 ? new String[]{segmentName} : segment;

        // collect all customer in all segment is provided
        for (String segmentName : segmentList) {
            // open list customer in segment page
            new Segments(driver).navigate().openSegmentCustomerPage(segmentName);

            // reload page
            // some segment does not show list customer in the first times
            driver.navigate().refresh();

            // wait api return list customers
            sleep(2000);

            // check if customer is not add => add it to customer list
            for (WebElement element : LIST_CUSTOMER_NAME)
                if (!customerList.contains(element.getText())) customerList.add(element.getText());
        }
    }
}
