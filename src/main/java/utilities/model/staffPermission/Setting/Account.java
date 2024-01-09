package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class Account {
    private boolean purchasePackage;
    private boolean renewPackage;
    private boolean updateAccountInformation;
    private boolean viewAccountDetail;
    private boolean resetPassword;
}