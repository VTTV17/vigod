package utilities.model.staffPermission.Supplier;

import lombok.Data;

@Data
public class Debt{
	private boolean viewDebtHistory;
	private boolean editADebt;
	private boolean deleteADebt;
	private boolean publicADebt;
	private boolean createANewDebt;
	private boolean makeADebtRepayment;
}