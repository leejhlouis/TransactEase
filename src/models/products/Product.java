package models.products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import builder.ProductBuilder;
import database.DatabaseConnection;

public class Product {
	private String id, name;
	private int price, stock;
	private DatabaseConnection con = DatabaseConnection.getInstance();
	
	public Product() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
	private Product map(ResultSet rs) {
		String id, name;
		int price, stock;
		
		try {
			id = rs.getString("id");
			name = rs.getString("name");
			price = rs.getInt("price");
			stock = rs.getInt("stock");
			
			ProductBuilder pb = new ProductBuilder();
			return pb.setId(id).setName(name).setPrice(price).setStock(stock).build();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Vector<Product> getAll(){
		String query = "SELECT * FROM Products";
		ResultSet rs = con.executeQuery(query);
		Vector<Product> products = new Vector<>();
		try {
			while(rs.next()) {
				Product product = map(rs);
				products.add(product);
			}
			return products;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean insert() {
		String query = String.format("INSERT INTO Products (id, name, price, stock) VALUES (?, ?, ?, ?)");
		PreparedStatement ps = con.prepareStatement(query);
			
		try {
			ps.setString(1, id);
			ps.setString(2, name);
			ps.setInt(3, price);
			ps.setInt(4, stock);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean update() {
		String query = String.format("UPDATE Products SET name=?, price=?, stock=? WHERE id=?");
		PreparedStatement ps = con.prepareStatement(query);
			
		try {
			ps.setString(1, name);
			ps.setInt(2, price);
			ps.setInt(3, stock);
			ps.setString(4, id);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	public boolean delete() {
		String query = String.format("DELETE FROM Products WHERE id=?");
		PreparedStatement ps = con.prepareStatement(query);
		
		try {
			ps.setString(1, id);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public Product getProduct(String id) {
		String query = String.format("SELECT * FROM Products WHERE id=?");
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			Product product = null;
			if(rs.first()) {
				product = map(rs);
			}	
			return product;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
