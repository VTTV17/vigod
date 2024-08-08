package utilities.model.dashboard.marketing.loyaltyProgram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utilities.data.DataGenerator;

@Data
public class LoyaltyProgramInfo {
    public String name = "Membership " + new DataGenerator().generateString(5);
    public String description = "Membership Description" + new DataGenerator().generateString(5);
    public Integer segmentId;
    public Integer sellerId;
    public Integer priority = 1;
    public Boolean enabledBenefit = true;
    public Integer discountPercent = 20;
    public Double discountMaxAmount = Double.valueOf(50000);
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Image image;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer storeId;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer id;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String iconExtension;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String iconUrlPrefix;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String imageUUID;
}

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Image {
    public String urlPrefix = "";
    public String imageUUID = "";
    public String extension = "";
    public String fullUrl = "";
}