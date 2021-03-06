package fortifyscanner.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fortifyscanner.util.DBUtils;

/**
 * Pop up used to show Ignored Rules List By the end user. Note that all ignored
 * rules that end with "... for this scan" are not included to the list here,
 * because these rules are ignored only once, for the final scan. If rescan is
 * made, those rules reappear again.
 * 
 * @author Umut
 */
public class IgnoredRulesDialog extends TitleAreaDialog {

	public static final String OS_KEY = "O.S";
	public static final String WORKSPACE_KEY = "Workspace only";
	
	private String windowTitle;
	private IgnoredRulesDialog thisDialog;
	private Table table;
	//End user's all selected (very first column in this title area dialog) box indexes (row number, starting from 0) are collected here, unselected ones are removed.
	private List<Integer> endUserSelectedIndexesOnIgnoreList = new ArrayList<Integer>();
	private List<String[]> tableData = new ArrayList<>();
	
	public IgnoredRulesDialog(Shell parentShell, String windowTitle) {

		super(parentShell);
		this.setHelpAvailable(false);
		this.windowTitle = windowTitle;
		this.thisDialog = this;
	}

	/**
	 * Initializes the popup.
	 */
	@Override
	public void create() {
		super.create();
		setMessage(this.windowTitle, IMessageProvider.NONE);
		this.getShell().setSize(getInitialSize());
	}

	/**
	 * Creates the layout and content.
	 * 
	 * @param parent parent container.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		Label label = new Label(container, 0);
		label.setText("Please manually trigger SCA for your project again to see the refreshed issue list.");

		table = new Table(container, SWT.SINGLE | SWT.BORDER | SWT.NO_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column;

		// Choose
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Select");
		column.setWidth(60);

		// Category
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Category");
		column.setWidth(250);

		// Subcategory
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("SubCategory");
		column.setWidth(250);

		// Blocked user wide or workspace wide.
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Scope");
		column.setWidth(150);

		List<String[]> osWideIgnoredRules = DBUtils.getColonSeperatedCatAndSubCatFromAllWorkspacesDB();
		List<String[]> enhancedOsWideIgnoredRules = osWideIgnoredRules.stream().map(array -> new String[] {array[0], array[1], OS_KEY}).collect(Collectors.toList());
				
				
		List<String[]> workspaceWideIgnoredRules = DBUtils.getColonSeperatedCatAndSubCatFromCurrentWorkspaceDB();
		List<String[]> enhancedWorkspaceWideIgnoredRules = workspaceWideIgnoredRules.stream().map(array -> new String[] {array[0], array[1], WORKSPACE_KEY}).collect(Collectors.toList());
		
		enhancedOsWideIgnoredRules.addAll(enhancedWorkspaceWideIgnoredRules);
		tableData = enhancedOsWideIgnoredRules;
		
		fillData(table, tableData);	
		return area;
	}
	
	private void fillData(Table table, List<String[]> ignoredRulesWithLastColumnDBOriginInfo) {
		
		if(ignoredRulesWithLastColumnDBOriginInfo == null) {
			return;
		}
		
		for (int i = 0; i < ignoredRulesWithLastColumnDBOriginInfo.size(); i++) {
			new TableItem(table, SWT.NONE);
		}
			
		TableItem[] items = table.getItems();
	
		for (int i = 0; i < items.length; i++) {
	        TableEditor tableEditor = new TableEditor(table);
			
			Button button = new Button(table, SWT.CHECK);
			button.pack();
			button.addSelectionListener(new IgnoreListSelectionListener(i));
			tableEditor.minimumWidth = button.getSize().x;
			tableEditor.horizontalAlignment = SWT.LEFT;
			tableEditor.setEditor(button, items[i], 0);
			
			tableEditor = new TableEditor(table);
			Text textCategory = new Text(table, SWT.NONE);
			textCategory.setText(ignoredRulesWithLastColumnDBOriginInfo.get(i)[0]);
			tableEditor.grabHorizontal = true;
			tableEditor.setEditor(textCategory, items[i], 1);
			
			tableEditor = new TableEditor(table);
			Text textSubCategory = new Text(table, SWT.NONE);
			textSubCategory.setText(ignoredRulesWithLastColumnDBOriginInfo.get(i)[1]);
			tableEditor.grabHorizontal = true;
			tableEditor.setEditor(textSubCategory, items[i], 2);			
			
			tableEditor = new TableEditor(table);
			Text textScope = new Text(table, SWT.NONE);
			textScope.setText(ignoredRulesWithLastColumnDBOriginInfo.get(i)[2]);
			tableEditor.grabHorizontal = true;
			tableEditor.setEditor(textScope, items[i], 3);			
		}		
	}
	
	private void activateBackEndUserSelectedRules() {	
		
		Map<String, List<String[]>> rulesToRollbackMap = new HashMap<>();
		rulesToRollbackMap.put(OS_KEY, new ArrayList<>());
		rulesToRollbackMap.put(WORKSPACE_KEY, new ArrayList<>());
		
		for(int index : endUserSelectedIndexesOnIgnoreList) {
			String[] threeColumnedData = tableData.get(index);
			List<String[]> tempList = rulesToRollbackMap.get(threeColumnedData[2]);
			tempList.add(new String[]{threeColumnedData[0], threeColumnedData[1]});			
			rulesToRollbackMap.put(threeColumnedData[2], tempList);
		}
		
		DBUtils.cleanOSDBData(rulesToRollbackMap.get(OS_KEY));
		DBUtils.cleanWorkspaceDBData(rulesToRollbackMap.get(WORKSPACE_KEY));		
		MessageDialog.openInformation(thisDialog.getShell(), "System Response", "Your selected rule list is activated back and removed from ignored rule list. Please do a re-scan on the same project to include the activated back rules.");
	}
	
	@Override
    public void okPressed() {
    	activateBackEndUserSelectedRules();
        this.close();
    }

	@Override
	protected Control createButtonBar(Composite parent) {

		Composite blank = new Composite(parent, SWT.NONE);
		blank.setLayoutData(new GridData(1, 1));
		Composite buttonbar = new Composite(parent, SWT.None);
		buttonbar.setLayout(new GridLayout(3, false));
		buttonbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		super.createButton(buttonbar, IDialogConstants.OK_ID, "Activate Back Rules", false);
		Button cancelButton = super.createButton(buttonbar, 2, "Cancel", true);
		cancelButton.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		    	  thisDialog.close();
		        }
		      });
		return buttonBar;
	}
	
	/**
	 * Selection listener class for select boxes in dialog's ignore list table (very first column).
	 * 
	 * @author Umut
	 *
	 */
	class IgnoreListSelectionListener extends SelectionAdapter {

		private Integer index;
		
		public IgnoreListSelectionListener(Integer index) {
			this.index = index;
		}
		
		@Override
		public void widgetSelected(SelectionEvent selectionEvent) {
			boolean selected = ( (Button) selectionEvent.getSource() ).getSelection();
			if(selected) {
				endUserSelectedIndexesOnIgnoreList.add(Integer.valueOf(index));
			} else { //unselected
				endUserSelectedIndexesOnIgnoreList.remove(Integer.valueOf(index));
			}			
		}
	}
}