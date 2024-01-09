package utilities.model.staffPermission.Reservation;

import lombok.Data;

@Data
public class ReservationManagement{
	private boolean editReservation;
	private boolean cancelReservation;
	private boolean confirmReservation;
	private boolean completeReservation;
	private boolean viewReservationList;
	private boolean viewReservationDetail;
}