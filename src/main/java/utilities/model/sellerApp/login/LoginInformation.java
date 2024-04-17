package utilities.model.sellerApp.login;

import lombok.Data;

@Data
public class LoginInformation {
    private String email;
    private String phoneNumber;
    private String phoneCode;
    private String password;
    public LoginInformation() {
    }
    public LoginInformation(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginInformation(String phoneCode, String phoneNumber, String password) {
        this.phoneCode = phoneCode;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

}
