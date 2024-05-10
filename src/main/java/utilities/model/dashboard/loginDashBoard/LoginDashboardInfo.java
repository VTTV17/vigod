package utilities.model.dashboard.loginDashBoard;

import lombok.Data;

import java.util.List;

@Data
public class LoginDashboardInfo {
    private String accessToken;
    private String refreshToken;
    private int storeID;
    private String storeName;
    private int userId;
    private String staffPermissionToken;
    private List<String> userRole;
    private List<Integer> assignedBranchesIds;
    private List<String> assignedBranchesNames;
    private int ownerId;
    private String userName;
}
