package fortifyscanner.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import fortifyscanner.views.FortifyIssueDetailView;
import model.FortifyIssueDto;
import util.FortifyScanUtils;

/**
 * On double click listener for fortify
 * 
 * @author Umut
 *
 */
public class FortifyIssueDoubleClickListener implements IDoubleClickListener {

	private static final Logger LOGGER = Logger.getLogger(FortifyIssueDoubleClickListener.class.getName());
	
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
		ParsedLocationInfo locationInfo = parseLocationTrace(issue.getLocation());
		
		String descriptionDetailInIssueDetatilViewHedaer = issue.getDescription() == null || issue.getDescription().trim().length() == 0 ? "" : " (" + issue.getDescription() + ")"; 
		updateFortifyIssueDetailView(issue.getLocationTrace(), issue.getId() + " : " + issue.getReason() + descriptionDetailInIssueDetatilViewHedaer);
		
		String classWithPackagePath = locationInfo.getClassName();
		String line = locationInfo.getLineNumber();		
		String fullPath = FortifyScanUtils.PROJECT_ROOT_PATH + "/" + classWithPackagePath;		
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
				LOGGER.log(Level.SEVERE, "An exception -PartInitException- occurred while a line is double clicked by the end user in Fortify Console View", e);				
			} catch (NumberFormatException e) {
				LOGGER.log(Level.SEVERE, "An exception -NumberFormatException- occurred while a line is double clicked by the end user in Fortify Console View", e);
			} catch (BadLocationException e) {
				LOGGER.log(Level.SEVERE, "An exception -BadLocationException- occurred while a line is double clicked by the end user in Fortify Console View", e);
			}
		} else {
			LOGGER.log(Level.SEVERE, "A file with path " + fullPath + " can not be found in file system, please check if the path is correct, otherwise FortifyIssueDoubleClickListener parseLocationTrace() method might be containing a severe bug. Until it is fixed, go to the file and line manually on your IDE");
			throw new RuntimeException(new FileNotFoundException());
		}
	}
	
//	private void updateFortifyIssueDetailView(List<FortifyIssueDto> scanned) {
//		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//		IWorkbenchPage page = workbench.getActivePage();
//		try {
//			IViewPart viewPart = page.showView("fortifyscanner.views.FortifyConsoleView");
//			FortifyConsoleView fcv = (FortifyConsoleView)viewPart;
//			FortifyScanResultDto fscanResult = new FortifyScanResultDto();			
//			fscanResult.setIssues(scanned);
//			fcv.refreshFortifyConsoleData(fscanResult);
//		} catch (PartInitException e) {
//			e.printStackTrace();
//		}
//	}
	
	private void updateFortifyIssueDetailView(List<String> locationLog, String infoHeader) {
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbench.getActivePage();
		try {
			IViewPart viewPart = page.showView("fortifyscanner.views.FortifyIssueDetailView");
			FortifyIssueDetailView fidv = (FortifyIssueDetailView)viewPart;
			fidv.refreshFortifyConsoleData(locationLog, infoHeader);
		} catch (PartInitException e) {
			LOGGER.log(Level.SEVERE, "An exception -PartInitException- occurred while opening a new Fortify Issue Detail View", e);			
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
				String[] classAndLine = currentPart.replace("(","").replace(")", "").trim().split(".java");
				toReturn.setClassName(classAndLine[0] + ".java");
				toReturn.setLineNumber(classAndLine[1]);
			}			
		}
		return toReturn;
	}

	// Taken from user dbrank0's answer @ https://stackoverflow.com/questions/12257105/how-to-open-a-new-eclipse-editor-with-a-specific-cursor-offset-position
	public void openAndNavigateToLine(IFile file, IWorkbenchPage page, Integer line) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.LINE_NUMBER, line);
		IMarker marker = null;
		try {
			marker = file.createMarker(IMarker.TEXT);
			marker.setAttributes(map);
			try {
				IDE.openEditor(page, marker);
			} catch (PartInitException pie) {
				LOGGER.log(Level.SEVERE, "An exception -PartInitException- occurred after a line is double clicked and file/line number is trying to be focused on the code editor", pie);				
			}
		} catch (CoreException ce) {
			LOGGER.log(Level.SEVERE, "An exception -CoreException- occurred after a line is double clicked and file/line number is trying to be focused on the code editor", ce);
		} finally {
			try {
				if (marker != null)
					marker.delete();
			} catch (CoreException ce) {
				LOGGER.log(Level.SEVERE, "An exception -CoreException- occurred after the marker is trying to be deleted when editor is being opened", ce);
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
