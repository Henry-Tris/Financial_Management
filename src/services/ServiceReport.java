package services;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import entities.Deal;
import enumEntities.TypeStatus;

public class ServiceReport {

	private CashFlow cashFlow;
	private Stock stock;
	
	public ServiceReport(CashFlow cashFlow, Stock stock) {
		this.cashFlow = cashFlow;
		this.stock = stock;
	}
	
	public double totalIncome() {
		double sum = 0.0;
		for (Deal deal : cashFlow.getDeals()) {
			if (deal.getStatus() == TypeStatus.RECEITA) {
				sum = sum + deal.getValue();
			}
			
		}
		return sum;
	}
	
	public Map<YearMonth, Double> monthlyExpenses() {
	    Map<YearMonth, Double> result = new HashMap<>();

	    for (Deal deal : cashFlow.getDeals()) {
	        if (deal.getStatus() == TypeStatus.DESPESA) {
	            YearMonth month = YearMonth.from(deal.getDate());
	            double currentTotal = result.getOrDefault(month, 0.0);
	            result.put(month, currentTotal + deal.getValue());
	        }
	    }

	    return result;
	}
}
