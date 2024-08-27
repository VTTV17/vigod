package utilities.model.dashboard.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerInfoFull {

    private List<CustomerEmail> emails;
    private List<CustomerEmail> backupEmails;
    private List<CustomerPhone> phones;
    private List<CustomerPhone> backupPhones;
    private int id;
    private String fullName;
    private String storeId;
    private String email;
    private String phone;
    private String phoneNumberWithoutZero;
    private String phoneNumberWithZero;
    private String phoneNumberWithPhoneCode;
    private Double initDebt;
    private String saleChannel;
    private String memberSince;
    private String userName;
    private String userId;
    private Integer totalOrder;
    private Double totalPurchase;
    private Integer averangePurchase;
    private Integer numberOfFbUsers;
    private Integer numberOfZaloUsers;
    private Boolean guest;
    private CustomerAddress customerAddress;
    private CustomerAddressFull customerAddressFull;
    private String accountType;
    private List<BankInfo> bankInfos;
    private String partnerType;
    private List<String> tags;
    private String note;
    private String companyName;
    private String taxCode;
    private String identityCard;
    private Integer responsibleStaffUserId;
    private String gender;
    private String birthday;
    private Integer partnerId;
    private String userStatus;
    private Integer customerDropshipId;
}
