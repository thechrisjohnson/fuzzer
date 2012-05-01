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
	private static final String defaultPropertyFile = "";
	private static final String defaultUrl = "http://localhost";
	private static final String defaultPageDiscovery = "false";
	private static final String defaultGuessing = "false";
	private static final String defaultGuessingFile = "";
	private static final String defaultTimeGap ="0";
	private static final String defaultFuzzListFile = "";

	//Property keys
	private static final String url = "url";
	private static final String pageDiscovery = "pagediscovery";
	private static final String pageGuessing = "pageguessing";
	private static final String pageGuessingFile = "pageguessingfile";
	private static final String timeGap = "timegap";
	private static final String fuzzListFile = "fuzzlistfile";
	
	private static final long serialVersionUID = 5710264569101379889L;
	private static FuzzerProperties properties = null;
	
	private static FuzzerProperties getPropertyManager() {
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
		this(defaultPropertyFile);
	}
	
	public static boolean discoverPages() {
		if (FuzzerProperties.getPropertyManager().getProperty(pageDiscovery, defaultPageDiscovery).equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean guessPages() {
		if (FuzzerProperties.getPropertyManager().getProperty(pageGuessing, defaultGuessing).equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static long getTimeGap() {
		return Long.getLong(FuzzerProperties.getPropertyManager().getProperty(timeGap, defaultTimeGap));
	}
	
	public static List<String> getPageGuessList() {
		List<String> guessList = new ArrayList<String>();
		
		if (FuzzerProperties.guessPages()) {
			Scanner input = new Scanner(FuzzerProperties.getPropertyManager().getProperty(pageGuessingFile, defaultGuessingFile));
			while (input.hasNext()) {
				guessList.add(input.nextLine());
			}
		}
		
		return guessList;
	}
	
	public static List<String> getFuzzList() {
		List<String> fuzzList = new ArrayList<String>();
		
		Scanner input = new Scanner(FuzzerProperties.getPropertyManager().getProperty(fuzzListFile, defaultFuzzListFile));
		while (input.hasNext()) {
			fuzzList.add(input.nextLine());
		}
		
		return fuzzList;
	}
	
	public static String getURL() {
		return FuzzerProperties.getPropertyManager().getProperty(url, defaultUrl);
	}

}
