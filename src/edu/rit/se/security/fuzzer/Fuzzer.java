package edu.rit.se.security.fuzzer;

import edu.rit.se.security.fuzzer.properties.FuzzerProperties;

/**
 * Main class for running fuzzer.
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class Fuzzer {
	
	public Fuzzer() {
		//Make sure we can get the properties first
		FuzzerProperties.getPropertyManager();
	}
	
	public static void main(String[] args) {
		Fuzzer fuzzer = new Fuzzer();
		//Get your fuzz on!
		fuzzer.fuzz();
	}
	
	public void fuzz() {
		
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
