package fortifyscanner.ui.view;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import fortifyscanner.util.ConsoleUtils;

/**
 * Internal Browser Utility for detailed vulnerability explanations for Fortify Issues.
 * Requires an internet connection to serve data.
 * 
 * @author Umut
 *
 */
public class VulncatBrowserView extends ViewPart {

	private static final Logger LOGGER = Logger.getLogger(VulncatBrowserView.class.getName());	
	public static final String VULNCAT_QUERY_URL = "https://vulncat.fortify.com/en/weakness";
	public static final String VULNCAT_CATEGORY_QUERY_PARAM = "category";
	public static final String VULNCAT_SUBCATEGORY_QUERY_PARAM = "q";
	private Browser browser = null;

	/**
	 * Create the browser in internal frame.
	 * @param frame parent frame
	 */
	@Override
	public void createPartControl(Composite frame) {
		browser = new Browser(frame, SWT.NONE);	
		browser.addProgressListener( new ProgressAdapter() {
			  @Override
			  public void completed( ProgressEvent event ) {				
			    String text = browser.getText();
			    String url = browser.getUrl();
			    if(url.endsWith("+")) {
			    	url = url.substring(0, url.length() - 1);
			    }
			    String[] split = text.split("Weaknesses");
			    if(split != null && split.length > 0) {
			    	String summary = "<h3>Detail in: \"" +  url + "\"</h3>" + split[1];
			    	browser.setText(summary);
			    	browser.setVisible(true);
			    }
			  }
			} );
	}
	
	/**
	 * Closes the browser frame.
	 */
	public void close() {
		browser.dispose();
		browser = null;
		super.dispose();
	}
	
	/**
	 * Browses the given URL.
	 * @param URL URL to be browsed
	 */
	public void openURL(String URL) {
		LOGGER.info("Internal Browser is browsing URL: " + URL);
		ConsoleUtils.printMessageToConsoleWithNameConsole("Fortify Vulncat Browser is querying URL: " + URL);
		browser.setUrl(URL);
		browser.setVisible(false);
	}
	
	/**
	 * Browses the given URL using vulncat category field.
	 * @param category category vulnerability string to be searched
	 * @param subCategory sub category vulnerability string to be searched
	 */
	public void openURLByCategory(String category, String subCategory) {		
		if(category == null) {
			return;
		}
		category = category.trim().replaceAll("\\s+", "+");
		String urlPostFix = "";
		if(subCategory != null && !"".equals(subCategory.trim())) {
			subCategory = URLEncoder.encode(subCategory, StandardCharsets.UTF_8);
			urlPostFix = "&q=" + subCategory.trim(); 
		}
		String URL = VULNCAT_QUERY_URL + "?" + VULNCAT_CATEGORY_QUERY_PARAM +"=" + category + urlPostFix;		
		openURL(URL);		
	}	
	
	/**
	 * Sets focus on frame.
	 */
	public void setFocus() {
		browser.setFocus();
	}
}