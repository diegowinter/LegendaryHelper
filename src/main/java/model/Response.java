package model;

public class Response {
	
	private String response;
	private int keyword_id;
	
	public Response(String response, int  keyword_id) {
		this.response = response;
		this.keyword_id =  keyword_id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public int getId() {
		return  keyword_id;
	}

	public void setId(int  keyword_id) {
		this. keyword_id =  keyword_id;
	}

}
