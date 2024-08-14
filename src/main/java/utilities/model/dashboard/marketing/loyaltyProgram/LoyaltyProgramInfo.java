package utilities.model.dashboard.marketing.loyaltyProgram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utilities.data.DataGenerator;

@Data
public class LoyaltyProgramInfo {
    public String name ;
    public String description;
    public Integer segmentId;
    public Integer sellerId;
    public Integer priority;
    public Boolean enabledBenefit;
    public Integer discountPercent;
    public Double discountMaxAmount;
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