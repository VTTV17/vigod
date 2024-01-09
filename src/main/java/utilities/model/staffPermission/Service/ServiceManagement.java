package utilities.model.staffPermission.Service;

import lombok.Data;

@Data
public class ServiceManagement{
	private boolean createService;
	private boolean activateService;
	private boolean deactivateService;
	private boolean deleteService;
	private boolean viewListService;
	private boolean viewListCreatedService;
	private boolean viewServiceDetail;
	private boolean editService;
}