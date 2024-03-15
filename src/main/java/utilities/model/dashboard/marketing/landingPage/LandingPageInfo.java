package utilities.model.dashboard.marketing.landingPage;

import lombok.Data;
import utilities.data.DataGenerator;

import javax.swing.text.html.HTML;

@Data
public class LandingPageInfo {
    private String random = new DataGenerator().generateString(10);
    private int id;
    private String name = "Landing Page " + random;
    private String status = "DRAFT";
    private String contentHtml;
    private int templateId;
    private String domainName = random;
}
