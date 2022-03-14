package fortifyscanner.ui.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import fortifyscanner.listener.FortifyConsoleViewRightClickMenuSelectionListener;
import fortifyscanner.listener.FortifyIssueDoubleClickListener;
import fortifyscanner.model.FortifyIssueDto;
import fortifyscanner.model.FortifyScanResultDto;
import fortifyscanner.util.DBUtils;

/**
 * Custom View Class named as Fortify On the Fly. Can be opened via the menu:
 * Window -> Show View -> Fortify-On-the-Fly.
 * 
 * @author Umut
 *
 */
public class FortifyConsoleView extends ViewPart {

	public static final String RIGHT_CLICK_MENU_ITEM_1_STR = "Ignore Line just once";
	public static final String RIGHT_CLICK_MENU_ITEM_2_STR = "Ignore Category just once";
	public static final String RIGHT_CLICK_MENU_ITEM_3_STR = "Ignore Category for this Workspace Projects only";
	public static final String RIGHT_CLICK_MENU_ITEM_4_STR = "Ignore Category for every Java Project in this computer";
	
	private TableViewer viewer;
	private Table table;
	private FortifyScanResultDto data;

	/**
	 * Refreshes the data of the Custom View
	 * 
	 * @param newData new data of the view.
	 */
	public void refreshFortifyConsoleData(FortifyScanResultDto newData) {
		data = newData;
		List<FortifyIssueDto> issues = data.getIssues();
		issues = DBUtils.eliminateIgnoredRulesAtWorkspaceScope(DBUtils.eliminateIgnoredRulesAtUserScope(issues));
		data.setIssues(issues);
		viewer.setInput(data);
	}
	
	/**
	 * Removes given row from the output set and refreshes the view, does not create an ignore list so if scan is refreshed
	 * ignored item/items re appear again.
	 * @param rowToRemove row to remove
	 */
	public void removeDataFromResult(FortifyIssueDto rowToRemove) {
		FortifyScanResultDto currentResult = (FortifyScanResultDto)viewer.getInput();
		if(currentResult != null && currentResult.getIssues() != null) {
			List<FortifyIssueDto> currentIssues = currentResult.getIssues();
			currentIssues.remove(rowToRemove);			
			viewer.setInput(currentResult);
		}
	}
	
	/**
	 * Removes all rows having the given category and subcategory from the scan outputs. If scan is refreshed they
	 * reappear again.
	 * @param category category to remove
	 * @param subCategory subcategory to remove (if null, all nulls are taken into consideration)
	 */
	public void removeDataWithCategoryAndSubCategoryFromResult(String category, String subCategory) {
		FortifyScanResultDto currentResult = (FortifyScanResultDto)viewer.getInput();
		if(currentResult != null && currentResult.getIssues() != null) {
			List<FortifyIssueDto> currentIssues = currentResult.getIssues();
			List<Integer> indexesToRemove = new ArrayList<>();
			int index = -1;
			for(FortifyIssueDto current :  currentIssues) {
				index++;
				if(current.getReason().equals(category) && 
						((subCategory == null && current.getDescription() == null) || 
						  subCategory != null && subCategory.equals(current.getDescription()))) {
					indexesToRemove.add(index);
				}
			}
			indexesToRemove.sort(Comparator.reverseOrder());
			for(int indexToRemove : indexesToRemove) {
				currentIssues.remove(indexToRemove);
			}						
			viewer.setInput(currentResult);
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);
		setUpMouseRightClickListenerOnTable();
		
		viewer = new TableViewer(table);

		enrichTable(table, parent, viewer);

		viewer.setContentProvider(new FortifyConsoleContentProvider());
		viewer.setLabelProvider(new FortifyConsoleLabelProvider());

		viewer.setInput(data);
		viewer.addDoubleClickListener(new FortifyIssueDoubleClickListener(viewer));
	}

	private void setUpMouseRightClickListenerOnTable() {
		
		Menu menu = new Menu(table);
		table.setMenu(menu);
				
		MenuItem menuItem1 = new MenuItem(menu, SWT.None);
		MenuItem menuItem2 = new MenuItem(menu, SWT.None);
		MenuItem menuItem3 = new MenuItem(menu, SWT.None);
		MenuItem menuItem4 = new MenuItem(menu, SWT.None);
		menuItem1.setText(RIGHT_CLICK_MENU_ITEM_1_STR);
		menuItem2.setText(RIGHT_CLICK_MENU_ITEM_2_STR);
		menuItem3.setText(RIGHT_CLICK_MENU_ITEM_3_STR);
		menuItem4.setText(RIGHT_CLICK_MENU_ITEM_4_STR);
		FortifyConsoleViewRightClickMenuSelectionListener selectionListener = new FortifyConsoleViewRightClickMenuSelectionListener(this);
		menuItem1.addSelectionListener(selectionListener);
		menuItem2.addSelectionListener(selectionListener);
		menuItem3.addSelectionListener(selectionListener);
		menuItem4.addSelectionListener(selectionListener);
		
		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TableItem selectedItem = table.getItem(new Point(event.x,event.y));
				if (selectedItem != null) {					
					selectionListener.setSelectedFortifyIssueData(selectedItem);
					table.setMenu(menu);
				} else {
					table.setMenu(null);										
				}					
			}
		});		
	}

	private void enrichTable(Table table, Composite parent, TableViewer tableViewer) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column;

		// ID
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("ID");
		column.setWidth(150);
		column.addSelectionListener(new SearchResultSortListener("ID"));

		// SEVERITY
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Severity");
		column.setWidth(100);
		column.addSelectionListener(new SearchResultSortListener("Criticality"));

		// LOCATION
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Location");
		column.setWidth(200);
		column.addSelectionListener(new SearchResultSortListener("Path"));

		// REASON
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Reason");
		column.setWidth(250);
		column.addSelectionListener(new SearchResultSortListener("Category"));

		// DESCRIPTION
		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText("Description");
		column.setWidth(450);
		column.addSelectionListener(new SearchResultSortListener("Subcategory"));

		// TYPE
		column = new TableColumn(table, SWT.LEFT, 5);
		column.setText("Type");
		column.setWidth(140);
		column.addSelectionListener(new SearchResultSortListener("Analyzer"));
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Content provider - Content converter from a list of data into Array of data
	 * to fill the custom table.
	 * 
	 * @author Umut
	 *
	 */
	class FortifyConsoleContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object fcrAsObj) {
			FortifyScanResultDto fcr = (FortifyScanResultDto) fcrAsObj;
			return fcr.getIssues().toArray();
		}
	}

	/**
	 * Info mapper for the columns.
	 * 
	 * @author Umut
	 *
	 */
	class FortifyConsoleLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener arg0) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
		}

		@Override
		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}

		@Override
		public String getColumnText(Object el, int index) {
			String result = "";
			FortifyIssueDto fi = (FortifyIssueDto) el;
			switch (index) {
			case 0:
				result = fi.getId();
				break;
			case 1:
				result = fi.getSeverity();
				break;
			case 2:
				result = fi.getLocation();
				break;
			case 3:
				result = fi.getReason();
				break;
			case 4:
				result = fi.getDescription();
				break;
			case 5:
				result = fi.getType();
				break;
			default:
				break;
			}
			return result;
		}
	}

	/**
	 * Sorter Utility for each data attribute, triggered when the Column Headers is
	 * clicked.
	 * 
	 * @author Umut
	 *
	 */
	class SearchResultSortListener extends SelectionAdapter {

		// I changed the column label names to more known expressions by fortify users.
		public static final String DESCRIPTION_KEY = "Subcategory";
		public static final String ID_KEY = "ID";
		public static final String SEVERITY_KEY = "Criticality";
		public static final String LOCATION_KEY = "Path";
		public static final String REASON_KEY = "Category";
		public static final String TYPE_KEY = "Analyzer";

		private Map<String, Integer> sortAscendingOrDescending = new HashMap<>();

		private String sortType;

		public SearchResultSortListener(String sortType) {
			this.sortType = sortType;
			sortAscendingOrDescending.put(DESCRIPTION_KEY, 0);
			sortAscendingOrDescending.put(ID_KEY, 0);
			sortAscendingOrDescending.put(SEVERITY_KEY, 0);
			sortAscendingOrDescending.put(LOCATION_KEY, 0);
			sortAscendingOrDescending.put(REASON_KEY, 0);
			sortAscendingOrDescending.put(TYPE_KEY, 0);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {

			List<FortifyIssueDto> issues = data.getIssues();
			switch (sortType) {
			case DESCRIPTION_KEY:
				if (sortAscendingOrDescending.get(DESCRIPTION_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getDescription));
					sortAscendingOrDescending.put(DESCRIPTION_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getDescription, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(DESCRIPTION_KEY, 0);
				}
				break;
			case ID_KEY:
				if (sortAscendingOrDescending.get(ID_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getId));
					sortAscendingOrDescending.put(ID_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getId, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(ID_KEY, 0);
				}
				break;
			case SEVERITY_KEY:
				if (sortAscendingOrDescending.get(SEVERITY_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getSeverity));
					sortAscendingOrDescending.put(SEVERITY_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getSeverity, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(SEVERITY_KEY, 0);
				}
				break;
			case LOCATION_KEY:
				if (sortAscendingOrDescending.get(LOCATION_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getLocation));
					sortAscendingOrDescending.put(LOCATION_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getLocation, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(LOCATION_KEY, 0);
				}
				break;
			case REASON_KEY:
				if (sortAscendingOrDescending.get(REASON_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getReason));
					sortAscendingOrDescending.put(REASON_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getReason, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(REASON_KEY, 0);
				}
				break;
			case TYPE_KEY:
				if (sortAscendingOrDescending.get(TYPE_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getType));
					sortAscendingOrDescending.put(TYPE_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getType, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(TYPE_KEY, 0);
				}
				break;
			}
			viewer.setInput(data);
		}
	}
}
