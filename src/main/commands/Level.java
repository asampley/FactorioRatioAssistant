package main.commands;

import main.Command;
import main.Main;
import recipe.MachineClass;
import recipe.MachineLevelOutOfBoundsException;

public class Level extends Command {

	public Level() {
		super("level", (s) -> level(s), 
				"/level <machine class> <level>: Sets the level of a class of machines.\n"
				+ "e.g. /level furnace 3");
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
		
		MachineClass mc = Main.machineClasses.get(machineName);
		
		if (mc == null) {
			System.err.println("No machine with name \"" + machineName + "\" found");
			return;
		}
		
		try {
			Main.ratioSolver.setMachineLevel(mc, level);
		} catch (MachineLevelOutOfBoundsException e) {
			System.err.println("\"" + mc + "\" has no level " + level);
			return;
		}
		System.out.println("Set " + mc + " to " + mc.name(level));
	}
	
	private static void printLevelUsage() {
		System.out.println("Usage: /level <machine type> <level>");
	}
}
