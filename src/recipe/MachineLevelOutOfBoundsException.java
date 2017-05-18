package recipe;

public class MachineLevelOutOfBoundsException extends Exception {
	private static final long serialVersionUID = 1L;

	public MachineLevelOutOfBoundsException(MachineClass mc, int level) {
		super("Machine level " + level + " out of bounds for " + mc);
	}
}
