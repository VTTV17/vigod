package utilities.model.dashboard.customer.segment;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class SegmentList {
	Integer id;
	String name;
	Integer storeId;
	String matchCondition;
	Integer userCount;
}