package exceptions;

public class NonexistentServerRegisterException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonexistentServerRegisterException() {
		super("The given server id does not exist.");
	}

}
