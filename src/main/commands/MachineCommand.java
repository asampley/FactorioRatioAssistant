package main.commands;

import java.util.Collection;

import main.Command;
import main.Main;
import recipe.Item;
import recipe.MachineClass;

public class MachineCommand extends Command {

	
	public MachineCommand(boolean on) {
		super(on ? "machineon" : "machineoff", (s) -> machine(s, on), 
				on ?
						"/machineon <machine class>: Set the machine class to be used in recipe expansion." :
						"/machineoff <machine class>: Set the machine class to not be used in recipe expansion.");
	}

	private static void machine(String args, boolean on) {
		String machineClassName = args.trim();
		
		if (machineClassName.length() == 0) {
			System.err.println("Usage: /machine" + (on ? "on" : "off") + " <machine class>");
			return;
		}
		
		MachineClass mc = Main.machineClasses.get(machineClassName);
		
		if (mc == null) {
			System.err.println("No machine class named \"" + machineClassName + "\" found");
			return;
		}
		
		Collection<Item> itemsSet = Main.ratioSolver.setRaw((recipe) -> recipe.machineClass().equals(mc), !on);
		for (Item item : itemsSet) {
			System.out.println("Set " + item + " to " + (on ? "unraw" : "raw"));
		}
	}
}
