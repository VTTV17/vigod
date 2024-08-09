package utilities.model.dashboard.customer.update;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import utilities.model.dashboard.customer.BankInfo;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerPhone;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class EditCustomerModel {

    int id;
    String fullName;
    List<CustomerPhone> phones;
    List<CustomerEmail> emails;
    String note;
    List<String> tags;
    int responsibleStaffUserId;
    String userStatus;
    String address;
    String address2;
    String countryCode;
    String locationCode;
    String districtCode;
    String city;
    String zipCode;
    String wardCode;
    String gender;
    String birthday;
    Integer partnerId;
    String companyName;
    String taxCode;
    List<CustomerPhone> backupPhones;
    List<CustomerEmail> backupEmails;
    List<BankInfo> bankInfos;
    String identityCard;
	
}