package ratio;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import recipe.Ingredient;
import recipe.Item;
import recipe.Recipe;
import tree.Tree;

public class RatioSolver {
	private Map<Item, Recipe> recipes;
	
	public RatioSolver(Map<Item, Recipe> recipes) {
		this.recipes = new HashMap<Item, Recipe>();
		this.recipes.putAll(recipes);
	}
	
	public Tree<RecipeCount> solveInteger(Item item) {
		int multiple = multipleToWhole(item);
		Recipe recipe = recipes.get(item);
		
		//System.out.println(recipes);
		if (recipe == null) return null;
		
		return solveFraction(item, new Fraction(multiple * recipe.outputCount() / recipe.time()));
		
	}
	
	public Tree<RecipeCount> solveFraction(Item item, Fraction nPerSec) {
		Recipe recipe = recipes.get(item);
		
		if (recipe == null) return null;
		
		Fraction recipeCount = nPerSec.multiply(new Fraction(recipe.time() / recipe.outputCount()));
		Tree<RecipeCount> tree = new Tree<>(new RecipeCount(recipe, recipeCount));
		
		for (Ingredient ingredient : recipe.ingredients()) {
			Tree<RecipeCount> child = solveFraction(ingredient.item(), nPerSec.multiply(ingredient.count()));
			if (child != null) tree.addChild(child);
		}
		
		return tree;
	}
	
	protected int multipleToWhole(Item item) {
		int multiple = 1;
		ArrayDeque<Pair<Recipe, Fraction>> recipesToGo = new ArrayDeque<>();
		
		Recipe recipe = recipes.get(item);
		if (recipe == null) return multiple;
		
		Fraction recipePerSec = new Fraction(1 / recipe.time());
		
		recipesToGo.addLast(new Pair<>(recipe, recipePerSec));
		
		while (!recipesToGo.isEmpty()) {
			Pair<Recipe, Fraction> next = recipesToGo.pop();
			recipe = next.getFirst();
			recipePerSec = next.getSecond();
			
			Fraction machines = recipePerSec.multiply(new Fraction(multiple * recipe.time() / recipe.outputCount()));
			
			//System.out.println(machines + " x " + recipe);
			
			multiple *= machines.getDenominator();
			//System.out.println(multiple);
			
			for (Ingredient ingredient : recipe.ingredients()) {
				Recipe nextRecipe = recipes.get(ingredient.item());
				
				if (nextRecipe == null) continue;
				
				recipesToGo.add(new Pair<>(nextRecipe, recipePerSec.multiply(ingredient.count())));
			}
		}
		
		return multiple;
	}
}
