package fortifyscanner.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fortifyscanner.model.FortifyIssueDto;
import fortifyscanner.ui.view.FortifyConsoleView;

/**
 * Listener for right mouse click menu on Fortify On-the-Fly view.
 * 
 * @author Umut
 *
 */
public class FortifyConsoleViewRightClickMenuSelectionListener extends SelectionAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(FortifyConsoleViewRightClickMenuSelectionListener.class.getName());
	private FortifyIssueDto selectedFortifyIssue = null;
	private FortifyConsoleView fcv = null;
	
	public FortifyConsoleViewRightClickMenuSelectionListener(FortifyConsoleView fcv) {
		this.fcv = fcv;
	}
	
	/**
	 * Sets currently selected row's data from FortifyConsoleView
	 * @param selectedRow table item row selected.
	 */
	public void setSelectedFortifyIssueData(TableItem selectedRow) {
		this.selectedFortifyIssue = (FortifyIssueDto)selectedRow.getData();
	}
	
	@Override
	public void widgetSelected(SelectionEvent se) {
		MenuItem item = (MenuItem)se.widget;
		if(FortifyConsoleView.RIGHT_CLICK_MENU_ITEM_1_STR.equals(item.getText())) {
			ignoreRuleWithIDForThisScan();
		} else if(FortifyConsoleView.RIGHT_CLICK_MENU_ITEM_2_STR.equals(item.getText())) {
			ignoreRuleWithCategoryForThisScan();
		} else if(FortifyConsoleView.RIGHT_CLICK_MENU_ITEM_3_STR.equals(item.getText())) {
			ignoreRuleForThisWorkspaceProjectsAlways();
		} else if(FortifyConsoleView.RIGHT_CLICK_MENU_ITEM_4_STR.equals(item.getText())) {
			ignoreRuleForAllProjectsAlways();
		}
	}
	
	//Is triggered when end user wants to ignore selected rule line only. (Only one line) Excludes rule only for current static code analysis, when sca is triggered again ignored rule comes back. 
	private void ignoreRuleWithIDForThisScan() {		
		fcv.removeDataFromResult(selectedFortifyIssue);		
	}
	
	//Is triggered when end user wants to ignore selected category and subcategory from current scan. But when sca is triggered again, ignored rule set comes back.
	private void ignoreRuleWithCategoryForThisScan() {		
		fcv.removeDataWithCategoryAndSubCategoryFromResult(selectedFortifyIssue.getReason(), selectedFortifyIssue.getDescription());		
	}
	
	//Adds an ignored rule line to permanent db, new scas do not show the ignored rule from now on (for all projects).
	private void ignoreRuleForThisWorkspaceProjectsAlways() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		addIgnoreRuleToDbAtFolderPath(Platform.getStateLocation(bundle).toFile().getAbsolutePath());
		ignoreRuleWithCategoryForThisScan();
	}	
	
	//Adds an ignored rule line to workspace related db, new scas do not show the ignored rule from now on for that workspace.
	private void ignoreRuleForAllProjectsAlways() {		
		addIgnoreRuleToDbAtFolderPath(System.getProperty("user.home") + "/AppData/Local/Eclipse/FortifyScanner");
		ignoreRuleWithCategoryForThisScan();
	}
	
	//Adds ignored rule to the current .db file at given path.
	private boolean addIgnoreRuleToDbAtFolderPath(String folderPath) {
		File workspaceFolder = new File(folderPath);
		workspaceFolder.mkdirs();
		String workspaceFortifyIgnoredRulesFilePath = workspaceFolder.getAbsolutePath() + "/IgnoredRulesList.db" ;
		System.out.println(workspaceFortifyIgnoredRulesFilePath);
			
		File workspaceFortifyRulesFile = new File(workspaceFortifyIgnoredRulesFilePath);
		try {
			if(!workspaceFortifyRulesFile.exists()) { 
				workspaceFortifyRulesFile.createNewFile();					
			}
		} catch (IOException e) {	
			LOGGER.log(Level.SEVERE, "Can not create Fortify Rule Ignore List File (WorkspaceFortifyIgnoredRules.txt) for this workspace." , e);				
		}
		
		try (BufferedWriter output = new BufferedWriter(new FileWriter(workspaceFortifyRulesFile, true))){
			String ruleToAppend = selectedFortifyIssue.getReason().trim() + ((selectedFortifyIssue.getDescription() != null) ? (":" + selectedFortifyIssue.getDescription().trim()) : "");
			output.append(ruleToAppend).append(System.lineSeparator());
		} catch (IOException e) {	
			LOGGER.log(Level.SEVERE, "Can not open existing Fortify Rule Ignore List File (WorkspaceFortifyIgnoredRules.txt) for this workspace to append a new ignore item." , e);				
		}	
		return true;
	}
}
