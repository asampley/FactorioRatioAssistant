package recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.fraction.Fraction;

public class MachineClass {
	public final String NAME;
	private List<Fraction> speeds;
	private List<String> names;
	
	public MachineClass(String name, Fraction[] speeds, String[] names) {
		this(name, Arrays.asList(speeds), Arrays.asList(names));
	}
	
	public MachineClass(String name, List<Fraction> speeds, List<String> names) {
		this.NAME = name.toLowerCase();
		this.speeds = new ArrayList<>();
		this.names = new ArrayList<>();
		
		if (speeds.size() != names.size()) {
			throw new IllegalArgumentException("Names and speeds are not the same size");
		}
		
		this.speeds.addAll(speeds);
		this.names.addAll(names);
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

	/**
	 * Indexing starts at 1
	 * @param level level of machine in range [1,n]
	 * @return
	 */
	public String name(int level) {
		return names.get(level - 1);
	}
}
