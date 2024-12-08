package vMem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class FileHandling {
	String file;
	public FileHandling(String fileName) {
		file=fileName;
	}
	public HashMap<String, Integer> readFile() throws FileNotFoundException{
		HashMap<String,Integer> config = new HashMap<>();
		try(Scanner sc = new Scanner(new File(file))){
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
