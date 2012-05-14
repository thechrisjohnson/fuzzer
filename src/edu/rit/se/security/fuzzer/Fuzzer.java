package edu.rit.se.security.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

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
	private List<String> sanitizedData;
	private Random random;
	
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
		sanitizedData = FuzzerProperties.getSanitizedData();
		random = new Random();
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
			discoverLinks(base, breakDownUrl(base));
		}
		
		if (FuzzerProperties.guessPages()) {
			//Discover pages through guesses
			for (String pageUrl : FuzzerProperties.getPageGuessList()) {
				try {
					HtmlPage html = client.getPage(base + "/" + pageUrl);
					System.out.println("URL-DISCOVERY:GUESS - Valid url found " + base + "/" + pageUrl);
					WebPage page = breakDownUrl(base + "/" + pageUrl);
					containsImproperData(page, html);
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
				containsImproperData(page, html);
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
				testPageFull(page);
			}
		} else {
			for (int i = 0; i < FuzzerProperties.getRandomInputNumber(); i++) {
				int page = random.nextInt(pages.size());
				testPageRandom(pages.get(page));
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
			
			System.out.println("\tDATA IMPROPERLY SANITIZED: " + page.getSanitizedDataFound().size());
			for (String data : page.getSanitizedDataFound()) {
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
			containsImproperData(page, html);
			List<HtmlAnchor> links = html.getAnchors();
			for (HtmlAnchor link : links) {
				URL tmp = new URL(url + "/" + link.getHrefAttribute());
				if (!pages.contains(new WebPage(tmp.getPath()))) {
					WebPage temp = breakDownUrl(url + "/" + link.getHrefAttribute());
					discoverLinks(url + "/" + link.getHrefAttribute(), temp);
				} else {
					breakDownUrl(url + "/" + link.getHrefAttribute());
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
								
								HtmlSubmitInput submit = getSubmitInput(form);
								
								HtmlPage html = submit.<HtmlPage> click();
								containsImproperData(page, html);
								
								System.out.println("Guess Passwords - Attempting username: " + FuzzerProperties.getUsername()
										+ " password: " + password);
								
								String content = html.getWebResponse().getContentAsString();
								
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
		for (String urlInput : page.getUrlInputs()) {
			String base = url + page.getURL() + "?" + urlInput + "=";
			for (String input : FuzzerProperties.getInputs()) {
				try {
					System.out.println("Full Input Test - Accessing url: " + base + input);
					HtmlPage html = client.getPage(base + input);
					containsImproperData(page, html);
				} catch (FailingHttpStatusCodeException e) {
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}
		}
		
		for (HtmlForm form : page.getForms()) {
			try {
				List<HtmlInput> htmlInputs = getFormInputs(form);
				HtmlSubmitInput submit = getSubmitInput(form);
				for (String input : FuzzerProperties.getInputs()) {
					for (HtmlInput htmlInput : htmlInputs) {
						htmlInput.setValueAttribute(input);
					}
					
					try {
						System.out.println("Full Input Test - Submitting form for " + page.getURL());
						HtmlPage html = submit.<HtmlPage> click();
						containsImproperData(page, html);
					} catch (IOException e) {
						System.err.println("Full Input Test - Form Submission - " + e.getMessage());
					}
					
				}
			} catch(ElementNotFoundException e) {
				System.err.println("Full Input Test - Unable to find submit button for form on " + page.getURL());
			}
		}
	}
	
	private boolean testPageRandom(WebPage page) {
		boolean selectForm = false;
		if (page.getForms().size() > 0 && page.getUrlInputs().size() > 0) {
			int rand = random.nextInt(2);
			if (rand > 0) {
				selectForm = true;
			} 
		} else if (page.getForms().size() > 0) {
			selectForm = true;
		} else if (page.getUrlInputs().size() > 0) {
			// This statement unnecessary but else if required
			selectForm = false;
		} else {
			return false;
		}
		
		if (selectForm) {
			try {
				HtmlForm form = page.getForms().get(random.nextInt(page.getForms().size()));
				List<HtmlInput> inputs = getFormInputs(form);
				HtmlSubmitInput submit = getSubmitInput(form);
				
				
				
				for (HtmlInput input : inputs) {
					input.setValueAttribute(FuzzerProperties.getInputs().get(random.nextInt(FuzzerProperties.getInputs().size())));
				}
				
				HtmlPage html = submit.<HtmlPage> click();
				containsImproperData(page, html);
			} catch (ElementNotFoundException e) {
				System.err.println("Random Input Form - " + page.getURL() + " - Submit button not found!");
			} catch (IOException e) {
				System.err.println("Random Input Form - " + page.getURL() + " - " + e.getMessage());
			}
			
			
		} else {
			String base = url + page.getURL() + "?" + page.getUrlInputs().get(random.nextInt(page.getUrlInputs().size())) + "=";
			String input = FuzzerProperties.getInputs().get(random.nextInt(FuzzerProperties.getInputs().size()));
			base = base.concat(input);
			
			try {
				System.out.println("Accessing url: " + base);
				HtmlPage html = client.getPage(base);
				containsImproperData(page, html);
			} catch (FailingHttpStatusCodeException e) {
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
		}
		
		return true;
	}
	
	private List<HtmlInput> getFormInputs(HtmlForm form) {
		List<HtmlInput> inputs = new ArrayList<HtmlInput>();
		
		for (DomNode n : form.getChildren()) {
			inputs.addAll(parseDom(n));
		}
		
		return inputs;
	}
	
	private List<HtmlInput> parseDom(DomNode node) {
		List<HtmlInput> inputs = new ArrayList<HtmlInput>();
		
		for (DomNode n : node.getChildren()) {
			if( n instanceof HtmlTextInput){
				inputs.add((HtmlInput) n);
			}else if(n instanceof HtmlHiddenInput){
				inputs.add((HtmlInput) n);
			}else if(n instanceof HtmlFileInput){
				inputs.add((HtmlInput) n);
			}else if(n instanceof HtmlPasswordInput){
				inputs.add((HtmlInput) n);
			}else if(n instanceof HtmlImageInput){
				inputs.add((HtmlInput) n);
			}
			
			if (n.hasChildNodes()){
				inputs.addAll(parseDom(n));
			}
		}
		
		return inputs;
	}
	
	private HtmlSubmitInput getSubmitInput(HtmlForm form) throws ElementNotFoundException{
		for (DomNode n : form.getChildren()) {
			try {
				return getSubmitInput(n);
			} catch(ElementNotFoundException e) {}
		}
		throw new ElementNotFoundException("", "", "");
	}
	
	private HtmlSubmitInput getSubmitInput(DomNode node) throws ElementNotFoundException {
		if (node instanceof HtmlSubmitInput) {
			return (HtmlSubmitInput) node;
		} else if (node.hasChildNodes()) {
			for (DomNode n : node.getChildren()) {
				try {
					return getSubmitInput(n);
				} catch(ElementNotFoundException e) {}
			}
		}
		
		throw new ElementNotFoundException("", "", "");
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
					
					HtmlSubmitInput submit = getSubmitInput(form);
					
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
	
	private void containsImproperData(WebPage page, HtmlPage content) {
		for (String data : sensitiveData) {
			if (content.asXml().contains(data) && !page.getSensitiveDataFound().contains(data)) {
				page.getSensitiveDataFound().add(data);
			}
		}
		for (String data : sanitizedData) {
			if (content.asXml().contains(data) && !page.getSanitizedDataFound().contains(data)) {
				page.getSanitizedDataFound().add(data);
			}
		}
	}

}