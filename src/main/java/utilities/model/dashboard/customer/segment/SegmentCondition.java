package utilities.model.dashboard.customer.segment;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class SegmentCondition {
	String name;
	String value;
}