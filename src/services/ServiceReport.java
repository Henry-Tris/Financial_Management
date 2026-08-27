package services;

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
}
