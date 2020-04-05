package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.Connector;
import exceptions.DuplicatedKeywordException;
import exceptions.NonexistentKeywordException;
import model.text_responses.Keyword;

public class DAOKeyword {
	
	private Connector connector = new Connector();
	
	public int add(String word) throws SQLException, DuplicatedKeywordException {
		try {
			search(word);
			throw new DuplicatedKeywordException();
		} catch (NonexistentKeywordException e) {
			Connection connection = connector.getConnection();
			PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO keywords(id, keyword)"
					+ "values (0, ?)");
			prepStatement.setString(1, word);
			prepStatement.execute();
			prepStatement.close();
			connector.closeConnection(connection);
			try {
				return search(word).getId();
			} catch (NonexistentKeywordException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public Keyword search(String word) throws SQLException, NonexistentKeywordException {
		Connection connection = connector.getConnection();
		Keyword keyword = new Keyword(null, 0);
		
		PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM " 
				+ System.getenv("DB_NAME") + ".keywords WHERE keyword = ?");
		prepStatement.setString(1, word);
		
		ResultSet rs = prepStatement.executeQuery();
		if(rs.next()) {
			keyword.setKeyword(rs.getString("keyword"));
			keyword.setId(rs.getInt("id"));
		} else {
			prepStatement.close();
			connector.closeConnection(connection);
			throw new NonexistentKeywordException();
		}
		
		prepStatement.close();
		connector.closeConnection(connection);
		
		return keyword;
	}
	
	public ArrayList<Keyword> searchKeywordSet(ArrayList<String> words) throws SQLException {
		Connection connection = connector.getConnection();
		Keyword keyword = null;
		ArrayList<Keyword> keywordSet = new ArrayList<Keyword>();
		
		PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM " 
				+ System.getenv("DB_NAME") + ".keywords WHERE keyword = ?");
		
		for (String word : words) {
			prepStatement.setString(1, word);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()) {
				keyword = new Keyword(null, 0);
				keyword.setKeyword(rs.getString("keyword"));
				keyword.setId(rs.getInt("id"));
				keywordSet.add(keyword);
			}
		}
		
		prepStatement.close();
		connector.closeConnection(connection);
		
		return keywordSet;
	}
	
	public int delete(String word) throws NonexistentKeywordException, SQLException {
		Connection connection = connector.getConnection();
		Keyword keyword = search(word);
		
		PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM " 
				+ System.getenv("DB_NAME") + ".keywords WHERE id = ?");
		prepStatement.setInt(1, keyword.getId());
		prepStatement.execute();
		prepStatement.close();
		connector.closeConnection(connection);
		
		return keyword.getId();
	}
	
	public void update() {
		
	}
	
}
