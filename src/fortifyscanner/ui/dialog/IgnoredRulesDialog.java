package fortifyscanner.ui.dialog;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
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

	private String windowTitle;
	private IgnoredRulesDialog thisDialog;

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

		Table table = new Table(container, SWT.SINGLE | SWT.BORDER | SWT.NO_SCROLL | SWT.V_SCROLL);
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
		List<String[]> enhancedOsWideIgnoredRules = osWideIgnoredRules.stream().map(array -> new String[] {array[0], array[1], "O.S."}).collect(Collectors.toList());
				
				
		List<String[]> workspaceWideIgnoredRules = DBUtils.getColonSeperatedCatAndSubCatFromCurrentWorkspaceDB();
		List<String[]> enhancedWorkspaceWideIgnoredRules = workspaceWideIgnoredRules.stream().map(array -> new String[] {array[0], array[1], "Workspace only"}).collect(Collectors.toList());
		
		enhancedOsWideIgnoredRules.addAll(enhancedWorkspaceWideIgnoredRules);		
		fillData(table, enhancedOsWideIgnoredRules);	
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
		
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite blank = new Composite(parent, SWT.NONE);
		blank.setLayoutData(new GridData(1, 1));
		Composite buttonbar = new Composite(parent, SWT.None);
		buttonbar.setLayout(new GridLayout(3, false));
		buttonbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		Button removeSelectedFromIgnoreListButton = super.createButton(buttonbar, 1, "Activate Back Rules", false);
		removeSelectedFromIgnoreListButton.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		    	  activateBackEndUserSelectedRules();
		        }
		      });
		
		Button cancelButton = super.createButton(buttonbar, 2, "Cancel", true);
		cancelButton.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		    	  thisDialog.close();
		        }
		      });
		return buttonBar;
	}
}