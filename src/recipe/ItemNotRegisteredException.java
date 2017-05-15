package recipe;

public class ItemNotRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;

	public ItemNotRegisteredException(String name) {
		super("Item with name \"" + name + "\" not registered.");
	}

}
