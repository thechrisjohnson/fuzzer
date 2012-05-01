package edu.rit.se.security.fuzzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.WebClient;

import edu.rit.se.security.fuzzer.properties.FuzzerProperties;

/**
 * Main class for running fuzzer.
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class Fuzzer {
	
	private WebClient client;
	private List<WebPage> pages;
	
	public Fuzzer() {
		//Make sure we can get the properties file first
		FuzzerProperties.discoverPages();
		pages = new ArrayList<WebPage>();
	}
	
	public static void main(String[] args) {
		Fuzzer fuzzer = new Fuzzer();
		//Get your fuzz on!
		fuzzer.fuzz();
	}
	
	public void fuzz() {
		//Create new client
		client = new WebClient();
		
		//Add first page to list of pages discovered
		pages.add(new WebPage(FuzzerProperties.getURL()));
		discoverPages();
		
		//Once all pages are discovered, discover input
		discoverInput();
		
		//Close out client
		client.closeAllWindows();
	}
	
	private void discoverInput() {
		
	}
	
	private void discoverPages() {
		if (FuzzerProperties.discoverPages()) {
			
		}
		
		if (FuzzerProperties.guessPages()) {
			
		}
	}

}
