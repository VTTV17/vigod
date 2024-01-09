package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class Permission{
	private boolean deletePermissionGroup;
	private boolean removeStaffFromPermissionGroup;
	private boolean addStaffToPermissionGroup;
	private boolean viewPermissionGroupList;
	private boolean createPermissionGroup;
	private boolean editPermissionGroup;
}