package main.commands;

import java.util.function.Consumer;

import main.Command;
import main.Main;
import recipe.Item;
import recipe.ItemNotRegisteredException;
import recipe.Recipe;

public class RecipeCommand extends Command {

	public RecipeCommand() {
		super("recipe", (s) -> recipe(s), 
				"/recipe <item>: Prints the recipe for the item\n"
				+ "e.g. /recipe stone furnace");
		// TODO Auto-generated constructor stub
	}

	private static void recipe(String args) {
		String itemName = args.trim();
		
		if (itemName.length() == 0) {
			System.err.println("Usage: /recipe <item>");
			return;
		}
		
		Recipe recipe;
		try {
			recipe = Main.ratioSolver.getRecipeAlways(Item.fromName(itemName));
		} catch (ItemNotRegisteredException e) {
			System.err.println(e.getMessage());
			return;
		}
		
		if (recipe == null) {
			System.err.println("No recipe for \"" + itemName + "\" found");
			return;
		}
		
		System.out.println(recipe);
	}
}
