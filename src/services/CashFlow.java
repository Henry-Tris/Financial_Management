package services;

import java.util.ArrayList;
import java.util.List;

import entities.Bills;
import entities.Deal;
import enumEntities.PaymentStatus;
import enumEntities.TypeStatus;

public class CashFlow {

	private List<Deal> deals;
	private List<Bills> bills;

	public CashFlow() {
		this.deals = new ArrayList<>();
		this.bills = new ArrayList<>();
	}

	public void addDeal(Deal deal) {
		deals.add(deal);
	}

	public void addBill(Bills bill) {
		bills.add(bill);
	}

	public double currentBalance() {
		double balance = 0.0;

		for (Deal deal : deals) {
			if (deal.getPayment() == PaymentStatus.PAID) {
				if (deal.getStatus() == TypeStatus.RECEITA) {
					balance += deal.getValue();
				} else {
					balance -= deal.getValue();
				}
			}
		}

		return balance;
	}

	public double expectedIncome() {
		double total = 0;

		for (Deal deal : deals) {
			if (deal.getStatus() == TypeStatus.RECEITA && deal.getPayment() == PaymentStatus.PENDENT) {
				total += deal.getValue();
			}
		}

		return total;
	}

	public double expectedExpenses() {
		double total = 0;

		for (Deal deal : deals) {
			if (deal.getStatus() == TypeStatus.DESPESA && deal.getPayment() == PaymentStatus.PENDENT) {
				total += deal.getValue();
			}
		}

		for (Bills bill : bills) {
			if (bill.getPayment() == PaymentStatus.PENDENT) {
				total += bill.getValue();
			}
		}

		return total;
	}

	public List<Deal> getDeals() {
		return deals;
	}

	public List<Bills> getBills() {
		return bills;
	}
}
