package main;

import java.util.function.Consumer;

public class Command {
	public final String NAME;
	private Consumer<String> function;
	private String help;
	
	public Command(String name, Consumer<String> function, String help) {
		this.NAME = name;
		this.function = function;
		this.help = help;
	}
	
	public String help() {
		return help;
	}
	
	public void accept(String args) {
		function.accept(args);
	}
}
