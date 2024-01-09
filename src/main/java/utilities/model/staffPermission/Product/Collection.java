package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class Collection{
	private boolean viewCollectionDetail;
	private boolean editTranslation;
	private boolean createCollection;
	private boolean editCollection;
	private boolean deleteCollection;
	private boolean viewCollectionList;
}