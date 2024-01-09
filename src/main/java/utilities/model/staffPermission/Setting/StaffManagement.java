package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class StaffManagement{
	private boolean editStaff;
	private boolean deleteStaff;
	private boolean viewStaffList;
	private boolean addStaff;
	private boolean activeDeactivateStaff;
}