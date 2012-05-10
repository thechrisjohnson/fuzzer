package edu.rit.se.security.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import edu.rit.se.security.fuzzer.properties.FuzzerProperties;

/**
 * A wrapper class for the WebClient that allows for timing between requests
 * 
 * @author Christopher Johnson <cdj2981@gmail.com>
 */
public class TimedWebClient extends WebClient {
	
	private static final long serialVersionUID = -4710712450215994977L;
	private boolean requestAllowed;

	public TimedWebClient() {
		super();
		requestAllowed = true;
	}
	
	public HtmlPage getPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		// TODO Timing things
		while (!requestAllowed());
		startTimer();
		return super.getPage(url);
		
	}
	
	public synchronized boolean requestAllowed() {
		return requestAllowed;
	}
	
	public synchronized void setRequestAllowed(boolean requestAllowed) {
		this.requestAllowed = requestAllowed;
	}
	
	private void startTimer() {
		setRequestAllowed(false);
		Timer timer = new Timer(this);
		
		Thread thread = new Thread(timer);
		
		thread.start();
	}

}

class Timer implements Runnable {
	
	TimedWebClient client;
	
	public Timer(TimedWebClient client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(FuzzerProperties.getTimeGap());
		} catch (InterruptedException e) {
			System.err.println("TimedWebClient : " + e.getMessage());
		}
		client.setRequestAllowed(true);
	}
}
