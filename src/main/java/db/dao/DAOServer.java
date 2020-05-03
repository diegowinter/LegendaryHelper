package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.Connector;
import exceptions.ExistingServerRegisterException;
import exceptions.NonexistentServerRegisterException;
import model.Server;

public class DAOServer {
	
	private Connector connector = new Connector();
	
	public void add(String serverId) throws SQLException, ExistingServerRegisterException {
		try {
			search(serverId);
			System.out.println("Já tem");
			throw new ExistingServerRegisterException();
		} catch (NonexistentServerRegisterException e) {
			Connection connection = connector.getConnection();
			PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO servers(id, enable_kwd_resp, enable_minigames)"
					+ "values (?, ?, ?)");
			prepStatement.setString(1, serverId);
			prepStatement.setBoolean(2, true);
			prepStatement.setBoolean(3, true);
			prepStatement.execute();
			prepStatement.close();
			connector.closeConnection(connection);
		}
	}
	
	public Server search(String serverId) throws SQLException, NonexistentServerRegisterException {
		Connection connection = connector.getConnection();
		Server server = new Server(null, false);
		
		PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM "
				+ System.getenv("DB_NAME") + ".servers WHERE id = ?");
		prepStatement.setString(1, serverId);
		
		ResultSet rs = prepStatement.executeQuery();
		if(rs.next()) {
			server.setServerId(rs.getString("id"));
			server.setEnableKeywordResponses(rs.getBoolean("enable_kwd_resp"));
			server.setEnableMinigames(rs.getBoolean("enable_minigames"));
		} else {
			prepStatement.close();
			connector.closeConnection(connection);
			throw new NonexistentServerRegisterException();
		}
		
		prepStatement.close();
		connector.closeConnection(connection);
		
		return server;
	}
	
	public void delete(String serverId) throws SQLException {
		Connection connection = connector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM "
				+ System.getenv("DB_NAME") + ".servers WHERE id = ?");
		prepStatement.setString(1, serverId);
		prepStatement.execute();
		prepStatement.close();
		connector.closeConnection(connection);
	}
	
	public void update(String serverId, String prop, boolean enable) throws SQLException {
		Connection connection = connector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement("UPDATE servers SET " 
				+ prop + " = ? WHERE id = " + serverId +";");	
		if(enable) {
			prepStatement.setBoolean(1, true);
		} else {
			prepStatement.setBoolean(1, false);
		}
		prepStatement.execute();
		prepStatement.close();
		connector.closeConnection(connection);
	}
	
}
