package utilities.model.staffPermission.OnlineStore;

import lombok.Data;

@Data
public class Theme{
	private boolean viewThemeLibrary;
	private boolean editTheme;
	private boolean publishTheme;
	private boolean addNewTheme;
	private boolean deleteTheme;
	private boolean viewThemeDetail;
	private boolean unpublishTheme;
}