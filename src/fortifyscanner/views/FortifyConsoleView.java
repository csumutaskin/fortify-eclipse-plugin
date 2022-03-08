package fortifyscanner.views;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import fortifyscanner.listener.FortifyIssueDoubleClickListener;
import model.FortifyIssueDto;
import model.FortifyScanResultDto;

public class FortifyConsoleView extends ViewPart {

	private TableViewer viewer;
	private Table table;
	private FortifyScanResultDto data;

	public void refreshFortifyConsoleData(FortifyScanResultDto newData) {
		data = newData;
		viewer.setInput(data);
	}

	@Override
	public void createPartControl(Composite parent) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = new Table(parent, style);
		viewer = new TableViewer(table);

		enrichTable(table, parent, viewer);

		viewer.setContentProvider(new FortifyConsoleContentProvider());
		viewer.setLabelProvider(new FortifyConsoleLabelProvider());

		//viewer.setContentProvider(new FortifyConsoleContentProvider());
		viewer.setInput(data);
		viewer.addDoubleClickListener(new FortifyIssueDoubleClickListener(viewer));
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
		column.addSelectionListener(new SearchResultSortListener("Severity"));

		// LOCATION
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Location");
		column.setWidth(200);
		column.addSelectionListener(new SearchResultSortListener("Location"));

		// REASON
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Reason");
		column.setWidth(250);
		column.addSelectionListener(new SearchResultSortListener("Reason"));

		// DESCRIPTION
		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText("Description");
		column.setWidth(450);
		column.addSelectionListener(new SearchResultSortListener("Description"));

		// TYPE
		column = new TableColumn(table, SWT.LEFT, 5);
		column.setText("Type");
		column.setWidth(140);
		column.addSelectionListener(new SearchResultSortListener("Type"));
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	class FortifyConsoleContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object fcrAsObj) {
			FortifyScanResultDto fcr = (FortifyScanResultDto) fcrAsObj;
			return fcr.getIssues().toArray();
		}
	}

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

	class SearchResultSortListener extends SelectionAdapter {
		
		public static final String DESCRIPTION_KEY = "Description";
		public static final String ID_KEY = "ID";
		public static final String SEVERITY_KEY = "Severity";
		public static final String LOCATION_KEY = "Location";
		public static final String REASON_KEY = "Reason";
		public static final String TYPE_KEY = "Type";
		
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
				if(sortAscendingOrDescending.get(DESCRIPTION_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getDescription));
					sortAscendingOrDescending.put(DESCRIPTION_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getDescription, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(DESCRIPTION_KEY, 0);
				}
				break;
			case ID_KEY:
				if(sortAscendingOrDescending.get(ID_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getId));
					sortAscendingOrDescending.put(ID_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getId, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(ID_KEY, 0);
				}				
				break;
			case SEVERITY_KEY:
				if(sortAscendingOrDescending.get(SEVERITY_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getSeverity));
					sortAscendingOrDescending.put(SEVERITY_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getSeverity, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(SEVERITY_KEY, 0);
				}				
				break;
			case LOCATION_KEY:
				if(sortAscendingOrDescending.get(LOCATION_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getLocation));
					sortAscendingOrDescending.put(LOCATION_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getLocation, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(LOCATION_KEY, 0);
				}				
				break;
			case REASON_KEY:
				if(sortAscendingOrDescending.get(REASON_KEY) == 0) {
					issues.sort(Comparator.comparing(FortifyIssueDto::getReason));
					sortAscendingOrDescending.put(REASON_KEY, 1);
				} else { // ==1
					issues.sort(Comparator.comparing(FortifyIssueDto::getReason, Comparator.reverseOrder()));
					sortAscendingOrDescending.put(REASON_KEY, 0);
				}				
				break;
			case TYPE_KEY:
				if(sortAscendingOrDescending.get(TYPE_KEY) == 0) {
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
