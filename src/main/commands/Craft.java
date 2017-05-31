package main.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import main.Command;
import main.Main;
import ratio.MachineCount;
import recipe.Item;
import recipe.ItemNotRegisteredException;
import recipe.Machine;
import recipe.MachineClass;
import tree.Tree;

public class Craft extends Command {
	public Craft() {
		super("craft", (s) -> craft(s),
				"/craft <item>: Calculate the ratio for the specified item\n"
				+ "e.g. /craft science pack 1");
	}



	private static void craft(String args) {
		try {
			String itemName = args.trim();
			
			if (itemName.length() == 0) {
				System.err.println("Usage: /craft <item>");
				return;
			}
			
			Item item = Item.fromName(args.trim());
			Main.ratioSolver.solve(item);
			Tree<MachineCount> tree = Main.ratioSolver.solution();
			
			if (tree == null) {
				System.err.println("No recipe found for " + item);
				return;
			}
			
			System.out.println("--Tree--");
			System.out.print(tree.toString(1));
			System.out.println();
			
			System.out.println("--Cumulative Machines by Recipe--");
			Map<Machine, Fraction> totals = Main.ratioSolver.machines();
			Map<Pair<MachineClass, Integer>, Fraction> everythingTotal = new HashMap<>();
			for (Machine machine : totals.keySet()) {
				System.out.println("\t" + new MachineCount(machine, totals.get(machine)));
				MachineClass mc = machine.machineClass();
				Pair<MachineClass, Integer> key = new Pair<>(mc, machine.level());
				if (everythingTotal.containsKey(key)) {
					everythingTotal.put(key, everythingTotal.get(key).add(totals.get(machine)));
				} else {
					everythingTotal.put(key, totals.get(machine));
				}
			}
			System.out.println();
			
			System.out.println("--Cumulative Machines--");
			for (Pair<MachineClass, Integer> mci : everythingTotal.keySet()) {
				System.out.println("\t" + everythingTotal.get(mci) + " x " + mci.getFirst() + " " + mci.getSecond());
			}
			System.out.println();
			
			System.out.println("--Raw Consumption Per Second--");
			Map<Item, Fraction> production = Main.ratioSolver.raw();
			for (Item itemi : production.keySet()) {
				System.out.println("\t" + production.get(itemi) + " x " + itemi);
			}
			System.out.println();
			
			System.out.println("--Total Production Per Second--");
			Machine machine = tree.getRootValue().getMachine();
			Fraction machineCount = tree.getRootValue().getCount();
			Fraction prodPerSec = machineCount.multiply(machine.outputCountPerSec());
			System.out.println("\t" + prodPerSec + " x " + item);
			System.out.println();
			
		} catch (ItemNotRegisteredException e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}
