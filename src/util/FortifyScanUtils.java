package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.FortifyScanResultDto;

/** 
 * A utility that facilitates fortify scans (using command line command) and mapping results into proper objects.
 * 
 * @author Umut
 *
 */
public class FortifyScanUtils {
	
	private static final Logger LOGGER = Logger.getLogger(FortifyScanUtils.class.getName());
	
	/**
	 * Creates an on the fly scan (no file generation, logs of result are kept in a parseable string).
	 * @param fullProjectRootPathToScan full path in OS that points to be scanned project root path.
	 * @return scan result hold in a string
	 * @throws IOException 
	 */
	public static String scanOnTheFly(String fullProjectRootPathToScan) throws IOException {
		StringBuilder resultLog = new StringBuilder();
		String command = "sourceanalyzer " + fullProjectRootPathToScan + " -scan";
		LOGGER.info("SourceAnalyzer command is: " + command);
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		try {
			Process process = Runtime.getRuntime().exec(command);
			inputStreamReader = new InputStreamReader(process.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				resultLog.append(line).append(System.lineSeparator());			    
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception Running Source Analyzer on OS Command level:  ", e);
			e.printStackTrace();
		} finally {
			if(inputStreamReader != null) {
				inputStreamReader.close();
			}
			if(bufferedReader != null) {
				bufferedReader.close();
			}
		}
		return resultLog.toString();
	}
	
	/**
	 * Scans results to a file
	 */
	public static void scanToFile() {
		
	}
	
	/**
	 * Maps on the fly scan response to a FortifyScanResultDTO
	 * @param result log that holds all on the fly scan result
	 * @return mapped result in a DTO object
	 */
	public static FortifyScanResultDto mapOnTheFlyScanToDTO(String result) {
		return null;
	}
}
