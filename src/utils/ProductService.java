package utils;

import java.util.Vector;

import builder.ProductBuilder;
import models.products.Product;

public class ProductService {
	private static ProductService instance;
	private String errorMsg = "";
	
	public static ProductService getInstance() {
		if(instance == null) {
			instance = new ProductService();
		}
		
		return instance;
	}

	public Vector<Product> getAllProducts() {
		return new Product().getAll();
	}

	public String getErrorMsg() {
		return errorMsg;
	}
	
	private String generateId() {
		return String.format("P%03d", getAllProducts().size() + 1);
	}

	public boolean insertProduct(String name, String price, String stock) {
		if (name.isEmpty() || price.isEmpty() || stock.isEmpty()) {
			errorMsg = "All fields must be filled!";
			return false;
		}
		else if(name.length() < 5) {
			errorMsg = "Name length must be at least 5";
			return false;
		}
		else {
			int priceInt = -1, stockInt = -1;
			try {
				priceInt = Integer.parseInt(price);
				stockInt = Integer.parseInt(stock);
			} catch (Exception e) {
				errorMsg = "Price and stock must be numeric!";
				return false;
			}
			
			ProductBuilder pb = new ProductBuilder();
			System.out.println(generateId());
			Product p = pb.setId(generateId()).setName(name).setPrice(priceInt).setStock(stockInt).build();
			boolean inserted = p.insert();
			
			if(!inserted) {
				errorMsg = "Insert failed!";
			}
			return inserted;
		}
	}
	
	public boolean updateProduct(String id, String name, String price, String stock) {
		if (id.isEmpty() || name.isEmpty() || price.isEmpty() || stock.isEmpty()) {
			errorMsg = "All fields must be filled!";
			return false;
		}
		else if(name.length() < 5) {
			errorMsg = "Name length must be at least 5";
			return false;
		}
		else {
			int priceInt = -1, stockInt = -1;
			try {
				priceInt = Integer.parseInt(price);
				stockInt = Integer.parseInt(stock);
			} catch (Exception e) {
				errorMsg = "Price and stock must be numeric!";
				return false;
			}
			
			ProductBuilder pb = new ProductBuilder();
			Product p = pb.setId(id).setName(name).setPrice(priceInt).setStock(stockInt).build();
			boolean updated = p.update();
			if(!updated) {
				errorMsg = "Update failed!";
				return updated;
			}
		}
		
		return true;
	}
	
	public boolean deleteSong(String id) {
		if(id.isEmpty()) {
			errorMsg = "ID must be filled!";
			return false;
		}
		
		ProductBuilder pb = new ProductBuilder();
		Product p = pb.setId(id).build();
		
		boolean deleted = p.delete();
		
		if(!deleted) {
			errorMsg = "Delete failed!";
		}
		
		return deleted;
	}
}
