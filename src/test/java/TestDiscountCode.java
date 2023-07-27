import api.dashboard.login.Login;
import api.dashboard.promotion.DiscountCode;
import org.testng.annotations.Test;
import utilities.model.sellerApp.login.LoginInformation;

import static utilities.links.Links.URI;

public class TestDiscountCode {
    @Test
    void TestCode() {
//        URI = "https://api.beecow.info";
        new DiscountCode(new LoginInformation()).createDiscountCode("bigdata_per@yopmail.com", "H123456@");
    }
}
