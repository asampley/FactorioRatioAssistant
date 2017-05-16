package ratio;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import recipe.Ingredient;
import recipe.Item;
import recipe.Machine;
import recipe.MachineClass;
import recipe.MachineLevelOutOfBoundsException;
import recipe.Recipe;
import tree.Tree;

public class RatioSolver {
	private Map<Item, Recipe> recipes;
	
	private Tree<MachineCount> solution;
	private Map<Item, Fraction> raw;
	private Map<Machine, Fraction> machineCounts;
	private Map<MachineClass, Integer> machineLevels;
	
	public RatioSolver(Map<Item, Recipe> recipes, Map<MachineClass, Integer> machineLevels) {
		this.recipes = new HashMap<>();
		this.recipes.putAll(recipes);
		this.raw = new HashMap<>();
		this.machineCounts = new HashMap<>();
		this.machineLevels = new HashMap<>();
		this.machineLevels.putAll(machineLevels);
	}
	
	public void solve(Item item) {
		raw.clear();
		machineCounts.clear();
		solution = solveInteger(item);
	}
	
	public Tree<MachineCount> solution() {
		return solution;
	}
	
	public Map<Item, Fraction> raw() {
		return raw;
	}
	
	public Map<Machine, Fraction> machines() {
		return machineCounts;
	}
	
	protected Tree<MachineCount> solveInteger(Item item) {
		try {
			int multiple = multipleToWhole(item);
			Recipe recipe = recipes.get(item);
			
			//System.out.println(recipes);
			if (recipe == null) return null;
			
			MachineClass mc = recipe.machineClass();
			Machine m = new Machine(mc, machineLevels.get(mc), recipe);
			
			return solveRecurse(item, new Fraction(multiple).divide(m.time()));
		} catch (MachineLevelOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Tree<MachineCount> solveRecurse(Item item, Fraction nPerSec) throws MachineLevelOutOfBoundsException {
		Recipe recipe = recipes.get(item);
		
		/*
		 * If the recipe is null, we have a raw ingredient
		 */
		if (recipe == null) {
			if (raw.containsKey(item)) {
				raw.put(item, raw.get(item).add(nPerSec));
			} else {
				raw.put(item, nPerSec);
			}
			return null;
		}
		
		MachineClass mc = recipe.machineClass();
		Machine machine = new Machine(mc, machineLevels.get(mc), recipe);
		Fraction machineCount = nPerSec.multiply(machine.time().divide(recipe.outputCount()));
		Tree<MachineCount> tree = new Tree<>(new MachineCount(machine, machineCount));
		
		if (machineCounts.containsKey(machine)) {
			machineCounts.put(machine, machineCounts.get(machine).add(machineCount));
		} else {
			machineCounts.put(machine, machineCount);
		}
		
		for (Ingredient ingredient : recipe.ingredients()) {
			Tree<MachineCount> child = solveRecurse(ingredient.item(), nPerSec.multiply(ingredient.count()).divide(recipe.outputCount()));
			if (child != null) {
				tree.addChild(child);
			}
		}
		
		return tree;
	}
	
	protected int multipleToWhole(Item item) throws MachineLevelOutOfBoundsException {
		int multiple = 1;
		ArrayDeque<Pair<Machine, Fraction>> machinesToGo = new ArrayDeque<>();
		
		Recipe recipe = recipes.get(item);
		if (recipe == null) return multiple;
		
		MachineClass mc = recipe.machineClass();
		Machine machine = new Machine(mc, machineLevels.get(mc), recipe);
		
		Fraction recipePerSec = new Fraction(1).divide(machine.time());
		
		machinesToGo.addLast(new Pair<>(machine, recipePerSec));
		
		while (!machinesToGo.isEmpty()) {
			Pair<Machine, Fraction> next = machinesToGo.pop();
			machine = next.getFirst();
			recipe = machine.recipe();
			recipePerSec = next.getSecond();
			
			Fraction machines = recipePerSec.multiply(machine.time()).multiply(multiple).divide(recipe.outputCount());
			
			//System.out.println(machines + " x " + recipe);
			
			multiple *= machines.getDenominator();
			//System.out.println(multiple);
			
			for (Ingredient ingredient : recipe.ingredients()) {
				Recipe nextRecipe = recipes.get(ingredient.item());
				
				if (nextRecipe == null) continue;
				
				mc = nextRecipe.machineClass();
				machine = new Machine(mc, machineLevels.get(mc), nextRecipe);
				
				machinesToGo.add(new Pair<>(machine, recipePerSec.multiply(ingredient.count())));
			}
		}
		
		return multiple;
	}

	public void setMachineLevel(MachineClass mc, Integer level) throws MachineLevelOutOfBoundsException {
		if (!mc.hasLevel(level)) {
			throw new MachineLevelOutOfBoundsException();
		}
		machineLevels.put(mc, level);
	}
}
