package entities;

import java.time.LocalDate;

import enumEntities.PaymentStatus;

public class Bills {

	private String name;
	private double value;
	private LocalDate limit;
	private PaymentStatus payment;
	
	public Bills() {
		
	}

	public Bills(String name, double value, LocalDate limit, PaymentStatus payment) {
		this.name = name;
		this.value = value;
		this.limit = limit;
		this.payment = payment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public LocalDate getLimit() {
		return limit;
	}

	public void setLimit(LocalDate limit) {
		this.limit = limit;
	}

	public PaymentStatus getPayment() {
		return payment;
	}

	public void setPayment(PaymentStatus payment) {
		this.payment = payment;
	}
}
