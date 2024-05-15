package utilities.model.dashboard.customer;

import lombok.Data;

@Data
public class CustomerInfo {
    private int customerId;
    private String mainEmail;
    private String mainEmailName;
    private String mainPhoneCode;
    private String mainPhoneNumber;
    private String mainPhoneName;
    private String userId;
}
