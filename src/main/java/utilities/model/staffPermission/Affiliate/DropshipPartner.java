package utilities.model.staffPermission.Affiliate;

import lombok.Data;

@Data
public class DropshipPartner{
	private boolean addDropshipPartner;
	private boolean exportPartner;
	private boolean viewDropshipPartnerDetail;
	private boolean editDropshipPartner;
	private boolean downloadExportedFile;
	private boolean viewDropshipPartnerList;
}