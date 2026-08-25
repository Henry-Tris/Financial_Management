package entities;

public class TaxRule {

	private String name;
	private double percentage;
	
	public TaxRule() {
		
	}

	public TaxRule(String name, double percentage) {
		this.name = name;
		this.percentage = percentage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	public double calculateTax(double cashIncome) {
		return cashIncome * percentage / 100.0;
	}
}
