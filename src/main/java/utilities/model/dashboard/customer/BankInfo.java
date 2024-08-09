package utilities.model.dashboard.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class BankInfo {
    private int id;
    private int storeId;
    private String countryCode;
    private String fullName;
    private Integer taxCode;//VN only
    private String region;//VN only
    private String bankHolderName;
    private String bankAccountNumber;
    private String bankId;//VN only
    private String bankBranchName;
    private String bankName;//Foreign only
    private String swiftCode;;//Foreign only
}
