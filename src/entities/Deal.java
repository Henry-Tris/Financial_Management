package entities;

import java.time.LocalDate;

import enumEntities.Context;
import enumEntities.PaymentStatus;
import enumEntities.TypeStatus;

public class Deal {

	private double value;
	private LocalDate date;
	private TypeStatus status;
	private String name;
	private Category category;
	private Context context;
	private PaymentStatus payment;
	
	public Deal() {
		
	}

	public Deal(double value, LocalDate date, TypeStatus status, String name, Category category, Context context,
			PaymentStatus payment) {
		this.value = value;
		this.date = date;
		this.status = status;
		this.name = name;
		this.category = category;
		this.context = context;
		this.payment = payment;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public TypeStatus getStatus() {
		return status;
	}

	public void setStatus(TypeStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public PaymentStatus getPayment() {
		return payment;
	}

	public void setPayment(PaymentStatus payment) {
		this.payment = payment;
	}
}
