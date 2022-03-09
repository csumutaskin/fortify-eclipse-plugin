package fortifyscanner.handlers;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import components.ProjectListDialog;
import fortifyscanner.views.FortifyConsoleView;
import model.FortifyIssueDto;
import model.FortifyScanResultDto;
import model.ProjectDto;
import util.ConsoleUtils;
import util.FortifyScanUtils;
import util.WorkspaceUtils;

/**
 * Handler that is triggered if end user wishes the Fortify report on Eclipse IDE itself.
 * Generated report can be seen on 2 custom views (Fortify on the fly and Fortify Issue Trace) on IDE 
 * and suggestions to fix the issue is given in an Internal Fortify Taxonomy Browser with details.
 * Internet Connection is required for the browser to serve the recommendation for the given issue.
 * 
 * Use the custom views to see details: Window -> Show View:
 * 
 * Fortify On-the-Fly: Issue table for the detected problems
 * Fortify Issue Trace: If a single issue is double clicked log trace is of the issue is given here.
 * Fortify Taxonomy: Contains abstract and recommendations (details) about the current issue investigated.
 * 
 * @author Umut
 *
 */
public class OnTheFlyHandler extends AbstractHandler {
	
	private static final Logger LOGGER = Logger.getLogger(OnTheFlyHandler.class.getName());
	private List<ProjectDto> allWorkspaceProjects;

	/**
	 * Executes an IDE based report for Fortify SCA issues on custom views.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		triggerWizard(event);
		return null;
	}
	
	// Wizard that creates the on the fly report.
	private void triggerWizard(ExecutionEvent event) {
		try {
			allWorkspaceProjects = WorkspaceUtils.getAllProjectSummaryInfoInCurrentWorkspace();
			int responseCode = openChooseProjectDialog();
			if(responseCode == Window.OK) {// User hit ok button
				openOKPressedDialog(event);
			} else if(responseCode == Window.CANCEL) {
				LOGGER.info("User cancelled the Fortify Scan on his/her will...");
			}			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unexpected exception during plugin's wizard execution: ", e);
		}
	}
	
	private int openChooseProjectDialog() throws IOException {
		int responseCode = -1; 
        String title = "Choose a Project:";
        String explanation = "Issues after scan will be listed on Fortify on the fly View"
        		+ System.lineSeparator() + "You can open view on Window -> Show View Menu";
        
        List<Entry<String,String>> projectList = new ArrayList<>();
        for(ProjectDto projectDTO: allWorkspaceProjects) {
        	projectList.add(new SimpleEntry<String,String>(projectDTO.getProjectPath(), projectDTO.getProjectName()));
        }

        ProjectListDialog projectsDialog = new ProjectListDialog(new Shell(), title, explanation, projectList, IMessageProvider.INFORMATION);
        int returnValue = projectsDialog.open();

        switch (returnValue) {
        case Window.OK:
            LOGGER.info("User has chosen: " +  projectsDialog.getChosenProjectRootPath());
            ConsoleUtils.printMessageToConsoleWithNameConsole("... Check Fortify On-the-Fly Console for detected issues ...");
            List<FortifyIssueDto> scanned = FortifyScanUtils.scanOnTheFly(projectsDialog.getChosenProjectRootPath());
            updateFortifyConsoleView(scanned);
            responseCode = Window.OK;
            break;
        case Window.CANCEL:
        	LOGGER.info("User has CANCELed choosing a project.");
            responseCode = Window.CANCEL;
            break;
        default:
        	LOGGER.info("Unexpected dialog operation..");
            break;
        }
        return responseCode;
	}	
	
	// Updates the custom Fortify On the Fly view using the issues that are detected.
	private void updateFortifyConsoleView(List<FortifyIssueDto> scanned) {
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbench.getActivePage();
		try {
			IViewPart viewPart = page.showView("fortifyscanner.views.FortifyConsoleView");
			FortifyConsoleView fcv = (FortifyConsoleView)viewPart;
			FortifyScanResultDto fscanResult = new FortifyScanResultDto();			
			fscanResult.setIssues(scanned);
			fcv.refreshFortifyConsoleData(fscanResult);
		} catch (PartInitException e) {
        	LOGGER.log(Level.SEVERE, "A problem occurred while Fortify On the Fly console data is being updated and shown to the end user.", e);
			e.printStackTrace();
		}
	}
	
	// Information message dialog for the user to inform issues are logged to the Fortify on the fly console.
	private void openOKPressedDialog(ExecutionEvent event) {
		IWorkbenchWindow window;
		try {
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		} catch (ExecutionException e) {			
			LOGGER.log(Level.SEVERE, "A problem occurred while informing end user about the process has already finished", e);
			return;
		}
		MessageDialog.openInformation(window.getShell(), "On-the-fly Report", "FortifyScanner issues are logged to the console.");
	}
}
