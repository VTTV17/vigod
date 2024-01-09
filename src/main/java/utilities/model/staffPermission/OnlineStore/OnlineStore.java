package utilities.model.staffPermission.OnlineStore;

import lombok.Data;

@Data
public class OnlineStore{
	private Theme theme;
	private Page page;
	private Menu menu;
	private Domain domain;
	private Preferences preferences;
	private Blog blog;
}