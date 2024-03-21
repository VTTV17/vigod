package utilities.model.dashboard.marketing.emailCampaign;

import lombok.Data;
import utilities.data.DataGenerator;

@Data
public class EmailCampaignInfo {
    private String random = new DataGenerator().generateString(10);
    private int id;
    private String name = "Email campaign " + random;
    private String contentHtml;
    private int mailId;
    private String emailTitle = "title "+random;
}