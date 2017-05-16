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

import org.apache.commons.math3.fraction.Fraction;

import recipe.Ingredient;
import recipe.Item;
import recipe.ItemIsCountableException;
import recipe.ItemNotRegisteredException;
import recipe.MachineClass;
import recipe.Recipe;

public class RecipeInit {
	public static Map<Item, Recipe> readFromDirectory(File directory, MachineClass machine) throws FileNotFoundException, IOException {
		Map<Item, Recipe> recipes = new HashMap<>();
		if (!directory.isDirectory()) {
			throw new IOException(directory.getPath() + " is not a directory");
		}
		
		for (File file : directory.listFiles()) {
			try {
				Recipe recipe = readFromFile(file, machine);
				
				if (recipe == null) {
					System.err.println("Warning: Recipe from file " + file + " was not read");
				} else {
					recipes.put(recipe.output(), recipe);
				}
			} catch (NoSuchElementException e) {
				System.err.println("Unable to read from " + file);
			} catch (ItemNotRegisteredException | ItemIsCountableException e) {
				System.err.println("Error in file " + file + ": " + e.getMessage());
			}
		}
		
		return recipes;
	}
	
	public static Recipe readFromFile(File file, MachineClass machine) throws FileNotFoundException, IOException, ItemNotRegisteredException, ItemIsCountableException {
		return readFromStream(new BufferedReader(new InputStreamReader(new FileInputStream(file))), machine);
	}
	
	public static Recipe readFromStream(BufferedReader reader, MachineClass machine) throws IOException, ItemNotRegisteredException, ItemIsCountableException {
		List<Ingredient> ingredients = new ArrayList<>();
		
		Iterator<String> lines = reader.lines().iterator();

		if (!lines.hasNext()) return null;
		
		String line = lines.next();
		Fraction time = new Fraction(Float.parseFloat(line));

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
			Fraction inputCount = new Fraction(Float.parseFloat(iCount));
			
			ingredients.add(new Ingredient(input, inputCount));
		}
		
		return new Recipe(machine, output, outputCount, time, ingredients);
	}
}
