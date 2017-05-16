package init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.math3.fraction.Fraction;

import recipe.ItemIsCountableException;
import recipe.ItemNotRegisteredException;
import recipe.MachineClass;

public class MachineInit {
	public static Map<String, MachineClass> readFromDirectory(File directory) throws FileNotFoundException, IOException {
		Map<String, MachineClass> machines = new HashMap<>();
		if (!directory.isDirectory()) {
			throw new IOException(directory.getPath() + " is not a directory");
		}
		
		for (File file : directory.listFiles()) {
			try {
				MachineClass machine = readFromFile(file);
				
				if (machine == null) {
					System.err.println("Warning: Machine from file " + file + " was not read");
				} else {
					machines.put(machine.NAME, machine);
				}
			} catch (NoSuchElementException e) {
				System.err.println("Unable to read from " + file);
			} catch (ItemNotRegisteredException | ItemIsCountableException e) {
				System.err.println("Error in file " + file + ": " + e.getMessage());
			}
		}
		
		return machines;
	}
	
	public static MachineClass readFromFile(File file) throws FileNotFoundException, IOException, ItemNotRegisteredException, ItemIsCountableException {
		return readFromStream(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
	}
	
	public static MachineClass readFromStream(BufferedReader reader) throws IOException, ItemNotRegisteredException, ItemIsCountableException {
		List<Fraction> speeds = new ArrayList<>();
		
		Iterator<String> lines = reader.lines().iterator();

		if (!lines.hasNext()) return null;
		
		String line = lines.next();
		String name = line.trim();

		if (!lines.hasNext()) return null;
		
		while (lines.hasNext()) {
			line = lines.next();
			
			Fraction speed = new Fraction(Float.parseFloat(line));
			
			speeds.add(speed);
		}
		
		return new MachineClass(name, speeds);
	}
}
