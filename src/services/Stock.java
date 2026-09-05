package services;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import entities.Product;

public class Stock {

	private Map<String, Product> products;
	
	public Stock() {
	    this.products = new LinkedHashMap<>();
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
			existing.setSalePrice(product.getSalePrice());
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
	
	public boolean sellProduct(String name, int quantity) {
	    Product product = products.get(name);

	    if (product == null) {
	        return false;
	    }

	    if (product.getQuantity() < quantity) {
	        return false;
	    }

	    product.setQuantity(product.getQuantity() - quantity);
	    return true;
	}
}