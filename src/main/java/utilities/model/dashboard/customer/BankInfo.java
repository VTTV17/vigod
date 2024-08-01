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
    private String region;
    private String bankHolderName;
    private String bankAccountNumber;
    private String bankId;
    private String bankBranchName;
}
