package recipe;

public class MachineLevelOutOfBoundsException extends Exception {
	private static final long serialVersionUID = 1L;

	public MachineLevelOutOfBoundsException() {
		super("Machine level out of bounds for the specified class");
	}
}
