package utilities.model.dashboard.customer;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerAddress {
	String createdDate;
	String lastModifiedDate;
	Integer id;
	String address;
	String address2;
	String countryCode;
	String locationCode;
	String districtCode;
	String wardCode;
	String city;
	String zipCode;
	CustomerGeoLocation geoLocation;
}