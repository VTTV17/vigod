package utilities.model.dashboard.customer.segment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CreateSegment {
	String name;
	String matchCondition;
	List<SegmentCondition> conditions;
}