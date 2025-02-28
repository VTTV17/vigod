package utilities.model;

import lombok.Data;

@Data
public class LoginCredentials {
	String country;
    String username;
    String password;
    
    public LoginCredentials(String country, String username, String password) {
		this.country = country;
		this.username = username;
		this.password = password;
	}
}