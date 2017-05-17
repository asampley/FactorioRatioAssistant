package main.commands;

import main.Command;
import main.Main;
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
		}
		
		MachineClass mc = Main.machineClasses.get(machineClassName);
		
		if (mc == null) {
			System.err.println("No machine class named \"" + machineClassName + "\" found");
			return;
		}
		
		Main.ratioSolver.useMachineClass(mc, on);
		System.out.println("Turned machine class \"" + machineClassName + "\" " + (on ? "on" : "off"));
	}
}
