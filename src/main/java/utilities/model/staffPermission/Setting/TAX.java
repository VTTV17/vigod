package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class TAX{
	private boolean deleteTAX;
	private boolean createImportingTAX;
	private boolean updateTAXConfiguration;
	private boolean viewTAXList;
	private boolean createSellingTAX;
}