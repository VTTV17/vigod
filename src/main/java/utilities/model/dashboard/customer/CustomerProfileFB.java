package utilities.model.dashboard.customer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerProfileFB {
	List<CustomerEmail> emails;
	List<CustomerEmail> backupEmails;
	List<CustomerPhone> phones;
	List<CustomerPhone> backupPhones;
	Integer id;
	String fullName;
	String userId;
	String email;
	String phone;
	Boolean guest;
	CustomerAddress customerAddress;
	String birthday;
	Integer partnerId;
	String accountType;
	Integer initDebt;
	String partnerType;
	Boolean inMerge;
}