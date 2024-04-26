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
import java.util.Objects;

import static api.Seller.products.transfer.TransferManagement.TransferStatus.*;

public class TransferManagement {
    String getAllTransferPath = "/itemservice/api/transfers/store/%s?searchBy=id&transferType=BRANCH&page=%s&size=100&sort=id,desc";
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();

    public TransferManagement(LoginInformation loginInformation) {
        loginInfo = new Login().getInfo(loginInformation);
        this.loginInformation = loginInformation;
    }
    
    public enum TransferStatus {
        READY_FOR_TRANSPORT, DELIVERING, RECEIVED, CANCELLED
    }

    @Data
    public static class TransferInfo {
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();
        List<TransferStatus> statues = new ArrayList<>();
    }

    Response getTransferResponse(int page) {
        return api.get(getAllTransferPath.formatted(loginInfo.getStoreID(), page), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public TransferInfo getAllTransferInfo() {
        // init model
        TransferInfo info = new TransferInfo();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getTransferResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jPath = getTransferResponse(pageIndex).jsonPath();
            ids.addAll(jPath.getList("id"));
            originBranchIds.addAll(jPath.getList("originBranchId"));
            destinationBranchIds.addAll(jPath.getList("destinationBranchId"));
            statues.addAll(jPath.getList("status"));
        }
        // set transfer id
        info.setIds(ids);

        // set origin branch id
        info.setOriginBranchIds(originBranchIds);

        // set destination branch id
        info.setDestinationBranchIds(destinationBranchIds);

        // set transfer status
        info.setStatues(statues.stream().map(TransferStatus::valueOf).toList());

        return info;
    }

    public List<Integer> getListProductIdInNotCompletedTransfer() {
        TransferInfo info = getAllTransferInfo();
        List<Integer> transferIds = info.getIds();
        List<TransferStatus> statues = info.getStatues();

        // get list in-complete transfer id
        List<Integer> inCompleteTransferIds = transferIds.stream()
                .filter(transferId -> !(Objects.equals(statues.get(transferIds.indexOf(transferId)), CANCELLED)
                        || Objects.equals(statues.get(transferIds.indexOf(transferId)), RECEIVED)))
                .toList();

        // init transfer information api
        APITransferDetail transferInformation = new APITransferDetail(loginInformation);

        // get list itemId in in-complete transfer
        return inCompleteTransferIds.stream()
                .flatMap(transferId -> transferInformation.getItemIds(transferId).stream())
                .distinct()
                .toList();
    }

    public List<Integer> getListTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // staff can not view detail of transfer
        // that have origin and destination branch are not in assigned branches
        List<Integer> transferIds = info.getIds();
        List<Integer> originBranchIds = info.getOriginBranchIds();
        List<Integer> destinationBranchIds = info.getDestinationBranchIds();
        return transferIds.stream()
                .filter(transferId -> assignedBranchIds.contains(originBranchIds.get(transferIds.indexOf(transferId)))
                        || assignedBranchIds.contains(destinationBranchIds.get(transferIds.indexOf(transferId))))
                .toList();

    }

    public int getViewPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        List<Integer> transferIds = getListTransferId(assignedBranchIds, transferInfo);
        return transferIds.isEmpty() ? 0 : transferIds.get(0);
    }

    public int getNoViewPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // staff can not view detail of transfer
        // that have origin and destination branch are not in assigned branches
        List<Integer> transferIds = info.getIds();
        List<Integer> originBranchIds = info.getOriginBranchIds();
        List<Integer> destinationBranchIds = info.getDestinationBranchIds();
        return transferIds.stream().mapToInt(transferId -> transferId)
                .filter(transferId -> !(assignedBranchIds.contains(originBranchIds.get(transferIds.indexOf(transferId)))
                        || assignedBranchIds.contains(destinationBranchIds.get(transferIds.indexOf(transferId)))))
                .findFirst()
                .orElse(0);
    }

    public int getConfirmShipGoodsPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // when assigned branches NOT contains origin branch in transfer
        // => staff can not confirm ship goods
        List<Integer> transferIds = info.getIds();
        List<Integer> originBranchIds = info.getOriginBranchIds();
        List<TransferStatus> statues = info.getStatues();
        return transferIds.stream().filter(transferId -> Objects.equals(statues.get(transferIds.indexOf(transferId)), READY_FOR_TRANSPORT)
                && assignedBranchIds.contains(originBranchIds.get(transferIds.indexOf(transferId)))).findFirst().orElse(0);
    }


    public int getNoConfirmShipGoodsPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // when assigned branches NOT contains origin branch in transfer
        // => staff can not confirm ship goods
        List<Integer> transferIds = info.getIds();
        List<Integer> originBranchIds = info.getOriginBranchIds();
        List<Integer> destinationBranchIds = info.getDestinationBranchIds();
        List<TransferStatus> statues = info.getStatues();
        return transferIds.stream().mapToInt(transferId -> transferId)
                .filter(transferId -> Objects.equals(statues.get(transferIds.indexOf(transferId)), READY_FOR_TRANSPORT)
                        && !assignedBranchIds.contains(originBranchIds.get(transferIds.indexOf(transferId)))
                        && assignedBranchIds.contains(destinationBranchIds.get(transferIds.indexOf(transferId))))
                .findFirst()
                .orElse(0);
    }

    public int getConfirmReceiveGoodsPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // when assigned branches NOT contains destination branch in transfer
        // => staff can not confirm received goods
        List<Integer> transferIds = info.getIds();
        List<Integer> destinationBranchIds = info.getDestinationBranchIds();
        List<TransferStatus> statues = info.getStatues();
        return transferIds.stream().mapToInt(transferId -> transferId)
                .filter(transferId -> Objects.equals(statues.get(transferIds.indexOf(transferId)), DELIVERING)
                        && assignedBranchIds.contains(destinationBranchIds.get(transferIds.indexOf(transferId))))
                .findFirst()
                .orElse(0);
    }

    public int getNoConfirmReceiveGoodsPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // when assigned branches NOT contains destination branch in transfer
        // => staff can not confirm received goods
        List<Integer> transferIds = info.getIds();
        List<Integer> originBranchIds = info.getOriginBranchIds();
        List<Integer> destinationBranchIds = info.getDestinationBranchIds();
        List<TransferStatus> statues = info.getStatues();
        return transferIds.stream().mapToInt(transferId -> transferId)
                .filter(transferId -> Objects.equals(statues.get(transferIds.indexOf(transferId)), DELIVERING)
                        && assignedBranchIds.contains(originBranchIds.get(transferIds.indexOf(transferId)))
                        && !assignedBranchIds.contains(destinationBranchIds.get(transferIds.indexOf(transferId))))
                .findFirst()
                .orElse(0);
    }

    public int getCancelTransferPermissionTransferId(List<Integer> assignedBranchIds, TransferInfo... transferInfo) {
        TransferInfo info = (transferInfo.length != 0) ? transferInfo[0] : getAllTransferInfo();
        // when assigned branches NOT contains destination branch in transfer
        // => staff can not confirm received goods
        List<Integer> transferIds = info.getIds();
        List<Integer> destinationBranchIds = info.getDestinationBranchIds();
        List<TransferStatus> statues = info.getStatues();
        return transferIds.stream().mapToInt(transferId -> transferId)
                .filter(transferId -> !(Objects.equals(statues.get(transferIds.indexOf(transferId)), CANCELLED)
                        || Objects.equals(statues.get(transferIds.indexOf(transferId)), RECEIVED))
                        && assignedBranchIds.contains(destinationBranchIds.get(transferIds.indexOf(transferId))))
                .findFirst()
                .orElse(0);
    }
}
