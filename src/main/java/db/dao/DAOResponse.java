package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.Connector;
import model.text_responses.Response;

public class DAOResponse {
	
	private Connector connector = new Connector();
	
	public void add() {
		
	}
	
	public void addResponseSet(int keywordId, ArrayList<String> responses) throws SQLException {
		Connection connection = connector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO responses(keyword_id, response)"
				+ "values (?, ?)");
		for (String response : responses) {
			prepStatement.setInt(1, keywordId);
			prepStatement.setString(2, response);
			prepStatement.execute();
		}
		prepStatement.close();
		connector.closeConnection(connection);
	}
	
	public ArrayList<Response> searchResponseSet(int keywordId) throws SQLException {
		Connection connection = connector.getConnection();
		Response response = null;
		ArrayList<Response> responses = new ArrayList<Response>();
		PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM "
				+ System.getenv("DB_NAME") + ".responses WHERE keyword_id = ?");
		prepStatement.setInt(1, keywordId);
		ResultSet rs = prepStatement.executeQuery();
		while(rs.next()) {
			response = new Response(null, 0);
			response.setResponse(rs.getString("response"));
			response.setId(rs.getInt("keyword_id"));
			responses.add(response);
		}
		prepStatement.close();
		connector.closeConnection(connection);
		
		return responses;
	}
	
	public void delete(int keywordId) throws SQLException {
		Connection connection = connector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM " 
				+ System.getenv("DB_NAME") + ".responses WHERE keyword_id = ?");
		prepStatement.setInt(1, keywordId);
		prepStatement.execute();
		prepStatement.close();
		connector.closeConnection(connection);
	}

}
