package utilities.model.staffPermission.Analytics;

import lombok.Data;

@Data
public class Analytics {
    private OrdersAnalytics ordersAnalytics = new OrdersAnalytics();
    private ReservationAnalytics reservationAnalytics = new ReservationAnalytics();
}