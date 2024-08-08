package utilities.model.dashboard.customer;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CustomerPhone {
	Integer id;
	Integer customerId;
	String phoneCode;
	String phoneName;
	String phoneNumber;
	String phoneType;
}