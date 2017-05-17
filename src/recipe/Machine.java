package recipe;

import org.apache.commons.math3.fraction.Fraction;

public class Machine {
	private MachineClass machineClass;
	private int machineLevel;
	private Recipe recipe;
	
	public Machine(MachineClass machineClass, int level, Recipe recipe) throws MachineLevelOutOfBoundsException {
		if (!machineClass.hasLevel(level)) {
			throw new MachineLevelOutOfBoundsException();
		}
		
		this.machineClass = machineClass;
		this.machineLevel = level;
		this.recipe = recipe;
	}
	
	public Recipe recipe() {
		return recipe;
	}
	
	public MachineClass machineClass() {
		return machineClass;
	}
	
	public int level() {
		return machineLevel;
	}
	
	public Fraction speed() {
		try {
			return machineClass.getSpeed(machineLevel);
		} catch (MachineLevelOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Fraction time() {
		return recipe.time().divide(speed());
	}
	
	@Override
	public int hashCode() {
		return recipe.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Machine) {
			Machine m = (Machine)o;
			return m.machineClass.NAME.equals(this.machineClass.NAME)
					&& m.machineLevel == this.machineLevel
					&& m.recipe.equals(this.recipe);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return this.machineClass().name(this.level()) + " [" + this.time().floatValue() + "s] (" + this.recipe().output() + ")";
	}
	
	public String toString(boolean fullRecipe) {
		if (fullRecipe) {
			return this.machineClass().name(this.level()) + " [" + this.time().floatValue() + "s]" + this.recipe();
		}
		
		return toString();
	}
}
