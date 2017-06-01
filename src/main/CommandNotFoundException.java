package main;

public class CommandNotFoundException extends Exception {
	public CommandNotFoundException(String name) {
		super("No command \"" + name + "\" found.");
	}
}
