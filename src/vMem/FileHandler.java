package vMem;

import java.io.*;
import java.util.*;

public class FileHandler {
	public static HashMap<String, Integer> readFile(String filePath) throws FileNotFoundException {
		HashMap<String, Integer> config = new HashMap<>();
		try (Scanner sc = new Scanner(new File(filePath))) {
			String line;
			while (sc.hasNext()) {
				line = sc.nextLine();
				String[] arr = line.split("=");
				String key = arr[0].trim();
				int val = Integer.parseInt(arr[1].trim());
				config.put(key, val);
			}
		}
		return config;
	}
}
