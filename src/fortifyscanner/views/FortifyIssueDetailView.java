package fortifyscanner.views;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import fortifyscanner.listener.FortifyIssueDoubleClickListener;

public class FortifyIssueDetailView extends ViewPart {

	private TableViewer viewer;
	private Table table;
	private List<String> data;
	private TableColumn logColumnWithHeader;

	public void refreshFortifyConsoleData(List<String> locationLog, String infoHeader) {
		data = locationLog;
		viewer.setInput(data);
		logColumnWithHeader.setText(infoHeader);
	}

	@Override
	public void createPartControl(Composite parent) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = new Table(parent, style);
		viewer = new TableViewer(table);

		enrichTable(table, parent, viewer);

		viewer.setContentProvider(new FortifyIssueDetailContentProvider());
		viewer.setLabelProvider(new FortifyIssueDetailLabelProvider());

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

		// ID
		logColumnWithHeader = new TableColumn(table, SWT.LEFT, 0);		
		logColumnWithHeader.setResizable(false);		
		logColumnWithHeader.setWidth(3000);		
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	class FortifyIssueDetailContentProvider implements IStructuredContentProvider {

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object fcrAsObj) {
			List<String> locationLogs = (List<String>) fcrAsObj;
			return locationLogs.toArray();
		}
	}

	class FortifyIssueDetailLabelProvider implements ITableLabelProvider {

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
			return "" + el;
		}
	}	
}