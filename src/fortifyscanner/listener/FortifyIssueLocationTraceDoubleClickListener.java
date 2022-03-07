package fortifyscanner.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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

import model.ParsedLocationInfo;
import util.FortifyScanUtils;

public class FortifyIssueLocationTraceDoubleClickListener implements IDoubleClickListener {

	private static final Logger LOGGER = Logger.getLogger(FortifyIssueLocationTraceDoubleClickListener.class.getName());
	
	private TableViewer tableViewer;

	public FortifyIssueLocationTraceDoubleClickListener(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		String locationLogTrace = (String) selection.getFirstElement();
		if (locationLogTrace == null) {
			return;
		}		
		ParsedLocationInfo locationInfo = parseLocationTrace(locationLogTrace);

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
				LOGGER.log(Level.SEVERE, "An exception -PartInitException- occurred while a line is double clicked by the end user in Fortify Issue Detail View", e);				
			} catch (NumberFormatException e) {
				LOGGER.log(Level.SEVERE, "An exception -NumberFormatException- occurred while a line is double clicked by the end user in Fortify Issue Detail View", e);
			} catch (BadLocationException e) {
				LOGGER.log(Level.SEVERE, "An exception -BadLocationException- occurred while a line is double clicked by the end user in Fortify Issue Detail View", e);
			}
		} else {
			LOGGER.log(Level.SEVERE, "A file with path " + fullPath + " can not be found in file system, please check if the path is correct, otherwise FortifyIssueDoubleClickListener parseLocationTrace() method might be containing a severe bug. Until it is fixed, go to the file and line manually on your IDE");
			throw new RuntimeException(new FileNotFoundException());
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
		String[] partsOfIssueLocation = locationTrace.split("\\[|\\]");		
		for(String currentPart : partsOfIssueLocation) {
			if(currentPart.contains(".java")) {
				String[] classAndLine = currentPart.replace("(","").replace(")", "").trim().split(".java");
				toReturn.setClassName(classAndLine[0] + ".java");
				toReturn.setLineNumber(classAndLine[1]);
			}			
		}
		return toReturn;
	}
}
