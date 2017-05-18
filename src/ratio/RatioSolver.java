package ratio;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Set<Item> doNotSolve;
	private Set<MachineClass> doNotUse;
	
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
		this.doNotSolve = new HashSet<>();
		this.doNotUse = new HashSet<>();
	}
	
	/**
	 * Consider the item as a raw item, which is not crafted using a recipe
	 * @param item
	 * @param setRaw 
	 */
	public void setRaw(Item item, boolean setRaw) {
		if (setRaw) {
			doNotSolve.add(item);
		} else {
			doNotSolve.remove(item);
		}
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
	
	/**
	 * Returns the recipe for a given item. This recipe is null if the
	 * item will not be expanded in any way. For any recipe, regardless
	 * of whether it will be expanded or not, use {@link #getRecipeAlways}
	 * @param item
	 * @return
	 */
	public Recipe getRecipe(Item item) {
		Recipe recipe;
		
		if (doNotSolve.contains(item)) return null;
		
		recipe = getRecipeAlways(item);
		
		if (recipe == null) return recipe;
		if (doNotUse.contains(recipe.machineClass())) return null;
		
		return recipe;
	}
	
	public Recipe getRecipeAlways(Item item) {
		return recipes.get(item);
	}
	
	protected Tree<MachineCount> solveInteger(Item item) {
		try {
			int multiple = multipleToWhole(item);
			Recipe recipe = getRecipe(item);
			
			//System.out.println(recipes);
			if (recipe == null) return null;
			
			MachineClass mc = recipe.machineClass();
			Machine m = new Machine(mc, machineLevels.get(mc), recipe);
			
			return solveRecurse(item, new Fraction(multiple * recipe.outputCount()).divide(m.time()));
		} catch (MachineLevelOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Tree<MachineCount> solveRecurse(Item item, Fraction itemPerSec) throws MachineLevelOutOfBoundsException {
		Recipe recipe = getRecipe(item);
		
		/*
		 * If the recipe is null, we have a raw ingredient
		 */
		if (recipe == null) {
			if (raw.containsKey(item)) {
				raw.put(item, raw.get(item).add(itemPerSec));
			} else {
				raw.put(item, itemPerSec);
			}
			return null;
		}
		
		MachineClass mc = recipe.machineClass();
		Machine machine = new Machine(mc, machineLevels.get(mc), recipe);
		Fraction machineCount = itemPerSec.multiply(machine.time()).divide(recipe.outputCount());
		Tree<MachineCount> tree = new Tree<>(new MachineCount(machine, machineCount));
		
		if (machineCounts.containsKey(machine)) {
			machineCounts.put(machine, machineCounts.get(machine).add(machineCount));
		} else {
			machineCounts.put(machine, machineCount);
		}
		
		for (Ingredient ingredient : recipe.ingredients()) {
			Tree<MachineCount> child = solveRecurse(ingredient.item(), itemPerSec.multiply(ingredient.count()).divide(recipe.outputCount()));
			if (child != null) {
				tree.addChild(child);
			}
		}
		
		return tree;
	}
	
	protected int multipleToWhole(Item item) throws MachineLevelOutOfBoundsException {
		int multiple = 1;
		ArrayDeque<Pair<Machine, Fraction>> machinesToGo = new ArrayDeque<>();
		
		Recipe recipe = getRecipe(item);
		if (recipe == null) return multiple;
		
		MachineClass mc = recipe.machineClass();
		Machine machine = new Machine(mc, machineLevels.get(mc), recipe);
		
		Fraction itemPerSec = new Fraction(recipe.outputCount()).divide(machine.time());
		
		machinesToGo.addLast(new Pair<>(machine, itemPerSec));
		
		while (!machinesToGo.isEmpty()) {
			Pair<Machine, Fraction> next = machinesToGo.pop();
			machine = next.getFirst();
			recipe = machine.recipe();
			itemPerSec = next.getSecond();
			
			Fraction machines = itemPerSec.multiply(machine.time()).multiply(multiple).divide(recipe.outputCount());
			
			//System.out.println(machines + " x " + recipe);
			
			multiple *= machines.getDenominator();
			//System.out.println(multiple);
			
			for (Ingredient ingredient : recipe.ingredients()) {
				Recipe nextRecipe = getRecipe(ingredient.item());
				
				if (nextRecipe == null) continue;
				
				MachineClass nextMc = nextRecipe.machineClass();
				Machine nextMachine = new Machine(nextMc, machineLevels.get(nextMc), nextRecipe);
				
				machinesToGo.add(new Pair<>(nextMachine, itemPerSec.multiply(ingredient.count()).divide(recipe.outputCount())));
			}
		}
		
		return multiple;
	}

	public void setMachineLevel(MachineClass mc, Integer level) throws MachineLevelOutOfBoundsException {
		if (!mc.hasLevel(level)) {
			throw new MachineLevelOutOfBoundsException(mc, level);
		}
		machineLevels.put(mc, level);
	}

	public void useMachineClass(MachineClass mc, boolean on) {
		if (on) {
			doNotUse.remove(mc);
		} else {
			doNotUse.add(mc);
		}
	}
}
