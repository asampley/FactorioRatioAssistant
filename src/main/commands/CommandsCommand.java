package main.commands;

import main.Command;
import main.Main;

public class CommandsCommand extends Command {

	public CommandsCommand() {
		super("commands", (s) -> commands(),
				"/commands: List the commands available.");
	}
	
	private static void commands() {
		for (String command : Main.commandMap.keySet()) {
			System.out.println("/" + command);
		}
	}
}
