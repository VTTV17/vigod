package utilities.model.staffPermission.OnlineStore;

import lombok.Data;

@Data
public class OnlineStore {
    private Theme theme = new Theme();
    private Page page = new Page();
    private Menu menu = new Menu();
    private Domain domain = new Domain();
    private Preferences preferences = new Preferences();
    private Blog blog = new Blog();
}