package recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.fraction.Fraction;

public class Recipe {
	MachineClass machineClass;
	Set<Ingredient> ingredients;
	Item output;
	int outputCount;
	Fraction time;
	
	public Recipe(MachineClass machine, Item output, int outputCount, Fraction time, Ingredient... ingredients) {
		this(machine, output, outputCount, time, Arrays.asList(ingredients));
	}

	public Recipe(MachineClass machine, Item output, int outputCount, Fraction time, Collection<Ingredient> ingredients) {
		this.machineClass = machine;
		this.output = output;
		this.outputCount = outputCount;
		this.ingredients = new HashSet<>();
		this.time = time;
		this.ingredients.addAll(ingredients);
	}
	
	@Override
	public String toString() {
		return "(" + output + ":" + outputCount + ") <- " + ingredients;
	}

	public Item output() {
		return output;
	}
	
	public Set<Ingredient> ingredients() {
		return ingredients;
	}

	public int outputCount() {
		return outputCount;
	}

	public Fraction time() {
		return time;
	}
	
	public MachineClass machineClass() {
		return machineClass;
	}
	
	@Override
	public int hashCode() {
		return output.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Recipe) {
			Recipe r = (Recipe)o;
			
			return r.ingredients.equals(this.ingredients)
					&& r.output.equals(this.output)
					&& r.outputCount == this.outputCount
					&& r.time.equals(this.time)
					&& r.machineClass.equals(this.machineClass);
		}
		
		return false;
	}
}
