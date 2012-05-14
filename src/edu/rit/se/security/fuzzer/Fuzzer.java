package edu.rit.se.security.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import edu.rit.se.security.fuzzer.properties.FuzzerProperties;

/**
 * Main class for running fuzzer.
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class Fuzzer {
	
	private List<WebPage> pages;
	private WebClient client;
	private String url;
	private boolean loginSuccessful;
	private List<String> sensitiveData;
	
	public Fuzzer() {
		//Make sure we can get the properties file first
		FuzzerProperties.discoverPages();
		pages = new LinkedList<WebPage>();
		client = new TimedWebClient();
		client.setJavaScriptEnabled(true);
		client.setPrintContentOnFailingStatusCode(false);
		url = FuzzerProperties.getURL();
		loginSuccessful = false;
		sensitiveData = FuzzerProperties.getSensitiveData();
	}
	
	public static void main(String[] args) {
		Fuzzer fuzzer = new Fuzzer();
		//Get your fuzz on!
		fuzzer.fuzz();
	}
	
	public void fuzz() {
		//Add first page to list of pages discovered
		discoverPages(FuzzerProperties.getURL());
		
		//Login
		login();
		
		//Once all pages are discovered, discover input for each page
		discoverForms();
		
		//Now test the input possibilities
		testInput();
		
		//Test passwords
		testPasswords();
		
		//Output discoveries
		print();
		
		//Close out client
		client.closeAllWindows();
	}
	
	private void discoverPages(String base) {
		if (FuzzerProperties.discoverPages()) {
			// Discover pages recursively
			List<WebPage> toAdd = new ArrayList<WebPage>();
			discoverLinks(base, breakDownUrl(base));
		}
		
		if (FuzzerProperties.guessPages()) {
			//Discover pages through guesses
			for (String pageUrl : FuzzerProperties.getPageGuessList()) {
				try {
					HtmlPage html = client.getPage(base + "/" + pageUrl);
					System.out.println("URL-DISCOVERY:GUESS - Valid url found " + base + "/" + pageUrl);
					WebPage page = breakDownUrl(base + "/" + pageUrl);
					containsSensitiveData(page, html);
				} catch (FailingHttpStatusCodeException e) {
					//Url does not work
					System.out.println("URL-DISCOVERY:GUESS - Url not valid " + base + "/" + pageUrl);
				} catch (MalformedURLException e) {
					//Invalid url in file
					System.err.println("URL-DISCOVERY:GUESS - Invalid url in guess page file " + pageUrl);
				} catch (IOException e) {
					//Error
					System.err.println("URL-DISCOVERY:GUESS - " + e.getMessage());
				}
			}
		}
	}
	
	private void discoverForms() {
		for (WebPage page : pages) {
			try {
				HtmlPage html = client.getPage(FuzzerProperties.getBaseURL() + page.getURL());
				containsSensitiveData(page, html);
				List<HtmlForm> forms = html.getForms();
				for (HtmlForm form : forms) {
					page.getForms().add(form);
					try {
						form.getInputByName(FuzzerProperties.getPasswordId());
						page.setPassword(true, form);
					} catch (ElementNotFoundException e) {}
				}
			} catch (FailingHttpStatusCodeException e) {
				System.err.println("Discover Forms - " + e.getMessage());
			} catch (MalformedURLException e) {
				System.err.println("Discover Forms - " + e.getMessage());
			} catch (IOException e) {
				System.err.println("Discover Forms - " + e.getMessage());
			}
		}
	}
	
	private void testInput() {
		if (FuzzerProperties.fullInputTest()) {
			for (WebPage page : pages) {
				if (page.getForms().size() != 0) {
					testPageFull(page);
				}
			}
		} else {
			for (int i = 0; i < FuzzerProperties.getRandomInputNumber(); i++) {
				
			}
		}
	}
	
	private WebPage breakDownUrl(String pageUrl) {
		WebPage page = null;
		try {
			URL url = new URL(pageUrl);
			//See if page exists already
			if (pages.contains(new WebPage(url.getPath()))) {
				for (WebPage compare : pages) {
					if (compare.getURL().equals(url.getPath())) {
						page = compare;
						break;
					}
				}
			} else {
				page = new WebPage(url.getPath());
				pages.add(page);
			}
			
			//Parse query and add to WebPage if not already found
			if (url.getQuery() != null) {
				for (String query : url.getQuery().split("&")) {
					String input = query.split("=")[0];
					if (!page.getUrlInputs().contains(input)) {
						page.getUrlInputs().add(input);
					}
				}
			}
			
		} catch (MalformedURLException e) {
			System.err.println("URLBreakdown - " + e.getMessage());
		}
		
		return page;
	}
	
	private void print() {
		System.out.println();
		System.out.println("Valid pages discovered:");
		for (WebPage page: pages) {
			System.out.println(page.getURL());
			System.out.println("\tURL INPUTS: " + page.getUrlInputs().size());
			for (String url : page.getUrlInputs()) {
				System.out.println("\t\t" + url);
			}
			System.out.println("\tFORM INPUTS: " + page.getForms().size());
			System.out.println("\tPASSWORD FORM: " + page.isPassword());
			if (page.isPassword()) {
				System.out.println("\tSUCCESSFUL PASSWORDS: " + page.getSuccessfulPasswords().size());
				for (String success : page.getSuccessfulPasswords()) {
					System.out.println("\t\t" + success);
				}
			}
			System.out.println("\tSENSITIVE DATA LEAKED: " + page.getSensitiveDataFound().size());
			for (String data : page.getSensitiveDataFound()) {
				System.out.println("\t\t" + data);
			}
			System.out.println();
			
		}
			
		System.out.print("LOGIN ATTEMPT: ");
		if (loginSuccessful) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("FAILURE");
		}
		System.out.println("\tLOGIN URL: " + FuzzerProperties.getLoginUrl());
		System.out.println("\tUSERNAME: " + FuzzerProperties.getUsername());
		System.out.println("\tPASSWORD: " + FuzzerProperties.getPassword());
	}
	
	private void discoverLinks(String base, WebPage page) {
		try {
			HtmlPage html = client.getPage(base);
			containsSensitiveData(page, html);
			List<HtmlAnchor> links = html.getAnchors();
			for (HtmlAnchor link : links) {
				URL tmp = new URL(url + "/" + link.getHrefAttribute());
				if (!pages.contains(new WebPage(tmp.getPath()))) {
					WebPage temp = breakDownUrl(url + "/" + link.getHrefAttribute());
					discoverLinks(url + "/" + link.getHrefAttribute(), temp);
				} else {
					WebPage temp = breakDownUrl(url + "/" + link.getHrefAttribute());
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			System.err.println("Invalid link - " + e.getMessage());
		} catch (MalformedURLException e) {
			System.err.println("Invalid link - " + e.getMessage());
		} catch	(IOException e) {
			System.err.println("Invalid link - " + e.getMessage());
		}
	}
	
	private void testPasswords() {
		if (FuzzerProperties.guessPasswords()) {
			for (WebPage page : pages) {
				if (page.isPassword()) {
					for (String password : FuzzerProperties.getPasswords()) {
						try {
							HtmlForm form = page.getPasswordForm();
							try {
								HtmlInput usernameInput = form.getInputByName(FuzzerProperties.getUsernameId());
								usernameInput.setValueAttribute(FuzzerProperties.getUsername());
								HtmlInput passwordInput = form.getInputByName(FuzzerProperties.getPasswordId());
								passwordInput.setValueAttribute(password);
								
								HtmlSubmitInput submit = form.getInputByName(FuzzerProperties.getSubmitId());
								
								String content = submit.<HtmlPage> click().getWebResponse().getContentAsString();
								
								if (content.contains(FuzzerProperties.getLoginSuccessContent())) {
									page.getSuccessfulPasswords().add(password);
								}
							} catch (ElementNotFoundException e) {
								System.err.println("Test Passwords - Submit button not found!");
							} catch (IOException e) {
								System.err.println("Test Passwords - " + e.getMessage());
							}

						} catch (FailingHttpStatusCodeException e) {
							System.err.println("Test Passwords - " + e.getMessage());
						}
					}
					
				}
			}
		}
	}
	
	private void testPageFull(WebPage page) {
		// TODO Test form with all input
	}
	
	private void login() {
		try {
			HtmlPage page = client.getPage(FuzzerProperties.getLoginUrl());
			for (HtmlForm form : page.getForms()) {
				try {
					HtmlInput username = form.getInputByName(FuzzerProperties.getUsernameId());
					username.setValueAttribute(FuzzerProperties.getUsername());
					HtmlInput password = form.getInputByName(FuzzerProperties.getPasswordId());
					password.setValueAttribute(FuzzerProperties.getPassword());
					
					HtmlSubmitInput submit = form.getInputByName(FuzzerProperties.getSubmitId());
					
					String content = submit.<HtmlPage> click().getWebResponse().getContentAsString();
					
					if (content.contains(FuzzerProperties.getLoginSuccessContent())) {
						loginSuccessful = true;
					}
				} catch(ElementNotFoundException e) {
					
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			System.err.println("Login - " + e.getMessage());
		} catch (MalformedURLException e) {
			System.err.println("Login - " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Login - " + e.getMessage());
		}
	}
	
	private void containsSensitiveData(WebPage page, HtmlPage content) {
		for (String data : sensitiveData) {
			if (content.asXml().contains(data) && !page.getSensitiveDataFound().contains(data)) {
				page.getSensitiveDataFound().add(data);
			}
		}
	}

}
