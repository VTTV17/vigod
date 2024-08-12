package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class StoreBranch {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private String name;
    private int storeId;
    private String code;
    private String address;
    private String ward;
    private String district;
    private String city;
    private String phoneNumberFirst;
    private Boolean isDefault;
    private String branchStatus;
    private String branchType;
    private String address2;
    private String countryCode;
    private String cityName;
    private String zipCode;
    @JsonProperty("default")
    private boolean defaultBranch;
    private Boolean status;
}
