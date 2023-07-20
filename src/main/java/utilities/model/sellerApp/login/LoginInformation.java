package utilities.model.sellerApp.login;

import lombok.Data;

@Data
public class LoginInformation {
    private String email;
    private String phoneNumber;
    private String phoneCode;
    private String password;

}
