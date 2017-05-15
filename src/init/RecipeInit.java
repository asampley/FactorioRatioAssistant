package init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import recipe.Ingredient;
import recipe.Item;
import recipe.ItemNotRegisteredException;
import recipe.Recipe;

public class RecipeInit {
	public static Map<Item, Recipe> readFromDirectory(File directory) throws FileNotFoundException, IOException, ItemNotRegisteredException {
		Map<Item, Recipe> recipes = new HashMap<>();
		if (!directory.isDirectory()) {
			throw new IOException(directory.getPath() + " is not a directory");
		}
		
		for (File file : directory.listFiles()) {
			try {
				Recipe recipe = readFromFile(file);
				
				if (recipe == null) {
					System.err.println("Warning: Recipe from file " + file + " was not read");
				} else {
					recipes.put(recipe.output(), recipe);
				}
			} catch (NoSuchElementException e) {
				System.out.println("Unable to read from " + file);
			}
		}
		
		return recipes;
	}
	
	public static Recipe readFromFile(File file) throws FileNotFoundException, IOException, ItemNotRegisteredException {
		return readFromStream(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
	}
	
	public static Recipe readFromStream(BufferedReader reader) throws IOException, ItemNotRegisteredException {
		List<Ingredient> ingredients = new ArrayList<>();
		
		Iterator<String> lines = reader.lines().iterator();

		if (!lines.hasNext()) return null;
		
		String line = lines.next();
		float time = Float.parseFloat(line);

		if (!lines.hasNext()) return null;
		
		line = lines.next();
		String[] lineParsed = line.split(":");

		String iName = lineParsed[0].trim();
		String iCount = lineParsed[1].trim();
		
		Item output = Item.fromName(iName);
		int outputCount = Integer.parseInt(iCount);
		
		while (lines.hasNext()) {
			line = lines.next();
			lineParsed = line.split(":");
			
			iName = lineParsed[0].trim();
			iCount = lineParsed[1].trim();
			
			Item input = Item.fromName(iName);
			int inputCount = Integer.parseInt(iCount);
			
			ingredients.add(new Ingredient(input, inputCount));
		}
		
		return new Recipe(output, outputCount, time, ingredients);
	}
}
