package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class BranchManagement{
	private boolean purchaseBranch;
	private boolean upgradeBranch;
	private boolean activeDeactivateBranch;
	private boolean renewBranch;
	private boolean updateBranchInformation;
	private boolean addBranch;
	private boolean viewBranchInformation;
}