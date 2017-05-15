package recipe;

public class Ingredient {
	protected Item item;
	protected int count;
	
	public Ingredient(Item item, int count) {
		this.item = item;
		this.count = count;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Ingredient && ((Ingredient)o).item.equals(this.item) && ((Ingredient)o).count == count;
	}
	
	@Override
	public int hashCode() {
		return item.hashCode();
	}
	
	@Override
	public String toString() {
		return item.toString() + ":" + count;
	}
	
	public Item item() {
		return item;
	}
	
	public int count() {
		return count;
	}
}
