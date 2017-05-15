package ratio;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import recipe.Recipe;

public class RecipeCount extends Pair<Recipe, Fraction> {
	public RecipeCount(Pair<? extends Recipe, ? extends Fraction> entry) {
		super(entry);
	}
	
	public RecipeCount(Recipe recipe, Fraction fraction) {
		super(recipe, fraction);
	}
	
	public Recipe getRecipe() {
		return this.getFirst();
	}
	
	public Fraction getCount() {
		return this.getSecond();
	}
	
	@Override
	public String toString() {
		return this.getCount() + " x " + this.getRecipe();
	}
}
