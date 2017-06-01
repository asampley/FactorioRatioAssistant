package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

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
		
		InputStream in = null;
		char inChar;
		StringBuffer inLine = new StringBuffer();
		boolean onInitFile = false;
		if (args.length > 1) {
			String initFile = args[1];
			
			try {
				in = new FileInputStream(new File(initFile));
				onInitFile = true;
				System.out.println("Loaded init file \"" + initFile + "\"");
			} catch (FileNotFoundException e) {
				System.err.println("Warning: Init file \"" + initFile + "\" not found");
				in = System.in;
			}
		} else {
			in = System.in;
		}
		
		//System.out.println(recipes);
		
		ratioSolver = new RatioSolver(recipes, machineSpeeds);
		
		try {
			while (true) {
				inChar = (char)in.read();
				
				Pair<Command, String> commandAndArgs;
				Command command;
				String commandArgs;
				
				switch (inChar) {
//				case '\b':
//				case (char)127: // delete character
//					inLine.deleteCharAt(inLine.length() - 1);
//					break;
				case '\n':
				case (char)-1:
					String line = inLine.toString();
					inLine = new StringBuffer();
					if (line.length() == 0) break;
					
					try {
						commandAndArgs = parseCommand(line.toString());
					} catch (CommandNotFoundException e1) {
						System.err.println(e1.getMessage());
						break;
					}
					
					command = commandAndArgs.getFirst();
					commandArgs = commandAndArgs.getSecond();
					
					command.accept(commandArgs);
					break;
				case '\t':
//					System.out.print('\b');
					
					if (inLine.length() == 0) break;
					
					try {
						commandAndArgs = parseCommand(inLine.toString());
					} catch (CommandNotFoundException e1) {
						e1.printStackTrace();
						break;
					}
					
					command = commandAndArgs.getFirst();
					commandArgs = commandAndArgs.getSecond();
					
					Collection<Pair<Integer, String>> completions = command.tabComplete(commandArgs);
					
					if (completions == null) break;
					else if (completions.isEmpty()) break;
					else if (completions.size() == 1) {
						Pair<Integer, String> backAndReplacement = completions.iterator().next();
						inLine.delete(inLine.length() - backAndReplacement.getFirst(), inLine.length());
						inLine.append(backAndReplacement.getSecond());
					} else {
						for (Pair<Integer, String> backAndReplacement : completions) {
							System.out.println(backAndReplacement.getSecond());
						}
					}
					break;
				default:
					inLine.append(inChar);
					break;
				}
				
				// check if we are done init file to switch to input
				if (inChar == (char)-1) {
					if (onInitFile) {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						in = System.in;
					} else {
						// end while loop
						break;
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Pair<Command, String> parseCommand(String inLine) throws CommandNotFoundException {
		Command command = null;
		String args = "";
		
		if (inLine.length() == 0) return new Pair<>(command, args);
		
		// check for command
		if (inLine.charAt(0) == '/') {
			int split = inLine.indexOf(" ");
			
			String commandString;
			if (split == -1) {
				commandString = inLine.substring(1, inLine.length());
				args = "";
			} else {
				commandString = inLine.substring(1, split);
				args = inLine.substring(split + 1, inLine.length());
			}
			
			if (commandMap.containsKey(commandString)) {
				command = commandMap.get(commandString);
			} else {
				throw new CommandNotFoundException(commandString);
			}
		} else { // default command
			command = commandMap.get(defaultCommand);
			args = inLine.toString();
		}
		
		return new Pair<>(command, args);
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
