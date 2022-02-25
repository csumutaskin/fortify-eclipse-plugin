package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import model.FortifyIssueDto;
import model.FortifyScanResultDto;

/** 
 * A utility that facilitates fortify scans (using command line command) and mapping results into proper objects.
 * 
 * @author Umut
 *
 */
public class FortifyScanUtils {
	
	private static final Logger LOGGER = Logger.getLogger(FortifyScanUtils.class.getName());
	public static String PROJECT_ROOT_PATH = null;
	
	/**
	 * Creates an on the fly scan (no file generation, logs of result are kept in a parseable string).
	 * @param fullProjectRootPathToScan full path in OS that points to be scanned project root path.
	 * @return scan result hold in a string
	 * @throws IOException 
	 */
	public static List<FortifyIssueDto> scanOnTheFly(String fullProjectRootPathToScan) throws IOException {
		PROJECT_ROOT_PATH = null;
		List<FortifyIssueDto> toReturn = new ArrayList<>();
		StringBuilder completeLog = new StringBuilder();
		String command = "sourceanalyzer " + fullProjectRootPathToScan + " -scan";
		LOGGER.info("SourceAnalyzer command is: " + command);
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		try {
			Process process = Runtime.getRuntime().exec(command);
			inputStreamReader = new InputStreamReader(process.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String line = "";
			FortifyLineJoinerAndParser fortifyLineJoinerAndParser = null;
			fortifyLineJoinerAndParser = toParse(null, "");		
			
			while ((line = bufferedReader.readLine()) != null) {
				
				fortifyLineJoinerAndParser = toParse(fortifyLineJoinerAndParser.fortifyIssueDto, line);
				if(fortifyLineJoinerAndParser.complete) {
					toReturn.add(fortifyLineJoinerAndParser.fortifyIssueDto);				
				}
				completeLog.append(line).append(System.lineSeparator());		    
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
		LOGGER.info(completeLog.toString());
		return toReturn;
	}
	
	public static FortifyLineJoinerAndParser toParse(FortifyIssueDto current, String line) {
		
		if(line != null) {
			line = line.trim();
		}
		if(current != null) {
			if(current.getLocation() != null) {
				current = null;	
			} else {
				current.setLocation(line);
				return new FortifyLineJoinerAndParser(current, true);
			}			
		}
		
		if(current == null && line.startsWith("[") && line.endsWith("]")) {
			String lineToBeParsed = line.substring(1, line.length() - 1);
			String[] splitted = lineToBeParsed.split(":");
			if(splitted.length < 4) { //issue line contains at least id, severity, reason and type, otherwise it is not an issue
				PROJECT_ROOT_PATH = lineToBeParsed;
				return new FortifyLineJoinerAndParser(current, false);
			}
			current = new FortifyIssueDto();
			current.setId(splitted[0]);
			current.setSeverity(splitted[1]);
			current.setReason(splitted[2]);
			current.setDescription(splitted.length == 4 ? null : splitted[3]);
			current.setType(splitted.length == 4 ? splitted[3] : splitted[4]);
		}
		return new FortifyLineJoinerAndParser(current, false);
	}
	
	/**
	 * Scans results to a file
	 */
	public static void scanToFile(String projectName, String fullProjectRootPathToScan) {
		System.out.println(System.getProperty("user.home"));
		//Creates fortify project report (fpr) file first. user friendly report will be created from this .fpr
		String commandToCreateFpr = "sourceanalyzer " + fullProjectRootPathToScan + " -scan -f " + System.getProperty("user.home") + "/Desktop/" + projectName + ".fpr";
		LOGGER.info("SourceAnalyzer command is: " + commandToCreateFpr);
		
		String templatePath = getAllIssuesTemplateResourcePath();
		String templateSuffixCommand = "".equals(templatePath) ? "" : "-template " + templatePath;
		String commandToCreatePDF = "ReportGenerator.bat -format pdf -f " + System.getProperty("user.home") + "/Desktop/"+ projectName +"-Report.pdf -source " + System.getProperty("user.home") + "/Desktop/" + projectName + ".fpr  -showRemoved -showSuppressed -showHidden  " + templateSuffixCommand;
		LOGGER.info("Report Generator command is: " + commandToCreatePDF);	
		
		try {
			Process process = Runtime.getRuntime().exec(commandToCreateFpr);
			process.waitFor();
			Process process2 = Runtime.getRuntime().exec(commandToCreatePDF);
			process2.waitFor();
			
		} catch (IOException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Exception Running PDF report generation in the background:  ", e);
			e.printStackTrace();
		} 
	}
	
	//Tries to locate all issues XML template (think of it as a resource) in plugin, return empty string if not located
	private static String getAllIssuesTemplateResourcePath() {
		
		Bundle bundle = Platform.getBundle("FortifyScanner");
		URL url = FileLocator.find(bundle, new Path("resources/AllIssues.xml"), null);
		try {
			url = FileLocator.toFileURL(url);
			return url.toString().replace("file:","");
		} catch (IOException e1) {
			LOGGER.log(Level.WARNING, "Can not retrieve AllIssues.xml template for current Plugin, so the output report will give limited information without all issue info", e1);
		}
		return "";		
	}
	
	/**
	 * Maps on the fly scan response to a FortifyScanResultDTO
	 * @param result log that holds all on the fly scan result
	 * @return mapped result in a DTO object
	 */
	public static FortifyScanResultDto mapOnTheFlyScanToDTO(String result) {
		return null;
	}
	
	/**
	 * Scan utility in cmd returns like a result as below:
	 * 
	 * --EmptyLine
	 *[D:/Dev/workspaces/java/runtime-EclipseApplication/Sample/src]
     * -- EmptyLine
     *[634D05B33BA856A694F4EF6704F24D0A : low : J2EE Bad Practices : Leftover Debug Code : structural ]
     *    Sample.java(3)
     * -- EmptyLine
     *[48911FA4B2A29DB127F4E341E2AF14F8 : low : Poor Logging Practice : Use of a System Output Stream : structural ]
     *    Sample.java(5)
     *
     * main aim of this class is by using the same reference passed as argument to the constructor to return a
     * complete set of object (all fields set to the object, and the boolean complete returns as true at that time) 
     * 
	 * @author Umut
	 *
	 */
	public static class FortifyLineJoinerAndParser {
		public FortifyIssueDto fortifyIssueDto;
		public boolean complete = false;
		public FortifyLineJoinerAndParser(FortifyIssueDto fortifyIssueDto, boolean complete) {
			this.complete = complete;
			this.fortifyIssueDto = fortifyIssueDto;
		}
	}
}
