package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class StoreLanguage{
	private boolean unpublishLanguage;
	private boolean publishLanguage;
	private boolean updateTranslation;
	private boolean addLanguage;
	private boolean removeLanguage;
	private boolean purchaseLanguagePackage;
	private boolean renewLanguagePackage;
	private boolean changeDefaultLanguage;
}