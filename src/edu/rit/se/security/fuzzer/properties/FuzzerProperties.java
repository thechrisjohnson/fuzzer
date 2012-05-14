package edu.rit.se.security.fuzzer.properties;

import java.io.File;
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
	private static final String defaultPropertyFile = "config.properties";
	private static final String defaultUrl = "http://localhost";
	private static final String defaultPageDiscovery = "false";
	private static final String defaultGuessing = "false";
	private static final String defaultGuessingFile = "urlguesslist.txt";
	private static final String defaultTimeGap ="0";
	private static final String defaultFuzzListFile = "fuzzlist.txt";
	private static final String defaultInputTest = "random";
	private static final String defaultBaseUrl = "http://localhost:8080";
	private static final String defaultPasswordsFile = "passwords.txt";
	private static final String defaultUsernameId = "username";
	private static final String defaultPasswordId = "password";
	private static final String defaultSubmitId = "submit";
	private static final String defaultGuessPasswords = "false";
	private static final String defaultUsername = "username";
	private static final String defaultPassword ="password";
	private static final String defaultLoginSuccessContent = "Success";
	private static final String defaultLoginUrl = "http://localhost:8080/bodgeit/login.jsp";
	private static final String defaultRandomInputNumber = "10";
	private static final String defaultSensitiveDataFile = "sensitivedata.txt";

	//Property keys
	private static final String url = "url";
	private static final String pageDiscovery = "pagediscovery";
	private static final String pageGuessing = "pageguessing";
	private static final String pageGuessingFile = "pageguessingfile";
	private static final String timeGap = "timegap";
	private static final String fuzzListFile = "fuzzlistfile";
	private static final String inputTest = "inputtest";
	private static final String baseUrl = "baseurl";
	private static final String passwordsFile = "passwordsfile";
	private static final String usernameId = "usernameid";
	private static final String passwordId = "passwordid";
	private static final String submitId = "submitid";
	private static final String guessPasswords = "passwordguessing";
	private static final String username = "username";
	private static final String password = "password";
	private static final String loginSuccessContent = "loginsuccesscontent";
	private static final String loginUrl = "loginurl";
	private static final String randomInputNumber = "randominputnumber";
	private static final String sensitiveDataFile = "sensitivedatafile";
	
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
	
	public static Long getTimeGap() {
		return Long.valueOf(FuzzerProperties.getPropertyManager().getProperty(timeGap, defaultTimeGap));
	}
	
	public static List<String> getPageGuessList() {
		List<String> guessList = new ArrayList<String>();
		
		if (FuzzerProperties.guessPages()) {
			Scanner input;
			try {
				input = new Scanner(new File(FuzzerProperties.getPropertyManager().getProperty(pageGuessingFile, defaultGuessingFile)));
				while (input.hasNext()) {
					guessList.add(input.nextLine());
				}
			} catch (FileNotFoundException e) {
				System.err.println("FuzzerProperties: " + e.getMessage());
			}
		}
		
		return guessList;
	}
	
	public static List<String> getFuzzList() {
		List<String> fuzzList = new ArrayList<String>();
		
		try {
			Scanner input = new Scanner(new File(FuzzerProperties.getPropertyManager().getProperty(fuzzListFile, defaultFuzzListFile)));
			while (input.hasNext()) {
				fuzzList.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("FuzzerProperties: " + e.getMessage());

		}
		
		return fuzzList;
	}
	
	public static List<String> getPasswords() {
		List<String> passwords = new ArrayList<String>();
		
		try {
			Scanner input = new Scanner(new File(FuzzerProperties.getPropertyManager().getProperty(passwordsFile, defaultPasswordsFile)));
			while (input.hasNext()) {
				passwords.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("FuzzerProperties: " + e.getMessage());
		}
		return passwords;
	}
	
	public static List<String> getSensitiveData() {
		List<String> data = new ArrayList<String>();
		
		try {
			Scanner input = new Scanner(new File(FuzzerProperties.getPropertyManager().getProperty(sensitiveDataFile, defaultSensitiveDataFile)));
			while (input.hasNext()) {
				data.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("FuzzerProperties: " + e.getMessage());
		}
		return data;
	}
	
	public static String getURL() {
		return FuzzerProperties.getPropertyManager().getProperty(url, defaultUrl);
	}
	
	public static boolean fullInputTest() {
		return FuzzerProperties.getPropertyManager().getProperty(inputTest, defaultInputTest).equals("full");
	}
	
	public static String getBaseURL() {
		return FuzzerProperties.getPropertyManager().getProperty(baseUrl, defaultBaseUrl);
	}
	
	public static String getPasswordId() {
		return FuzzerProperties.getPropertyManager().getProperty(passwordId, defaultPasswordId);
	}
	
	public static String getUsernameId() {
		return FuzzerProperties.getPropertyManager().getProperty(usernameId, defaultUsernameId);
	}
	
	public static String getSubmitId() {
		return FuzzerProperties.getPropertyManager().getProperty(submitId, defaultSubmitId);
	}
	
	public static boolean guessPasswords() {
		if (FuzzerProperties.getPropertyManager().getProperty(guessPasswords, defaultGuessPasswords).equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getUsername() {
		return FuzzerProperties.getPropertyManager().getProperty(username, defaultUsername);
	}
	
	public static String getPassword() {
		return FuzzerProperties.getPropertyManager().getProperty(password, defaultPassword);
	}

	public static String getLoginSuccessContent() {
		return FuzzerProperties.getPropertyManager().getProperty(loginSuccessContent, defaultLoginSuccessContent);
	}
	
	public static String getLoginUrl() {
		return FuzzerProperties.getPropertyManager().getProperty(loginUrl, defaultLoginUrl);
	}
	
	public static Long getRandomInputNumber() {
		return Long.valueOf(FuzzerProperties.getPropertyManager().getProperty(randomInputNumber, defaultRandomInputNumber));
	}
}
