package recipe;

public class ItemAlreadyRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;

	public ItemAlreadyRegisteredException(String name) {
		super("Item with name \"" + name + "\" already registered.");
	}

}
