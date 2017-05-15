package recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Recipe {
	Set<Ingredient> ingredients;
	Item output;
	int outputCount;
	float time;
	
	public Recipe(Item output, int outputCount, float time, Ingredient... ingredients) {
		this(output, outputCount, time, Arrays.asList(ingredients));
	}

	public Recipe(Item output, int outputCount, float time, Collection<Ingredient> ingredients) {
		this.output = output;
		this.outputCount = outputCount;
		this.ingredients = new HashSet<>();
		this.time = time;
		this.ingredients.addAll(ingredients);
	}
	
	@Override
	public String toString() {
		return "[" + time + "s] (" + output + ":" + outputCount + ") <- " + ingredients;
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

	public float time() {
		return time;
	}
}
