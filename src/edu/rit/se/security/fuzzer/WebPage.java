package edu.rit.se.security.fuzzer;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlForm;

/**
 * Represents a web page seen by the system
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class WebPage {

	private boolean password;
	private HtmlForm passwordForm;
	private String url;
	private List<HtmlForm> forms;
	private List<String> urlInputs;
	private List<String> successfulPasswords;
	private List<String> sensitiveDataFound;
	private List<String> sanitizedDataFound;
	
	public WebPage() {
		url = new String();
		forms = new ArrayList<HtmlForm>();
		urlInputs = new ArrayList<String>();
		password = false;
		successfulPasswords = new ArrayList<String>();
		sensitiveDataFound = new ArrayList<String>();
		sanitizedDataFound = new ArrayList<String>();
	}
	
	public WebPage(String url) {
		this.url = url;
		forms = new ArrayList<HtmlForm>();
		urlInputs = new ArrayList<String>();
		password = false;
		successfulPasswords = new ArrayList<String>();
		sensitiveDataFound = new ArrayList<String>();
		sanitizedDataFound = new ArrayList<String>();
	}
	
	public List<HtmlForm> getForms() {
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
	
	public boolean isPassword() {
		return password;
	}
	
	public HtmlForm getPasswordForm() {
		return passwordForm;
	}
	
	public void setPassword(boolean password, HtmlForm form) {
		this.password = password;
		passwordForm = form;
	}
	
	public List<String> getSuccessfulPasswords() {
		return successfulPasswords;
	}
	
	public List<String> getSensitiveDataFound() {
		return sensitiveDataFound;
	}
	
	public List<String> getSanitizedDataFound() {
		return sanitizedDataFound;
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