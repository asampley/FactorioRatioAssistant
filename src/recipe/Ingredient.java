package recipe;

import org.apache.commons.math3.fraction.Fraction;

public class Ingredient {
	protected Item item;
	protected Fraction count;
	
	public Ingredient(Item item, Fraction count) throws ItemIsCountableException {
		this.item = item;
		
		if (item.countable && count.getDenominator() != 1) {
			throw new ItemIsCountableException(item);
		}
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
	
	public Fraction count() {
		return count;
	}
}
