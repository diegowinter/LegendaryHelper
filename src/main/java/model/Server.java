package model;

public class Server {
	
	private String serverId;
	private boolean enableKeywordResponses;
	private boolean enableMinigames;
	
	public Server(String serverId, boolean enableKeywordResponses) {
		this.serverId = serverId;
		this.enableKeywordResponses = enableKeywordResponses;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public boolean isEnableKeywordResponses() {
		return enableKeywordResponses;
	}

	public void setEnableKeywordResponses(boolean enableKeywordResponses) {
		this.enableKeywordResponses = enableKeywordResponses;
	}

	public boolean isEnableMinigames() {
		return enableMinigames;
	}

	public void setEnableMinigames(boolean enableMinigames) {
		this.enableMinigames = enableMinigames;
	}

}
