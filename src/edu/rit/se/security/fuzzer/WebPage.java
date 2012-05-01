package edu.rit.se.security.fuzzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a web page seen by the system
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class WebPage {

	private String url;
	private List<String> forms;
	
	public WebPage() {
		url = new String();
		forms = new ArrayList<String>();
	}
	
	public WebPage(String url) {
		this.url = url;
		forms = new ArrayList<String>();
	}
	
	public List<String> getForms() {
		return forms;
	}

}
