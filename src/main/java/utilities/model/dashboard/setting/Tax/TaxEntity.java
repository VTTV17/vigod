package utilities.model.dashboard.setting.Tax;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class TaxEntity {
	int id;
	String name;
	int rate;
	int storeId;
	boolean useDefault;
	String taxType;
	String description;
}