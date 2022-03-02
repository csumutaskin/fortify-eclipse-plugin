package fortifyscanner.listener;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		ParsedLocationInfo locationInfo = parseLocationTrace(issue.getLocation());
		String classWithPackagePath = locationInfo.getClassName();
		String line = locationInfo.getLineNumber();
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
				// TODO: Check here...
			} catch (NumberFormatException e) {
				// TODO Check here...
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Check here...
				e.printStackTrace();
			}
		} else {
			// Do something if the file does not exist
		}
	}
	
	/**
	 * Parses location trace in the form of 
	 * [className(line)], [className(line),...,causeClassName(causeLine)]...
	 * @param locationTrace trace line
	 * @return ParsedLocationInfo object
	 */
	public ParsedLocationInfo parseLocationTrace(String locationTrace) {
		ParsedLocationInfo toReturn = new ParsedLocationInfo();
		String[] issueLocations = locationTrace.split(",");//To split array to string formation back into elements;
		String lastIssueLocation = issueLocations[issueLocations.length - 1];
		String[] partsOfIssueLocation = lastIssueLocation.split("\\[|\\]");		
		for(String currentPart : partsOfIssueLocation) {
			if(currentPart.contains(".java")) {
				String[] classAndLine = currentPart.replace("(","").replace(")", "").split(".java");
				toReturn.setClassName(classAndLine[0] + ".java");
				toReturn.setLineNumber(classAndLine[1]);
			}			
		}
		return toReturn;
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
	
	public class ParsedLocationInfo {
		private String className;
		private String lineNumber;
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getLineNumber() {
			return lineNumber;
		}
		public void setLineNumber(String lineNumber) {
			this.lineNumber = lineNumber;
		}		
	}
}
