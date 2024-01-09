package utilities.model.staffPermission.Service;

import lombok.Data;

@Data
public class ServiceCollection{
	private boolean viewCollectionDetail;
	private boolean createCollection;
	private boolean editCollection;
	private boolean deleteCollection;
	private boolean viewCollectionList;
}