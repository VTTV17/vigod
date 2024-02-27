package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class LotDate{
	private boolean viewLotDetail;
	private boolean importLot;
	private boolean viewLotList;
	private boolean editLot;
	private boolean enableProductLot;
	private boolean createLot;
	private boolean deleteLot;
}