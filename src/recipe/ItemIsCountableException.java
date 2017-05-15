package recipe;

public class ItemIsCountableException extends Exception {
	private static final long serialVersionUID = 1L;

	public ItemIsCountableException(Item item) {
		super("Item with name \"" + item.NAME + "\" must be represented by an integer.");
	}

}
