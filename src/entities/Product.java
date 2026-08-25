package entities;

public class Product {

	private String name;
	private double costPrice;
	private double salePrice;
	private int quantity;
	
	public Product() {
		
	}

	public Product(String name, double costPrice, double salePrice, int quantity) {
		this.name = name;
		this.costPrice = costPrice;
		this.salePrice = salePrice;
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
