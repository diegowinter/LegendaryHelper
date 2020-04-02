package model;

public class Keyword {
	
	private String keyword;
	private int id;
	
	public Keyword(String keyword, int id) {
		this.keyword = keyword;
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
