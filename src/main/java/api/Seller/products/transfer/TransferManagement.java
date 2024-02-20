package api.Seller.products.transfer;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TransferManagement {
    String getAllTransferPath = "/itemservice/api/transfers/store/%s?searchBy=id&transferType=BRANCH&page=%s&size=100&sort=id,desc";
    LoginDashboardInfo info;
    LoginInformation loginInformation;
    API api = new API();

    public TransferManagement(LoginInformation loginInformation) {
        info = new Login().getInfo(loginInformation);
        this.loginInformation = loginInformation;
    }

    @Data
    public static class TransferInfo {
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();
    }

    Response getTransferResponse(int page) {
        return api.get(getAllTransferPath.formatted(info.getStoreID(), page), info.getAccessToken());
    }

    public TransferInfo getAllTransferInfo() {
        // init model
        TransferInfo info = new TransferInfo();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getTransferResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            JsonPath jPath = getTransferResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jPath.getList("id"));
            originBranchIds.addAll(jPath.getList("originBranchId"));
            destinationBranchIds.addAll(jPath.getList("destinationBranchId"));
        }
        // set transfer id
        info.setIds(ids);

        // set origin branch id
        info.setOriginBranchIds(originBranchIds);

        // set destination branch id
        info.setDestinationBranchIds(destinationBranchIds);

        return info;
    }

    public List<Integer> getTransferIdWithOriginAndDestinationBranchIsNotInListAssignedBranches(List<Integer> assignedBranches) {
        TransferInfo info = getAllTransferInfo();
        return IntStream.range(0, info.getIds().size())
                .filter(index -> !(assignedBranches.contains(info.getOriginBranchIds().get(index))
                        || assignedBranches.contains(info.getDestinationBranchIds().get(index))))
                .mapToObj(index -> info.getIds().get(index))
                .toList();
    }
}
