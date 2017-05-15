package recipe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Map<String, Item> registry = new HashMap<>();
	
	public final String NAME;
	public final boolean countable;
	
	protected Item(String name, boolean countable) {
		NAME = name.toLowerCase();
		this.countable = countable;
	}
	
	public static void register(String name, boolean countable) throws ItemAlreadyRegisteredException {
		name = name.toLowerCase();
		if (registry.containsKey(name)) {
			throw new ItemAlreadyRegisteredException(name);
		} else {
			registry.put(name, new Item(name, countable));
		}
	}
	
	public static Item fromName(String name) throws ItemNotRegisteredException {
		name = name.toLowerCase();
		if (registry.containsKey(name)) {
			return registry.get(name);
		} else {
			throw new ItemNotRegisteredException(name);
		}
	}
	
	@Override
	public int hashCode() {
		return NAME.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Item && ((Item)o).NAME == this.NAME;
	}
	
	@Override
	public String toString() {
		return NAME;
	}
}
