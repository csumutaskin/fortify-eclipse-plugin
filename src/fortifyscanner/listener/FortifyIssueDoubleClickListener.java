package fortifyscanner.listener;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import model.FortifyIssueDto;
import util.FortifyScanUtils;

/**
 * On double click listener for fortify
 * 
 * @author Umut
 *
 */
public class FortifyIssueDoubleClickListener implements IDoubleClickListener {

	private TableViewer tableViewer;

	public FortifyIssueDoubleClickListener(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		FortifyIssueDto issue = (FortifyIssueDto) selection.getFirstElement();
		if (issue == null) {
			return;
		}
		System.out.println(issue.getDescription());
		String location = issue.getLocation();
		String classWithPackagePath = location.split("\\(")[0];
		System.out.println(classWithPackagePath);
//		String className = location.split(".")[0];

		
		String fullPath = FortifyScanUtils.PROJECT_ROOT_PATH + "/" + classWithPackagePath;
		System.out.println(fullPath);
		File fileToOpen = new File(fullPath);

		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			try {
				IDE.openEditorOnFileStore(page, fileStore);
			} catch (PartInitException e) {
				// Put your exception handler here if you wish to
			}
		} else {
			// Do something if the file does not exist
		}
	}
}
