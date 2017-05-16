package ratio;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.util.Pair;

import recipe.Machine;

public class MachineCount extends Pair<Machine, Fraction> {
	public MachineCount(Pair<? extends Machine, ? extends Fraction> entry) {
		super(entry);
	}
	
	public MachineCount(Machine machine, Fraction fraction) {
		super(machine, fraction);
	}
	
	public Machine getMachine() {
		return this.getFirst();
	}
	
	public Fraction getCount() {
		return this.getSecond();
	}
	
	@Override
	public String toString() {
		return this.getCount() + " x " + this.getMachine();
	}
}
