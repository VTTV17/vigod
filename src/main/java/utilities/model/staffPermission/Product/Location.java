package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class Location{
	private boolean viewLocationDetail;
	private boolean deleteLocation;
	private boolean addLocation;
	private boolean editLocation;
	private boolean viewLocationList;
}