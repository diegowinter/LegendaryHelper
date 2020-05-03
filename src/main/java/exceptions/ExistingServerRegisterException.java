package exceptions;

public class ExistingServerRegisterException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExistingServerRegisterException() {
		super("This server is already registered on database.");
	}
	
}
