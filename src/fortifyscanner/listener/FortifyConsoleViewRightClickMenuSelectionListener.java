package fortifyscanner.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;

import fortifyscanner.model.FortifyIssueDto;
import fortifyscanner.ui.view.FortifyConsoleView;

/**
 * Listener for right mouse click menu on Fortify On-the-Fly view.
 * 
 * @author Umut
 *
 */
public class FortifyConsoleViewRightClickMenuSelectionListener extends SelectionAdapter {
	
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
			ignoreRuleForAllProjects();
		}
	}
	
	private void ignoreRuleWithIDForThisScan() {		
		fcv.removeDataFromResult(selectedFortifyIssue);		
	}
	
	private void ignoreRuleWithCategoryForThisScan() {		
		fcv.removeDataWithCategoryAndSubCategoryFromResult(selectedFortifyIssue.getReason(), selectedFortifyIssue.getDescription());		
	}
	
	private void ignoreRuleForAllProjects() {		
	}
}
