package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import model.FortifyIssueDto;

/** 
 * A utility that facilitates fortify scans (using command line command) and mapping results into proper objects.
 * 
 * TODO: This utility will be simplified using directly the Fortify Issue DTO. Currently contains irrelevant code snippets. I am aware of that...
 * 
 * @author Umut
 *
 */
public class FortifyScanUtils {
	
	private static final Logger LOGGER = Logger.getLogger(FortifyScanUtils.class.getName());
	public static String PROJECT_ROOT_PATH = null;
	private static Map<String, IssueDetails> parsedReport = new HashMap<>();
	private static Matcher numberWrappedWithParanthesis = null;
	private static Pattern pattern = Pattern.compile("([0-9])");
		
	/**
	 * Creates an on the fly scan (no file generation, logs of result are kept in a parseable string).
	 * @param fullProjectRootPathToScan full path in OS that points to be scanned project root path.
	 * @return scan result hold in a string
	 * @throws IOException 
	 */
	public static List<FortifyIssueDto> scanOnTheFly(String fullProjectRootPathToScan) throws IOException {

		PROJECT_ROOT_PATH = null;
		StringBuilder completeLog = new StringBuilder();
		String currentIssueId = null;
		String responseLineOfCommand = "";
		
		String commandThatRunsInOS = "sourceanalyzer " + fullProjectRootPathToScan + " -scan";
		LOGGER.info("SourceAnalyzer command is: " + commandThatRunsInOS);
		
		Process process = Runtime.getRuntime().exec(commandThatRunsInOS);
		try(InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
			while ((responseLineOfCommand = bufferedReader.readLine()) != null) {
				currentIssueId = parseAndGroupReportData(currentIssueId, responseLineOfCommand);
				completeLog.append(responseLineOfCommand).append(System.lineSeparator());		    
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception Running Source Analyzer on OS Command level:  ", e);			
		}
		LOGGER.info(completeLog.toString());
		ConsoleUtils.printMessageToConsoleWithNameConsole(completeLog.toString());
		return convert(parsedReport);
	}
	
	/**
	 * Converts a Pre-filled issue detail map to fortify issue DTO list.
	 * @param toConvertMap map to be converted
	 * @return list of fortify issue DTO
	 */
	public static List<FortifyIssueDto> convert(Map<String, IssueDetails> toConvertMap) {
		List<FortifyIssueDto> toReturn = new ArrayList<>();
		if(toConvertMap == null || toConvertMap.size() ==0) {
			return toReturn;
		}
		Iterator<String> idIterator = toConvertMap.keySet().iterator();
		while(idIterator.hasNext()) {
			String id = idIterator.next();
			IssueDetails issueDetail = toConvertMap.get(id);			
			FortifyIssueDto inTurn = new FortifyIssueDto();
			inTurn.setId(id);
			inTurn.setDescription(issueDetail.getDescription());
			inTurn.setLocation(issueDetail.getLocationTrace() != null && issueDetail.getLocationTrace().get(0) != null ? issueDetail.getLocationTrace().get(0).trim() : null);
			inTurn.setLocationTrace(issueDetail.getLocationTrace());
			inTurn.setReason(issueDetail.getReason());
			inTurn.setSeverity(issueDetail.getSeverity());
			inTurn.setType(issueDetail.getType());
			toReturn.add(inTurn);
		}
		return toReturn;
	}
	
	/**
	 * maps fortify issue line to the related object attribute.
	 * @param id issue id.
	 * @param line line read from os command response.
	 * @return current issue id again.
	 */
	public static String parseAndGroupReportData(String id, String line) {
		
		if(line == null || "".equals(line.trim())) { //reset issue id on new source analyzer group
			return null;
		}
		//line = line.trim();
		numberWrappedWithParanthesis = pattern.matcher(line);
		if(line.startsWith("[") && line.endsWith("]")) {
			String lineWithoutSquareBrackets = line.substring(1, line.length() - 1);
			String[] splitted = lineWithoutSquareBrackets.split(":");
			if(splitted.length < 4) { //if line is a path at most one colon exists in the whole path...
				PROJECT_ROOT_PATH = lineWithoutSquareBrackets;			
			} else {
				IssueDetails currentIssueDetail = new IssueDetails();
				currentIssueDetail.setSeverity(splitted[1]);
				currentIssueDetail.setReason(splitted[2]);
				currentIssueDetail.setDescription(splitted.length == 4 ? null : splitted[3]);
				currentIssueDetail.setType(splitted.length == 4 ? splitted[3] : splitted[4]);
				parsedReport.put(splitted[0], currentIssueDetail);
				return splitted[0];
			}
		} else if(numberWrappedWithParanthesis.find()) { //line contains "(Number)" so it is a location trace
			IssueDetails issueDetails = parsedReport.get(id);
			if(issueDetails != null) {
				issueDetails.addLocationTrace(line + System.lineSeparator());
			}			
		}
		return id;
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
		public String locationStackTrace;
		public FortifyLineJoinerAndParser(FortifyIssueDto fortifyIssueDto, boolean complete, String locationStackTrace) {
			this.complete = complete;
			this.fortifyIssueDto = fortifyIssueDto;
			this.locationStackTrace = locationStackTrace;
		}
	}
	
	public static class IssueDetails {
		
		private String severity;
		private List<String> locationTrace = new ArrayList<String>();
		private String reason;
		private String description;
		private String type;
		public String getSeverity() {
			return severity;
		}
		public void setSeverity(String severity) {
			this.severity = severity;
		}
		public List<String> getLocationTrace() {
			return locationTrace;
		}
		public void setLocationTrace(List<String> locationTrace) {
			this.locationTrace = locationTrace;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}			
		public void addLocationTrace(String traceLine) {
			locationTrace.add(traceLine);
		}
	}
}
