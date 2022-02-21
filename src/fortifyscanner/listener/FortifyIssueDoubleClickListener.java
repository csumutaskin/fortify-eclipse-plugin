package fortifyscanner.listener;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

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
		location = location.replace(")", "");
		String[] classAndLineInfo = location.split("\\(");
		String classWithPackagePath = classAndLineInfo[0];
		String line = classAndLineInfo[1];
		System.out.println(classWithPackagePath);
		System.out.println(line);
		String fullPath = FortifyScanUtils.PROJECT_ROOT_PATH + "/" + classWithPackagePath;
		System.out.println(fullPath);
		File fileToOpen = new File(fullPath);

		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			try {
				ITextEditor editor = (ITextEditor) IDE.openEditorOnFileStore(page, fileStore);

				IDocumentProvider provider = editor.getDocumentProvider();
				IDocument document = provider.getDocument(editor.getEditorInput());

				int lineStart = document.getLineOffset(Integer.valueOf(line) - 1);
				editor.selectAndReveal(lineStart, 0);

				page.activate(editor);

			} catch (PartInitException e) {
				// Put your exception handler here if you wish to
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Do something if the file does not exist
		}
	}

	// Taken from user dbrank0's answer @
	// https://stackoverflow.com/questions/12257105/how-to-open-a-new-eclipse-editor-with-a-specific-cursor-offset-position
	public void openAndNavigateToLine(IFile file, IWorkbenchPage page, Integer line) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.LINE_NUMBER, line);
		IMarker marker = null;
		try {
			marker = file.createMarker(IMarker.TEXT);
			marker.setAttributes(map);
			try {
				IDE.openEditor(page, marker);
			} catch (PartInitException e) {
				// complain
			}
		} catch (CoreException e1) {
			// complain
		} finally {
			try {
				if (marker != null)
					marker.delete();
			} catch (CoreException e) {
				// whatever
			}
		}
	}
}
