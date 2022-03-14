package fortifyscanner.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import fortifyscanner.model.FortifyIssueDto;

/**
 * DBUtils contains utility methods to retrieve and do more operations on  
 * @author Umut
 *
 */
public class DBUtils {
	
	public static String USER_DB_FOLDER_PATH = System.getProperty("user.home")
			+ "/AppData/Local/Eclipse/FortifyScanner";
	public static String WORKSPACE_DB_FOLDER_PATH = Platform.getStateLocation(FrameworkUtil.getBundle(DBUtils.class))
			.toFile().getAbsolutePath();
	public static String DB_FILE_NAME = "IgnoredRulesList.db";
	private static final Logger LOGGER = Logger.getLogger(DBUtils.class.getName());

	/**
	 * Filters o.s. user wide ignore rules from a given collection 
	 * @param beforeElimination unfiltered collection.
	 * @return filtered collection.
	 */
	public static List<FortifyIssueDto> eliminateIgnoredRulesAtUserScope(List<FortifyIssueDto> beforeElimination) {
		return eliminateIgnoredRulesFromGivenList(USER_DB_FOLDER_PATH, DB_FILE_NAME, beforeElimination);
	}

	/**
	 * Filters workspace wide ignore rules from a given collection 
	 * @param beforeElimination unfiltered collection.
	 * @return filtered collection.
	 */
	public static List<FortifyIssueDto> eliminateIgnoredRulesAtWorkspaceScope(List<FortifyIssueDto> beforeElimination) {
		return eliminateIgnoredRulesFromGivenList(WORKSPACE_DB_FOLDER_PATH, DB_FILE_NAME, beforeElimination);
	}
		
	/**
	 * Returns a Collection of objects by eliminating end user wished rules (these rules are put to ignore list .db files)
	 * .db files are scanned and intersected rules by the collection to be filtered (this collection will be shown in custom views) are erased from the whole collection\
	 * and "not ignored" rules are returned after this filter.
	 * @param dbFolderPath folder path that .db file is located.
	 * @param dbFilePath file path that .db is located.
	 * @param givenListBeforeElimination unfiltered collection (will be filtered when this method runs.)
	 * @return filtered list.
	 */
	public static List<FortifyIssueDto> eliminateIgnoredRulesFromGivenList(String dbFolderPath, String dbFilePath, List<FortifyIssueDto> givenListBeforeElimination) {
		
		List<FortifyIssueDto> removeList = new ArrayList<>();
		if(givenListBeforeElimination == null) {
			return null;
		}
		List<String[]> userScopedIgnoredList = getColonSeperatedCatAndSubCatFromDB(dbFolderPath, dbFilePath);	
		Map<String, String> catSubCatMap = userScopedIgnoredList.stream().collect(Collectors.toMap((array) -> array[0], (array) -> array[1]));
		
		for(FortifyIssueDto current : givenListBeforeElimination) {
			String ignoredSubCategory = catSubCatMap.get(current.getReason().trim());
			if(ignoredSubCategory != null) {
				if("".equals(ignoredSubCategory) && (current.getDescription() == null || "".equals(current.getDescription().trim()))) {//1st match to remove current rule among all.
					removeList.add(current);	
				} else if(current.getDescription() != null && ignoredSubCategory.equals(current.getDescription().trim())) {//2nd match to remove current rule among all.
					removeList.add(current);
				}				
			}			
		}
		givenListBeforeElimination.removeAll(removeList);
		return givenListBeforeElimination;
	}
	
	/**
	 * Returns List of 2 length ignored rules string arrays (Category,sub category elements) from OS User Wide DB.
	 * @return List of ignored rules.
	 */
	public static List<String[]> getColonSeperatedCatAndSubCatFromAllWorkspacesDB() {
		return getColonSeperatedCatAndSubCatFromDB(USER_DB_FOLDER_PATH, DB_FILE_NAME);
	}
	
	/**
	 * Returns List of 2 length ignored rules string arrays (Category,sub category elements) from Workspace Wide DB.
	 * @return List of ignored rules.
	 */
	public static List<String[]> getColonSeperatedCatAndSubCatFromCurrentWorkspaceDB() {
		return getColonSeperatedCatAndSubCatFromDB(WORKSPACE_DB_FOLDER_PATH, DB_FILE_NAME);
	}
	
	/**
	 * Returns List of 2 length ignored rules string arrays (Category,sub category elements) from DB given in argument list.
	 * @param dbFolder containing folder path of the .db file for this plugin 
	 * @param dbFile .db file name
	 * @return List of ignored rules.
	 */
	public static List<String[]> getColonSeperatedCatAndSubCatFromDB(String dbFolder, String dbFile) {

		List<String[]> toReturn = new ArrayList<>();
		File file = new File(dbFolder + "/" + dbFile);
		try(FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);) {
			String line = "";
			while ((line = br.readLine()) != null) {
				if(!"".equals(line.trim())) {
					String[] catSubCat = line.split(":");
					if (catSubCat.length >= 1) {
						if (catSubCat.length == 1) {
							catSubCat = new String[] { catSubCat[0], "" };
						}
						toReturn.add(catSubCat);
					}
				}
			}
		} catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, "Can not read tuples from db located at: " + dbFolder + "/" + dbFile, ioe);
		}
		return toReturn;
	}

	/**
	 * Rollback method of what is previously put to the ignored list db file.
	 * Removes data in formation of index0:Category and index1: Subcategory from OS DB File. (detects and removes line)
	 * @param listToRemove list to remove from text file (.db file)
	 */
	public static boolean cleanOSDBData(List<String[]> listToRemove) {
		return cleanGivenDBData(USER_DB_FOLDER_PATH, DB_FILE_NAME, listToRemove);
	}
	
	/**
	 * Rollback method of what is previously put to the ignored list db file.
	 * Removes data in formation of index0:Category and index1: Subcategory from Workspace DB File. (detects and removes line)
	 * @param listToRemove list to remove from text file (.db file)
	 */
	public static boolean cleanWorkspaceDBData(List<String[]> listToRemove) {
		return cleanGivenDBData(WORKSPACE_DB_FOLDER_PATH, DB_FILE_NAME, listToRemove);				
	}	
		
	//Refreshes the txt db by rolling back the lines (erases the lines) that are checked as "activate back" by the end user.
	private static boolean cleanGivenDBData(String dbFolderPath, String dbFilePath, List<String[]> listToRemove) {	

		if(listToRemove == null) {
			return true;
		}

		File tempDbFile = new File(dbFolderPath + "/temp-" + dbFilePath);
		File dbFile = new File(dbFolderPath + "/" + dbFilePath);
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(tempDbFile));
			BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
		
			String currentLine;		
			Map<String, Boolean> writeToNewDB = new HashMap<>();
			
			while((currentLine = reader.readLine()) != null) {
			
				writeToNewDB.put(currentLine, Boolean.TRUE);
				
				String[] tupleInDB = currentLine.trim().split(":");
				for(String[] lineDataToRemove : listToRemove) {
				
					if(tupleInDB.length == 2 &&
					   tupleInDB[0].trim().equals(lineDataToRemove[0]) &&
					   tupleInDB[1].trim().equals(lineDataToRemove[1])) {
					
						writeToNewDB.put(currentLine, Boolean.FALSE);
						
					} else if(tupleInDB.length == 1 &&
							tupleInDB[0].trim().equals(lineDataToRemove[0].trim()) &&
							"".equals(lineDataToRemove[1].trim())) {
						
						writeToNewDB.put(currentLine, Boolean.FALSE);			
					}
				}
				if(writeToNewDB.get(currentLine) == Boolean.TRUE && !"".equals(currentLine.trim())) {
					writer.write(currentLine + System.getProperty("line.separator"));
					writer.flush();
				}
			}
		} catch(IOException ioe) {
			LOGGER.log(Level.SEVERE, "Can not remove back rules from db located at: " + dbFolderPath + "/" + dbFilePath, ioe);
			return false;
		}		
		if(dbFile.exists()) {
			dbFile.delete();
		}
		return tempDbFile.renameTo(dbFile);
	}
}
