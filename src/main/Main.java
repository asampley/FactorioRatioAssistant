package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import init.ItemInit;
import init.MachineInit;
import init.RecipeInit;
import main.commands.CommandsCommand;
import main.commands.Craft;
import main.commands.HelpCommand;
import main.commands.Level;
import main.commands.MachineCommand;
import main.commands.Raw;
import main.commands.RecipeCommand;
import ratio.RatioSolver;
import recipe.Item;
import recipe.MachineClass;
import recipe.Recipe;

public class Main {
	public static Map<String, Command> commandMap = new HashMap<>();
	public static final String defaultCommand = "craft";
	
	public static RatioSolver ratioSolver;
	public static Map<String, MachineClass> machineClasses;
	public static Map<Item, Recipe> recipes;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <config directory>");
			System.out.println("Usage: <config directory> <init file>");
			System.exit(1);
		}
		
		initCommands();
		
		System.out.println("Welcome to the Factorio Ratio Assistant");
		System.out.println("To find a ratio, type the name of the item (or /craft <item>)");
		System.out.println("Type /level <machine type> to change the level of a machine used for calculations");
		System.out.println("All recipes and machine definitions can be altered by changing the files in the recipe directory");
		System.out.println("----------------");
		
		String recipeDirectory = args[0];
		
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
		
		Scanner in = null;
		boolean onInitFile = false;
		if (args.length > 1) {
			String initFile = args[1];
			
			try {
				in = new Scanner(new FileInputStream(new File(initFile)));
				onInitFile = true;
				System.out.println("Loaded init file \"" + initFile + "\"");
			} catch (FileNotFoundException e) {
				System.err.println("Warning: Init file \"" + initFile + "\" not found");
				in = new Scanner(System.in);
			}
		} else {
			in = new Scanner(System.in);
		}

		//System.out.println(recipes);
		
		ratioSolver = new RatioSolver(recipes, machineSpeeds);
		
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
			
			// check if we are done init file to switch to input
			if (onInitFile && !in.hasNextLine()) {
				in.close();
				in = new Scanner(System.in);
			}
		}
		in.close();
	}
	
	private static void initCommands() {
		Set<Command> commands = new HashSet<Command>();
		
		commands.add(new Craft());
		commands.add(new Level());
		commands.add(new Raw(true));
		commands.add(new Raw(false));
		commands.add(new MachineCommand(true));
		commands.add(new MachineCommand(false));
		commands.add(new CommandsCommand());
		commands.add(new HelpCommand());
		commands.add(new RecipeCommand());
		
		for (Command command : commands) {
			commandMap.put(command.NAME, command);
		}
	}
}
