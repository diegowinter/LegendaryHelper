package message_listeners.minigames.tictactoe.model;

import message_listeners.minigames.tictactoe.VisualBoard;

public class Session {
	
	private String channelId;
	private String playerXId;
	private String playerOId;
	private String playerXName;
	private String playerOName;
	private Board board;
	private boolean isXTurn; // if false, it's O turn.
	private VisualBoard visualBoard;
	
	public Session(String channelId, String playerXId, String playerOId, String playerXName, String playerOName) {
		this.channelId = channelId;
		this.playerXId = playerXId;
		this.playerOId = playerOId;
		this.playerXName = playerXName;
		this.playerOName = playerOName;
		
		isXTurn = true;
		visualBoard = new VisualBoard(playerXName, playerOName);
		board = new Board();
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getPlayerXId() {
		return playerXId;
	}

	public void setPlayerXId(String playerXId) {
		this.playerXId = playerXId;
	}

	public String getPlayerOId() {
		return playerOId;
	}

	public void setPlayerOId(String playerOId) {
		this.playerOId = playerOId;
	}

	public String getPlayerXName() {
		return playerXName;
	}

	public void setPlayerXName(String playerXName) {
		this.playerXName = playerXName;
	}

	public String getPlayerOName() {
		return playerOName;
	}

	public void setPlayerOName(String playerOName) {
		this.playerOName = playerOName;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public boolean isXTurn() {
		return isXTurn;
	}

	public void setXTurn(boolean isXTurn) {
		this.isXTurn = isXTurn;
	}

	public VisualBoard getVisualBoard() {
		return visualBoard;
	}

	public void setVisualBoard(VisualBoard visualBoard) {
		this.visualBoard = visualBoard;
	}

}
