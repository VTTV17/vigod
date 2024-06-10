package utilities.model.dashboard.customer;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerEmail {
	Integer id;
	Integer customerId;
	String email;
	String emailName;
	String emailType;
}