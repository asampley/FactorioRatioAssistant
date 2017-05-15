package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import init.ItemInit;
import init.RecipeInit;
import ratio.RatioSolver;
import ratio.RecipeCount;
import recipe.Ingredient;
import recipe.Item;
import recipe.ItemIsCountableException;
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
			ItemInit.registerFromFile(new File(recipeDirectory, "items"), true);
			ItemInit.registerFromFile(new File(recipeDirectory, "fluids"), false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			recipes = RecipeInit.readFromDirectory(new File(recipeDirectory, "recipes"));
		} catch (IOException e) {
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
				ratioSolver.solve(item);
				Tree<RecipeCount> tree = ratioSolver.solution();
				
				if (tree == null) {
					System.err.println("No recipe found for " + item);
					continue;
				}
				
				System.out.println("--Tree--");
				System.out.println(tree);
				
				System.out.println("--Cumulative Machines--");
				Map<Recipe, Fraction> totals = ratioSolver.machines();
				Fraction everythingTotal = new Fraction(0);
				for (Recipe recipe : totals.keySet()) {
					System.out.println(new RecipeCount(recipe, totals.get(recipe)));
					everythingTotal = everythingTotal.add(totals.get(recipe));
				}
				System.out.println(everythingTotal + " Total Machines");
				
				System.out.println("--Raw Consumption Per Second--");
				Map<Item, Fraction> production = ratioSolver.raw();
				for (Item itemi : production.keySet()) {
					System.out.println(production.get(itemi) + " x " + itemi);
				}
			} catch (ItemNotRegisteredException e) {
				System.err.println(e.getMessage());
			}
		}
		in.close();
	}
}
