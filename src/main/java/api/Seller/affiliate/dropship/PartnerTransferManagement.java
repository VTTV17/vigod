package api.Seller.affiliate.dropship;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static api.Seller.affiliate.dropship.PartnerTransferManagement.PartnerTransferStatus.CANCELLED;
import static api.Seller.affiliate.dropship.PartnerTransferManagement.PartnerTransferStatus.RECEIVED;

public class PartnerTransferManagement {
    Logger logger = LogManager.getLogger(PartnerTransferManagement.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public PartnerTransferManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getAllPartnerTransferPath = "/itemservice/api/transfers/store/%s?searchBy=id&transferType=PARTNER&page=%s&size=100&sort=id,desc";

    @Data
    public static class PartnerTransferManagementInfo {
        private List<Integer> ids;
        private List<Integer> originBranchIds;
        private List<String> originBranchNames;
        private List<Integer> destinationBranchIds;
        private List<String> destinationBranchNames;
        private List<PartnerTransferStatus> statues;
    }

    Response getAllPartnerTransferManagementResponse(int pageIndex) {
        return api.get(getAllPartnerTransferPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public enum PartnerTransferStatus {
        READY_FOR_TRANSPORT, DELIVERING, RECEIVED, CANCELLED
    }

    public PartnerTransferManagementInfo getAllPartnerTransferInformation() {
        // init model
        PartnerTransferManagementInfo info = new PartnerTransferManagementInfo();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<String> originBranchNames = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();
        List<String> destinationBranchNames = new ArrayList<>();
        List<String> statues = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getAllPartnerTransferManagementResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            Response allProducts = getAllPartnerTransferManagementResponse(pageIndex);
            ids.addAll(allProducts.jsonPath().getList("id"));
            originBranchIds.addAll(allProducts.jsonPath().getList("originBranchId"));
            originBranchNames.addAll(allProducts.jsonPath().getList("originBranchName"));
            destinationBranchIds.addAll(allProducts.jsonPath().getList("destinationBranchId"));
            destinationBranchNames.addAll(allProducts.jsonPath().getList("destinationBranchName"));
            statues.addAll(allProducts.jsonPath().getList("status"));
        }
        info.setIds(ids);
        info.setOriginBranchIds(originBranchIds);
        info.setOriginBranchNames(originBranchNames);
        info.setDestinationBranchIds(destinationBranchIds);
        info.setDestinationBranchNames(destinationBranchNames);
        info.setStatues(statues.stream().map(PartnerTransferStatus::valueOf).toList());
        return info;
    }

    public List<Integer> getListProductIdInNotCompletedTransfer() {
        PartnerTransferManagementInfo info = getAllPartnerTransferInformation();
        List<Integer> transferIds = info.getIds();
        List<PartnerTransferStatus> statues = info.getStatues();

        // get list in-complete transfer id
        List<Integer> inCompleteTransferIds = transferIds.stream()
                .filter(transferId -> !(Objects.equals(statues.get(transferIds.indexOf(transferId)), CANCELLED)
                        || Objects.equals(statues.get(transferIds.indexOf(transferId)), RECEIVED)))
                .toList();

        // init transfer information api
        APIPartnerTransferDetail transferInformation = new APIPartnerTransferDetail(loginInformation);

        // get list itemId in in-complete transfer
        return inCompleteTransferIds.stream()
                .flatMap(transferId -> transferInformation.getItemIds(transferId).stream())
                .distinct()
                .toList();
    }
}
