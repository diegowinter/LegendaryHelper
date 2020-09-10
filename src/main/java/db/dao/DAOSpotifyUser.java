package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.Connector;
import model.SpotifyUser;

public class DAOSpotifyUser {
	
	private Connector connector = new Connector();
	
	public void updateToken(String userId, String token, String refreshToken) throws SQLException {
		Connection connection = connector.getConnection();
		
		PreparedStatement prepStatement = connection.prepareStatement("UPDATE users SET spotify_token = ?, spotify_refresh_token = ? WHERE id = " + userId +";");
		prepStatement.setString(1, token);
		prepStatement.setString(2, refreshToken);
		prepStatement.execute();
		prepStatement.close();
		
		connector.closeConnection(connection);
	}
	
	public SpotifyUser search(String userId) throws SQLException {
		Connection connection = connector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM " 
				+ System.getenv("DB_NAME") + ".users WHERE id = ?");
		prepStatement.setString(1, userId);
		
		SpotifyUser spotifyAccount = new SpotifyUser(userId, null, null, null);
		
		ResultSet rs = prepStatement.executeQuery();
		if(rs.next()) {
			spotifyAccount.setCode(rs.getString("spotify_code"));
			spotifyAccount.setToken(rs.getString("spotify_token"));
			spotifyAccount.setRefreshToken(rs.getString("spotify_refresh_token"));
			
			prepStatement.close();
			connector.closeConnection(connection);
			return spotifyAccount;
		} else {
			prepStatement.close();
			connector.closeConnection(connection);
			return null;
		}
	}
	
	public void delete(String userId) throws SQLException {
		Connection connection = connector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM " + System.getenv("DB_NAME") + ".users WHERE id = ?");
		prepStatement.setString(1, userId);
		
		prepStatement.execute();
		prepStatement.close();
		
		connector.closeConnection(connection);
	}

}
