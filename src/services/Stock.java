package services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import entities.Product;

public class Stock {

	private Map<String, Product> products;
	
	public Stock() {
		
	}

	public Stock(Map<String, Product> products) {
		super();
		this.products = new HashMap<>();
	}
	
	public void addProduct(Product product) {
		if (products.containsKey(product.getName())) {
			Product existing = products.get(product.getName());
			double oldTotalCost = existing.getCostPrice() * existing.getQuantity();
			double newTotalCost = product.getCostPrice() * product.getQuantity();
			int totalQuantity = existing.getQuantity() + product.getQuantity();
			double avgCost = (oldTotalCost + newTotalCost) / totalQuantity;
			existing.setCostPrice(avgCost);
			existing.setQuantity(totalQuantity);
		} else {
			products.put(product.getName(), product);
		}
	}
	
	public Product findProduct(String name) {
	    return products.get(name);
	}
	
	public void removeProduct(String name) {
	    products.remove(name);
	}
	
	public Collection<Product> getAllProducts() {
	    return products.values();
	}
}
