package edu.rit.se.security.fuzzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a web page seen by the system
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class WebPage {

	private boolean searched;
	private String url;
	private List<String> forms;
	private List<String> urlInputs;
	
	public WebPage() {
		url = new String();
		forms = new ArrayList<String>();
		urlInputs = new ArrayList<String>();
		searched = false;
	}
	
	public WebPage(String url) {
		this.url = url;
		forms = new ArrayList<String>();
		urlInputs = new ArrayList<String>();
		searched = false;
	}
	
	public List<String> getForms() {
		return forms;
	}
	
	public List<String> getUrlInputs() {
		return urlInputs;
	}
	
	public String getURL() {
		return url;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public boolean isSearched() {
		return searched;
	}
	
	public void setSearched(boolean searched) {
		this.searched = searched;
	}
	
	public boolean equals(Object o) {
		if (o instanceof WebPage) {
			if (url.equals(((WebPage) o).getURL())) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		int i = 0;
		for (char c: url.toCharArray()) {
			i += c;
		}
		return i;
	}

}
