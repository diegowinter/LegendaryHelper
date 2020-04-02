package exception;

public class DuplicatedKeywordException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public DuplicatedKeywordException() {
		super("The given keyword already exists.");
	}
	
}
