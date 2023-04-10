package utilities.model.dashboard.loginDashBoard;

import lombok.Data;

@Data
public class LoginDashboardInfo {
    private String accessToken;
    private String refreshToken;
    private int storeID;
    private String storeName;
    private int sellerID;
}
