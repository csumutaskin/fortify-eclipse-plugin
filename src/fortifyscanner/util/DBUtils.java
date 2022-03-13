package fortifyscanner.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

	public static List<FortifyIssueDto> eliminateIgnoredRulesAtUserScope(List<FortifyIssueDto> beforeElimination) {
		return eliminateIgnoredRulesFromGivenList(USER_DB_FOLDER_PATH, DB_FILE_NAME, beforeElimination);
	}

	public static List<FortifyIssueDto> eliminateIgnoredRulesAtWorkspaceScope(List<FortifyIssueDto> beforeElimination) {
		return eliminateIgnoredRulesFromGivenList(WORKSPACE_DB_FOLDER_PATH, DB_FILE_NAME, beforeElimination);
	}
		
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
				if("".equals(ignoredSubCategory) && current.getDescription() == null) {//1st match to remove current rule among all.
					removeList.add(current);	
				} else if(current.getDescription() != null && ignoredSubCategory.equals(current.getDescription().trim())) {//2nd match to remove current rule among all.
					removeList.add(current);
				}				
			}			
		}
		givenListBeforeElimination.removeAll(removeList);
		return givenListBeforeElimination;
	}
	
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
}
