package utilities.model.staffPermission.CallCenter;

import lombok.Data;

@Data
public class CallCenter{
	private boolean makeACall;
	private boolean viewCallHistory;
	private boolean renewPackage;
	private boolean pickUpCall;
	private boolean buyPackage;
	private boolean upgradePackage;
}