package utilities.model.dashboard.setting.languages;

import lombok.Data;

@Data
public class CreatedLanguage {
	int id;
	int storeId;
	String langCode;
	String langIcon;
	String langName;
}