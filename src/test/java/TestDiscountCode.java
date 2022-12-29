import api.dashboard.promotion.DiscountCode;
import org.testng.annotations.Test;

import static utilities.links.Links.URI;

public class TestDiscountCode {
    @Test
    void TestCode() {
        URI = "https://api.beecow.info";
        new DiscountCode().createDiscountCode("stgaboned@nbobd.com", "Abc@12345");
    }
}
