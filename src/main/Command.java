package main;

import java.util.Collection;
import java.util.function.Consumer;

import org.apache.commons.math3.util.Pair;

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

	public Collection<Pair<Integer, String>> tabComplete(String commandArgs) {
		return null;
	}
}
