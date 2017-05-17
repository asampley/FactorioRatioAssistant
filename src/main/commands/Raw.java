package main.commands;

import main.Command;
import main.Main;
import recipe.Item;
import recipe.ItemNotRegisteredException;

public class Raw extends Command {

	public Raw(boolean setRaw) {
		super(setRaw ? "raw" : "unraw", (s) -> raw(s, setRaw),
				setRaw ? 
						"/raw <item>: Set the item to not be expanded by a recipe\n"
						+ "e.g. /raw electronic circuit" :
						"/unraw <item>: Set the item to be expanded by a recipe\n"
						+ "e.g. /unraw electronic circuit");
	}
	
	private static void raw(String args, boolean setRaw) {
		String itemName = args.trim();
		
		try {
			Item item = Item.fromName(itemName);
			
			Main.ratioSolver.setRaw(item, setRaw);
			System.out.println("Set " + item + " to " + (setRaw ? "raw" : "unraw"));
		} catch (ItemNotRegisteredException e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}
