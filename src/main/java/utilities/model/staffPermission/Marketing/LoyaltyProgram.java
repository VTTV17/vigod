package utilities.model.staffPermission.Marketing;

import lombok.Data;

@Data
public class LoyaltyProgram{
	private boolean createMembership;
	private boolean collocateMembership;
	private boolean viewListMembership;
	private boolean editMembership;
	private boolean viewMembershipDetail;
	private boolean deleteMembership;
}