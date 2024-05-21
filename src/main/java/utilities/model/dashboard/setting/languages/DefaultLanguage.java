package utilities.model.dashboard.setting.languages;

import lombok.Data;

@Data
public class DefaultLanguage {
	int id;
	int storeId;
	String langCode;
	Boolean isDefault;
	Boolean published;
	Boolean isInitial;
	String langIcon;
	String langName;
}
