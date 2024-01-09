package utilities.model.staffPermission.Customer;

import lombok.Data;

@Data
public class Segment{
	private boolean viewSegmentList;
	private boolean deleteSegment;
	private boolean createSegment;
	private boolean editSegment;
}