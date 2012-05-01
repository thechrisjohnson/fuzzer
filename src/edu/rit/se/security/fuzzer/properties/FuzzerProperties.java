package edu.rit.se.security.fuzzer.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * The class that handles access of properties.
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class FuzzerProperties extends Properties {
	
	//Default values
	private static final String defaultFile = "";

	//Property keys
	private static final String pageDiscovery = "pagediscovery";
	private static final String pageGuessing = "pageguessing";
	private static final String pageGuessingFile = "pageguessingfile";
	
	private static final long serialVersionUID = 5710264569101379889L;
	private static FuzzerProperties properties = null;
	
	public static FuzzerProperties getPropertyManager() {
		if (properties == null) {
			properties = new FuzzerProperties();
		}
		
		return properties;
	}
	
	public FuzzerProperties(String file) {
		try {
			FileInputStream input = new FileInputStream(file);
			this.load(input);
		} catch(FileNotFoundException e) {
			System.err.println("FuzzerProperties: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("FuzzerProperties: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public FuzzerProperties() {
		this(defaultFile);
	}
	
	public static boolean discoverPages() {
		if (FuzzerProperties.getPropertyManager().getProperty(pageDiscovery) != null
				&& FuzzerProperties.getPropertyManager().getProperty(pageDiscovery).equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean guessPages() {
		if (FuzzerProperties.getPropertyManager().getProperty(pageGuessing) != null
				&& FuzzerProperties.getPropertyManager().getProperty(pageGuessing).equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static List<String> getPageGuessList() {
		List<String> guessList = new ArrayList<String>();
		
		if (FuzzerProperties.getPropertyManager().getProperty(pageGuessingFile) != null) {
			Scanner input = new Scanner(FuzzerProperties.getPropertyManager().getProperty(pageGuessingFile));
			while (input.hasNext()) {
				guessList.add(input.nextLine());
			}
		}
		
		return guessList;
	}

}
