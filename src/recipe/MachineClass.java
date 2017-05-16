package recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.fraction.Fraction;

public class MachineClass {
	public final String NAME;
	private List<Fraction> speeds;
	
	public MachineClass(String name, Fraction... speeds) {
		this(name, Arrays.asList(speeds));
	}
	
	public MachineClass(String name, List<Fraction> speeds) {
		this.NAME = name.toLowerCase();
		this.speeds = new ArrayList<>();
		this.speeds.addAll(speeds);
	}
	
	/**
	 * Indexing starts at one
	 */
	public Fraction getSpeed(int level) throws MachineLevelOutOfBoundsException {
		return speeds.get(level - 1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MachineClass) {
			MachineClass mc = (MachineClass)o;
			return mc.NAME.equals(this.NAME)
					&& mc.speeds.equals(this.speeds);
		}
	
		return false;
	}
	
	@Override
	public int hashCode() {
		return NAME.hashCode();
	}

	/**
	 * Indexing starts at one
	 */
	public boolean hasLevel(int level) {
		return level > 0 && level <= speeds.size();
	}
	
	@Override
	public String toString() {
		return NAME;
	}
}
