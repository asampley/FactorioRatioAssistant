package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import init.ItemInit;
import init.RecipeInit;
import ratio.RatioSolver;
import ratio.RecipeCount;
import recipe.Ingredient;
import recipe.Item;
import recipe.ItemNotRegisteredException;
import recipe.Recipe;
import tree.Tree;

public class Main {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <recipe directory>");
		}
		
		String recipeDirectory = args[0];
		
		Map<Item, Recipe> recipes = null;
		
		try {
			ItemInit.registerFromFile(new File(recipeDirectory, "Items.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			recipes = RecipeInit.readFromDirectory(new File(recipeDirectory, "recipes"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (ItemNotRegisteredException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(recipes);
		
		RatioSolver ratioSolver = new RatioSolver(recipes);
		
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			
			try {
				Item item = Item.fromName(line.trim());
//				Recipe recipe = recipes.get(item);
				Tree<RecipeCount> tree = ratioSolver.solveInteger(item);
				System.out.println(tree);
//				printRecursive(recipes, 0, item, new Fraction(multipleToWhole(recipes, item) / recipe.time()));
			} catch (ItemNotRegisteredException e) {
				System.err.println(e.getMessage());
			}
		}
		in.close();
	}
	
//	public static int multipleToWhole(Map<Item, Recipe> recipes, Item item) {
//		int multiple = 1;
//		ArrayDeque<Recipe> recipesToGo = new ArrayDeque<>();
//		ArrayDeque<Fraction> recipesPerSec = new ArrayDeque<>();
//		
//		Recipe recipe = recipes.get(item);
//		Fraction recipePerSec = new Fraction(1 / recipe.time());
//		
//		if (recipe == null) return multiple;
//		
//		recipesToGo.addLast(recipe);
//		recipesPerSec.addLast(recipePerSec);
//		
//		while (!recipesToGo.isEmpty()) {
//			recipe = recipesToGo.pop();
//			recipePerSec = recipesPerSec.pop();
//			
//			Fraction machines = recipePerSec.multiply(new Fraction(multiple * recipe.time() / recipe.outputCount()));
//			
//			//System.out.println(machines + " x " + recipe);
//			
//			multiple *= machines.getDenominator();
//			//System.out.println(multiple);
//			
//			for (Ingredient ingredient : recipe.ingredients()) {
//				Recipe nextRecipe = recipes.get(ingredient.item());
//				
//				if (nextRecipe == null) continue;
//				
//				recipesToGo.add(nextRecipe);
//				recipesPerSec.add(recipePerSec.multiply(ingredient.count()));
//			}
//		}
//		
//		return multiple;
//	}
	
//	public static void printRecursive(Map<Item, Recipe> recipes, int initDepth, Item item, Fraction nPerSec) {
//		Recipe recipe = recipes.get(item);
//		
//		if (recipe == null) return;
//		
//		for (int i = 0; i < initDepth; ++i) {
//			System.out.print("\t");
//		}
//		
//		System.out.println(nPerSec.multiply(new Fraction(recipe.time() / recipe.outputCount())) + " x " + recipe);
//		
//		for (Ingredient ingredient : recipe.ingredients()) {
//			printRecursive(recipes, initDepth + 1, ingredient.item(), nPerSec.multiply(ingredient.count()));
//		}
//	}
}
