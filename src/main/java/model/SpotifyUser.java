package model;

public class SpotifyUser {
	
	private String useriD;
	private String code;
	private String token;
	private String refreshToken;
	
	public SpotifyUser(String userId, String code, String token, String refreshToken) {
		this.useriD = userId;
		this.code = code;
		this.token = token;
		this.refreshToken = refreshToken;
	}
	
	public String getUseriD() {
		return useriD;
	}
	
	public void setUseriD(String useriD) {
		this.useriD = useriD;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
