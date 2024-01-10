package utilities.model.staffPermission.Reservation;

import lombok.Data;

@Data
public class Reservation {
    private POSService pOSService = new POSService();
    private ReservationManagement reservationManagement = new ReservationManagement();
}