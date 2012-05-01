package edu.rit.se.security.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import edu.rit.se.security.fuzzer.properties.FuzzerProperties;

/**
 * Main class for running fuzzer.
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class Fuzzer {
	
	private List<WebPage> pages;
	private WebClient client;
	
	public Fuzzer() {
		//Make sure we can get the properties file first
		FuzzerProperties.discoverPages();
		pages = new LinkedList<WebPage>();
		client = new TimedWebClient();
		client.setJavaScriptEnabled(true);
		client.setPrintContentOnFailingStatusCode(false);
	}
	
	public static void main(String[] args) {
		Fuzzer fuzzer = new Fuzzer();
		//Get your fuzz on!
		fuzzer.fuzz();
	}
	
	public void fuzz() {
		//Create new client
		
		
		//Add first page to list of pages discovered
		discoverPages(FuzzerProperties.getURL());
		
		//Once all pages are discovered, discover input for each page
		discoverForms();
		
		//Now test the input possibilities
		testInput();
		
		//Output discoveries
		print();
		
		//Close out client
		client.closeAllWindows();
	}
	
	private void discoverPages(String base) {
		if (FuzzerProperties.guessPages()) {
			//Discover pages through guesses
			for (String pageUrl : FuzzerProperties.getPageGuessList()) {
				try {
					client.getPage(base + "/" + pageUrl);
					System.out.println("URL-DISCOVERY:GUESS - Valid url found " + base + "/" + pageUrl);
					breakDownUrl(base + "/" + pageUrl);
				} catch (FailingHttpStatusCodeException e) {
					//Url does not work
					System.out.println("URL-DISCOVERY:GUESS - Url not valid " + base + "/" + pageUrl);
					// TODO check error
				} catch (MalformedURLException e) {
					//Invalid url in file
					System.err.println("URL-DISCOVERY:GUESS - Invalid url in guess page file " + pageUrl);
				} catch (IOException e) {
					//Error
					System.err.println("URL-DISCOVERY:GUESS - " + e.getMessage());
				}
			}
		}
		
		if (FuzzerProperties.discoverPages()) {
			// TODO Discover pages recursively
//			for (WebPage page : pages) {
//				
//				List<HtmlAnchor> links = page.getAnchors();
//				for (HtmlAnchor link : links) {
					// TODO Break down link into parts
					// TODO Check to see if WebPage exists or if new one needs to be created
					// TODO Check to see if WebPage already is aware of url input
//				}
//			}
		}
	}
	
	private void discoverForms() {		
		// TODO
	}
	
	private void testInput() {
		if (FuzzerProperties.fullInputTest()) {
			// TODO Test everything
		} else {
			// TODO Test randomly
		}
	}
	
	private void breakDownUrl(String pageUrl) {
		try {
			URL url = new URL(pageUrl);
			WebPage page = null;
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
	}
	
	private void print() {
		System.out.println();
		System.out.println("Valid pages discovered:");
		for (WebPage page: pages) {
			System.out.println(page.getURL());
		}
	}

}
