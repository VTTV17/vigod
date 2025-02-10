package business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import utilities.data.DataGenerator;
import utilities.enums.Domain;
import utilities.enums.newpackage.NewPackage;

/**
 * This business class handles the logic of GoSELL plan pricing such as calculating refund amount, remaining day, total package price and so on
 */

public class PlanPricingBusiness {

	static BigDecimal workoutRefund(NewPackage plan, int years, long remainingDays, int finalScale) {

		final int rudimentaryScale = 10;
		
		BigDecimal priceByYear = getBasePriceByYears(plan, years);
		
		BigDecimal refund = priceByYear.divide(BigDecimal.valueOf(years), rudimentaryScale, RoundingMode.HALF_UP)
				.divide(BigDecimal.valueOf(365), rudimentaryScale, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(remainingDays));

		System.out.println("Refund amount rounded and scaled by %s: %s".formatted(rudimentaryScale, refund));

		refund = refund.setScale(finalScale, RoundingMode.HALF_UP);

		System.out.println("Refund amount rounded and scaled by %s: %s".formatted(finalScale, refund));

		return refund;
	}	
	
	public static long workoutRemainingDays(Instant expiryDate) {
		long remainingDayCount = ChronoUnit.DAYS.between(Instant.now().plus(1, ChronoUnit.DAYS), expiryDate);
		System.out.println("Remaining days: " + remainingDayCount);
		return remainingDayCount;
	}	

	public static int deducePeriod(Instant registeredDate, Instant expiryDate) {
		long period = ChronoUnit.YEARS.between(registeredDate.atZone(ZoneOffset.UTC), expiryDate.atZone(ZoneOffset.UTC));
		System.out.println("Plan Period: " + period);
		return (int) period;
	}	

	static BigDecimal workoutVAT(BigDecimal pricePerYear) {
		BigDecimal vat = pricePerYear.multiply(BigDecimal.valueOf(0.1));
		System.out.println("VAT: " + vat.toPlainString());
		return vat;
	}	
	
	//Used for calculate plan price. Will remove later
	public static BigDecimal getBasePriceByYears(NewPackage plan, int years) {
		BigDecimal price = DataGenerator.getPlanInfo(plan).getTotalPrice().get(0).multiply(BigDecimal.valueOf(years));
		System.out.println("Price of %s for %s year(s): %s".formatted(plan, years, price));
		return price;
	}	

	public static BigDecimal calculateRefund(String country, NewPackage plan, int years, long remainingDays) {
		if (country.contentEquals("Vietnam")) {
			return workoutRefund(plan, years, remainingDays, 0);
		}
		return workoutRefund(plan, years, remainingDays, 2);
	}

	static BigDecimal resetRefund(BigDecimal priceIncludingTax, BigDecimal refundAmount) {
		return (refundAmount.compareTo(priceIncludingTax) != -1) ? priceIncludingTax : refundAmount;
	}	
	public static BigDecimal resetRefund(Domain domain, NewPackage newPlan, int years, BigDecimal refundAmount) {
		BigDecimal basePriceIncludingTax = calculatePriceIncludingTax(domain, newPlan, years);
		return resetRefund(basePriceIncludingTax, refundAmount);
	}	
	
	public static BigDecimal calculatePriceIncludingTax(Domain domain, NewPackage plan, int years) {
		BigDecimal priceByYears = getBasePriceByYears(plan, years);
		
		BigDecimal priceIncludingTax = domain.equals(Domain.BIZ) ? priceByYears : priceByYears.add(workoutVAT(priceByYears));
		
		System.out.println("Plan price inclusive of tax: %s".formatted(priceIncludingTax));
		
		return priceIncludingTax;
	}
	
	public static BigDecimal calculateFinalTotalPrice(Domain domain, NewPackage newPlan, int years, BigDecimal refundAmount) {
		
		BigDecimal basePriceIncludingTax = calculatePriceIncludingTax(domain, newPlan, years);
		
		BigDecimal newRefund = resetRefund(basePriceIncludingTax, refundAmount);   
		
		BigDecimal totalPrice = basePriceIncludingTax.subtract(newRefund);
		
		System.out.println("Total amount: " + totalPrice);
		
		return totalPrice;
	}
}
