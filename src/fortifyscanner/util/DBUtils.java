package fortifyscanner.util;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import fortifyscanner.model.FortifyIssueDto;

public class DBUtils {
	
	public static String USER_DB_FOLDER_PATH = System.getProperty("user.home") + "/AppData/Local/Eclipse/FortifyScanner";
	public static String WORKSPACE_DB_FOLDER_PATH = Platform.getStateLocation(FrameworkUtil.getBundle(DBUtils.class)).toFile().getAbsolutePath();
	public static String DB_FILE_NAME = "IgnoredRulesList.db";
	
	public static List<FortifyIssueDto> eliminateIgnoredRulesAtUserScope() {
		return null;
	}
	
	public static List<FortifyIssueDto> eliminateIgnoredRulesAtWorkspaceScope() {
		return null;
	}
	
}
