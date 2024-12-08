package vMem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class FileHandler {
	public static HashMap<String, Integer> readFile(String filePath) throws FileNotFoundException{
		HashMap<String,Integer> config = new HashMap<>();
		try(Scanner sc = new Scanner(new File(filePath))){
			String line;
			while(sc.hasNext()) {
				line=sc.nextLine();
				String[] arr = line.split("=");
				String key=arr[0];
				int val = Integer.parseInt(arr[1]);
				config.put(key, val);
				
			}
		}
		return config;
	}
}
