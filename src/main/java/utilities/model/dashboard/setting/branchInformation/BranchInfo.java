package utilities.model.dashboard.setting.branchInformation;

import lombok.Data;

import java.util.List;

@Data
public class BranchInfo {
    private List<String> branchCode;
    private List<String> branchAddress;
    private List<String> wardCode;
    private List<String> districtCode;
    private List<String> cityCode;
    private List<String> phoneNumberFirst;
    private List<String> countryCode;
    private List<Boolean> isDefaultBranch;
    private List<Integer> branchID;
    private List<String> branchName;
    private List<Boolean> isHideOnStoreFront;
    private List<String> allBranchStatus;
    private List<String> activeBranches;
}
