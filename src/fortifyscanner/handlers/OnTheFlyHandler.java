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
 * Generated report can be seen on a custom view on IDE but with less info than a generated PDF report.
 * 
 * @author Umut
 *
 */
public class OnTheFlyHandler extends AbstractHandler {
	
	private static final Logger LOGGER = Logger.getLogger(OnTheFlyHandler.class.getName());
	private List<ProjectDto> allWorkspaceProjects;

	public OnTheFlyHandler() {
	}	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		triggerWizard(event);
		return null;
	}
	
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
			e.printStackTrace();
		}
	}
	
	private void openOKPressedDialog(ExecutionEvent event) {
		IWorkbenchWindow window;
		try {
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		} catch (ExecutionException e) {			
			e.printStackTrace();
			return;
		}
		MessageDialog.openInformation(window.getShell(), "On-the-fly Report", "FortifyScanner issues are logged to the console.");
	}
}
