package utilities.model.dashboard.marketing.loyaltyProgram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class LoyaltyProgramInfo {
        public String name;
        public String description;
        public Integer segmentId;
        public Integer sellerId;
        public Integer priority;
        public Boolean enabledBenefit;
        public String discountPercent;
        public String discountMaxAmount;
        public Image image;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Image{
    public String urlPrefix = "";
    public String imageUUID = "";
    public String extension = "";
}