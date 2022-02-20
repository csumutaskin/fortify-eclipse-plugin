package fortifyscanner.views;

import java.util.ArrayList;
import java.util.List;

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

import model.FortifyIssueDto;
import model.FortifyScanResultDto;

public class FortifyConsoleView extends ViewPart {
	private TableViewer viewer;
	private Table table;
	private FortifyScanResultDto fcr;

//	private Action action1;
//	private Action action2;
//	private Action doubleClickAction;
//	private IWorkbench workbench;

//	public FortifyConsoleView() {
//		workbench = PlatformUI.getWorkbench();
//	}
//
//	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
//		@Override
//		public String getColumnText(Object obj, int index) {
//			return getText(obj);
//		}
//
//		@Override
//		public Image getColumnImage(Object obj, int index) {
//			return getImage(obj);
//		}
//
//		@Override
//		public Image getImage(Object obj) {
//			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		}
//	}

	@Override
	public void createPartControl(Composite parent) {
//		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//
//		viewer.setContentProvider(ArrayContentProvider.getInstance());
//		viewer.setInput(new String[] { "One", "Two", "Three" });
//		viewer.setLabelProvider(new ViewLabelProvider());

		createTable(parent);
		viewer = new TableViewer(table);
		viewer.setContentProvider(new FortifyConsoleContentProvider());
		viewer.setLabelProvider(new FortifyConsoleLabelProvider());
		
		List<FortifyIssueDto> fortifyIssues = new ArrayList<>();
		FortifyIssueDto fi = new FortifyIssueDto();
		fi.setDescription("Deneme");
		fi.setId("234");
		fi.setLocation("Aydin");
		fi.setReason("resson");
		fi.setSeverity("Severe");
		fi.setType("type");
		
		FortifyIssueDto fi2 = new FortifyIssueDto();
		fi2.setDescription("Deneme2");
		fi2.setId("2342");
		fi2.setLocation("Aydin2");
		fi2.setReason("resson2");
		fi2.setSeverity("Severe2");
		fi2.setType("type2");
		
		fortifyIssues.add(fi);
		fortifyIssues.add(fi2);
		fcr = new FortifyScanResultDto();
		fcr.setIssues(fortifyIssues);
		
		viewer.setContentProvider(new FortifyConsoleContentProvider());
		viewer.setInput(fcr);
		
//		getSite().setSelectionProvider(viewer);
//		makeActions();
//		hookContextMenu();
//		hookDoubleClickAction();
//		contributeToActionBars();
	}

	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

//		final int NUMBER_COLUMNS = 4;

		table = new Table(parent, style);

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
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
			}
		});

		// SEVERITY
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Severity");
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.OWNER));
			}
		});

		// LOCATION
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Location");
		column.setWidth(200);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
			}
		});

		// Reason
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Reason");
		column.setWidth(250);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
			}
		});

		// DESCRIPTION
		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText("Description");
		column.setWidth(450);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//						tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
			}
		});
		
		// TYPE
		column = new TableColumn(table, SWT.LEFT, 5);
		column.setText("Type");
		column.setWidth(140);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//						tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
			}
		});
	}

//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				FortifyConsoleView.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
//	}
//
//	private void contributeToActionBars() {
//		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
//		fillLocalToolBar(bars.getToolBarManager());
//	}
//
//	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
//	}
//
//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
//
//	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//	}
//
//	private void makeActions() {
//		action1 = new Action() {
//			public void run() {
//				showMessage("Action 1 executed");
//			}
//		};
//		action1.setText("Action 1");
//		action1.setToolTipText("Action 1 tooltip");
//		action1.setImageDescriptor(
//				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//
//		action2 = new Action() {
//			public void run() {
//				showMessage("Action 2 executed");
//			}
//		};
//		action2.setText("Action 2");
//		action2.setToolTipText("Action 2 tooltip");
//		action2.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//		doubleClickAction = new Action() {
//			public void run() {
//				IStructuredSelection selection = viewer.getStructuredSelection();
//				Object obj = selection.getFirstElement();
//				showMessage("Double-click detected on " + obj.toString());
//			}
//		};
//	}
//
//	private void hookDoubleClickAction() {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
//	}
//
//	private void showMessage(String message) {
//		MessageDialog.openInformation(viewer.getControl().getShell(), "FortifyConsoleView", message);
//	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	class FortifyConsoleContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object fcrAsObj) {
			FortifyScanResultDto fcr = (FortifyScanResultDto)fcrAsObj;
			return fcr.getIssues().toArray();
		}
	}
	
	class FortifyConsoleLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getColumnImage(Object arg0, int arg1) {
			// TODO Auto-generated method stub
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
				case 1 :
					result = fi.getSeverity();
					break;
				case 2 :
					result = fi.getLocation();
					break;
				case 3 :
					result = fi.getReason();
					break;
				case 4 :
					result = fi.getDescription();
					break;
				case 5 :
					result = fi.getType();
					break;	
				default :
					break; 	
			}
			return result;
		}


	}
	
}
