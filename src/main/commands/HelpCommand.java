package main.commands;

import main.Command;
import main.Main;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", (s) -> help(s), 
				"/help <command name>: Display help info for the command.\n"
				+ "e.g. /help craft");
	}

	private static void help(String args) {
		String commandName = args.trim();
		
		if (commandName.length() == 0) {
			commandName = "help";
		}
		
		Command command = Main.commandMap.get(commandName);
		
		if (command == null) {
			System.err.println("No command named \"" + commandName +"\"");
		}
		
		System.out.println(command.help());
	}
}
