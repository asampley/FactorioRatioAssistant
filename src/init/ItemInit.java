package init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import recipe.Item;
import recipe.ItemAlreadyRegisteredException;

public class ItemInit {
	public static void registerFromFile(File file) throws FileNotFoundException, IOException {
		registerFromStream(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
	}
	
	public static void registerFromStream(BufferedReader reader) throws IOException {
		Stream<String> lines = reader.lines();
		
		lines.forEachOrdered((String s) -> {
			try {
				Item.register(s);
			} catch (ItemAlreadyRegisteredException e) {
				System.err.println(e.getMessage());
			}
		});
	}
}
