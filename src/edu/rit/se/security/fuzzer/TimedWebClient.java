package edu.rit.se.security.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * A wrapper class for the WebClient that allows for timing between requests
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class TimedWebClient extends WebClient {
	
	private static final long serialVersionUID = -4710712450215994977L;

	public TimedWebClient() {
		super();
	}
	
	public HtmlPage getPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		// TODO Timing things
		return super.getPage(url);
	}

}
