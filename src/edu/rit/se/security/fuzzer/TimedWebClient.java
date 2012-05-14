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
	private Long time;

	public TimedWebClient() {
		super();
		requestAllowed = true;
		time = FuzzerProperties.getTimeGap();
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
		Timer timer = new Timer(this, time);
		
		Thread thread = new Thread(timer);
		
		thread.start();
	}

}

class Timer implements Runnable {
	
	TimedWebClient client;
	Long time;
	
	public Timer(TimedWebClient client, Long time) {
		this.client = client;
		this.time = time;
	}
	
	@Override
	public void run() {
		try {
			if (time > 0.0) {
				Thread.sleep(time);
			}
		} catch (InterruptedException e) {
			System.err.println("TimedWebClient : " + e.getMessage());
		}
		client.setRequestAllowed(true);
	}
}
