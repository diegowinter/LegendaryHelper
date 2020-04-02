package db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class Connector {
	
	private final String driver = "com.mysql.cj.jdbc.Driver";
	
	public Connection getConnection() {
		System.out.println("LOG: Opening connection to database");
		try {
			Class.forName(driver);
			return DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("DB_USERNAME"), 
					System.getenv("DB_PASSWORD"));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ERROR: Failed to open DB connection");
		}
	}
	
	public boolean isConnected(Connection c) {
		if(c != null) return true;
		else return false;
	}
	
	public void closeConnection(Connection c) {
		try {
			if(c != null) c.close();
			System.out.println("LOG: Database connection closed");
		} catch (SQLException e) {
			System.out.println("ERROR: Failed to close: " + e.getMessage());
		}
	}
	
}
