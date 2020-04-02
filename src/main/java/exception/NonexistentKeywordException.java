package exception;

public class NonexistentKeywordException extends Exception{

	private static final long serialVersionUID = 1L;

	public NonexistentKeywordException() {
		super("The given keyword does not exist.");
	}
	
}
