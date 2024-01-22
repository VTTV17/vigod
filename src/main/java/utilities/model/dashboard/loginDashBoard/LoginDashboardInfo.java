package utilities.model.dashboard.loginDashBoard;

import lombok.Data;

import java.util.List;

@Data
public class LoginDashboardInfo {
    private String accessToken;
    private String refreshToken;
    private int storeID;
    private String storeName;
    private int sellerID;
    private String staffPermissionToken;
    List<String> userRole;
    List<Integer> assignedBranches;
}
