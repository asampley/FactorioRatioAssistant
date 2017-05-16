package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import init.ItemInit;
import init.MachineInit;
import init.RecipeInit;
import ratio.RatioSolver;
import ratio.MachineCount;
import recipe.Item;
import recipe.ItemNotRegisteredException;
import recipe.Machine;
import recipe.MachineClass;
import recipe.MachineLevelOutOfBoundsException;
import recipe.Recipe;
import tree.Tree;

public class Main {
	private static Map<String, Consumer<String>> commandMap = new HashMap<>();
	public static final String defaultCommand = "craft";
	
	private static RatioSolver ratioSolver;
	private static Map<String, MachineClass> machineClasses;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <config directory>");
			System.exit(1);
		}
		
		System.out.println("Welcome to the Factorio Ratio Assistant");
		System.out.println("To find a ratio, type the name of the item (or /craft <item>)");
		System.out.println("Type /level <machine type> to change the level of a machine used for calculations");
		System.out.println("All recipes and machine definitions can be altered by changing the files in the recipe directory");
		System.out.println("----------------");
		
		commandMap.put("craft", (s) -> craft(s));
		commandMap.put("level", (s) -> level(s));
		commandMap.put("raw", (s) -> raw(s, true));
		commandMap.put("unraw", (s) -> raw(s, false));
		
		String recipeDirectory = args[0];
		
		Map<Item, Recipe> recipes = null;
		Map<MachineClass, Integer> machineSpeeds = new HashMap<>();
		
		// register all items and fluids
		try {
			ItemInit.registerFromFile(new File(recipeDirectory, "items"), true);
			ItemInit.registerFromFile(new File(recipeDirectory, "fluids"), false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// register all machines
		try {
			machineClasses = MachineInit.readFromDirectory(new File(recipeDirectory, "machines"));
			for (MachineClass machineClass : machineClasses.values()) {
				machineSpeeds.put(machineClass, 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// read all recipes
		try {
			recipes = new HashMap<>();
			for (MachineClass machine : machineSpeeds.keySet()) {
				recipes.putAll(RecipeInit.readFromDirectory(new File(recipeDirectory, "recipes/" + machine.NAME), machine));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//System.out.println(recipes);
		
		ratioSolver = new RatioSolver(recipes, machineSpeeds);
		
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			
			if (line.length() == 0) continue;
			
			// check for command
			if (line.charAt(0) == '/') {
				int split = line.indexOf(' ');
				
				String command;
				String commandArgs;
				if (split == -1) {
					command = line.substring(1, line.length());
					commandArgs = "";
				} else {
					command = line.substring(1, split);
					commandArgs = line.substring(split + 1, line.length());
				}
				
				if (commandMap.containsKey(command)) {
					commandMap.get(command).accept(commandArgs);
				} else {
					System.err.println("Unrecognized command : " + command);
				}
			} else { // default command
				commandMap.get(defaultCommand).accept(line);
			}
			
			
		}
		in.close();
	}
	
	private static void raw(String args, boolean setRaw) {
		String itemName = args.trim();
		
		try {
			Item item = Item.fromName(itemName);
			
			ratioSolver.setRaw(item, setRaw);
			System.out.println("Set " + item + " to " + (setRaw ? "raw" : "unraw"));
		} catch (ItemNotRegisteredException e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	private static void level(String args) {
		String[] parsedArgs = args.split(" ");
		
		if (parsedArgs.length < 2) {
			printLevelUsage();
			return;
		}

		Integer level;
		try {
			level = Integer.parseInt(parsedArgs[parsedArgs.length - 1]);
		} catch (NumberFormatException e) {
			printLevelUsage();
			return;
		}
		
		String machineName = "";
		for (int i = 0; i < parsedArgs.length - 1; ++i) {
			machineName += parsedArgs[i] + " ";
		}
		machineName = machineName.trim();
		
		MachineClass mc = machineClasses.get(machineName);
		
		if (mc == null) {
			System.err.println("No machine with name \"" + machineName + "\" found");
			return;
		}
		
		try {
			ratioSolver.setMachineLevel(mc, level);
		} catch (MachineLevelOutOfBoundsException e) {
			System.err.println("\"" + mc + "\" has no level " + level);
			return;
		}
		System.out.println("Set " + mc + " to level " + level);
	}
	
	private static void printLevelUsage() {
		System.out.println("Usage: /level <machine type> <level>");
	}

	private static void craft(String args) {
		try {
			String itemName = args.trim();
			
			if (itemName.length() == 0) {
				System.err.println("Usage: /craft <item>");
				return;
			}
			
			Item item = Item.fromName(args.trim());
			ratioSolver.solve(item);
			Tree<MachineCount> tree = ratioSolver.solution();
			
			if (tree == null) {
				System.err.println("No recipe found for " + item);
				return;
			}
			
			System.out.println("--Tree--");
			System.out.print(tree.toString(1));
			System.out.println();
			
			System.out.println("--Cumulative Machines by Recipe--");
			Map<Machine, Fraction> totals = ratioSolver.machines();
			Map<Pair<MachineClass, Integer>, Fraction> everythingTotal = new HashMap<>();
			for (Machine machine : totals.keySet()) {
				System.out.println("\t" + new MachineCount(machine, totals.get(machine)));
				MachineClass mc = machine.machineClass();
				Pair<MachineClass, Integer> key = new Pair<>(mc, machine.level());
				if (everythingTotal.containsKey(key)) {
					everythingTotal.put(key, everythingTotal.get(key).add(totals.get(machine)));
				} else {
					everythingTotal.put(key, totals.get(machine));
				}
			}
			System.out.println();
			
			System.out.println("--Cumulative Machines--");
			for (Pair<MachineClass, Integer> mci : everythingTotal.keySet()) {
				System.out.println("\t" + everythingTotal.get(mci) + " x " + mci.getFirst() + " " + mci.getSecond());
			}
			System.out.println();
			
			System.out.println("--Raw Consumption Per Second--");
			Map<Item, Fraction> production = ratioSolver.raw();
			for (Item itemi : production.keySet()) {
				System.out.println("\t" + production.get(itemi) + " x " + itemi);
			}
			System.out.println();
			
			System.out.println("--Total Production Per Second--");
			Fraction prodPerSec = tree.getRootValue().getCount().divide(tree.getRootValue().getMachine().time());
			System.out.println("\t" + prodPerSec + " x " + item);
			System.out.println();
			
		} catch (ItemNotRegisteredException e) {
			System.err.println(e.getMessage());
		}
	}
}
