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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import components.ProjectListDialog;
import model.ProjectDto;
import util.ConsoleUtils;
import util.FortifyScanUtils;
import util.WorkspaceUtils;

/**
 * Handler that is triggered if end user wishes the Fortify report as a separate PDF file.
 * Generated report is created on user desktop.
 * 
 * @author Umut
 *
 */
public class FileReportHandler extends AbstractHandler {

	private static final Logger LOGGER = Logger.getLogger(FileReportHandler.class.getName());
	private List<ProjectDto> allWorkspaceProjects;

	/**
	 * Triggers the process to create the .fpr file than the human readable .pdf file from
	 * this .fpr (Uses System variable: user.home / Desktop) path to set the base folder
	 * to create these reports. (Tested for Windows 10 operating system. Not tested for Linux or MacOS).
	 * @param event execution event.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		triggerWizard(event);
		return null;
	}

	// Report Creation flow. 
	private void triggerWizard(ExecutionEvent event) {
		try {
			allWorkspaceProjects = WorkspaceUtils.getAllProjectSummaryInfoInCurrentWorkspace();
			int responseCode = openChooseProjectDialog();
			if (responseCode == Window.OK) {
				openOKPressedDialog(event);
			} else if (responseCode == Window.CANCEL) {
				LOGGER.info("User cancelled the Fortify Scan on his/her will...");
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unexpected exception during plugin's wizard execution: ", e);
		}
	}

	// Creates the Project List of Workspace dialog (pop up) and waits for the end user's answer. For Windows OSs make sure the Fortify/bin folder is set in PATH. 
	private int openChooseProjectDialog() throws IOException {
		int responseCode = -1;
		String title = "Choose a Project:";
		String explanation = "Report will be created on Desktop."
				+ System.lineSeparator() + "Be sure to set Fortify->/bin folder on O.S. Path";
		List<Entry<String, String>> projectList = new ArrayList<>();
		for (ProjectDto projectDTO : allWorkspaceProjects) {
			projectList.add(new SimpleEntry<String, String>(projectDTO.getProjectPath(), projectDTO.getProjectName()));
		}

		ProjectListDialog projectsDialog = new ProjectListDialog(new Shell(), title, explanation, projectList,
				IMessageProvider.INFORMATION);
		int returnValue = projectsDialog.open();

		switch (returnValue) {
		case Window.OK:
			LOGGER.info("User has chosen project with root path: " + projectsDialog.getChosenProjectRootPath());
			ConsoleUtils.printMessageToConsoleWithNameConsole("... Check Desktop for the report ...");
			FortifyScanUtils.scanToFile(projectsDialog.getChosenProjectName() , projectsDialog.getChosenProjectRootPath());
			responseCode = Window.OK;
			break;
		case Window.CANCEL:
			LOGGER.info("CANCEL button pressed on project choice dialog by the end user");
			responseCode = Window.CANCEL;
			break;
		default:
			LOGGER.info("Unexpected thing happened with the project dialog..");
			break;
		}
		return responseCode;
	}

	//An informative message dialog after the report is succesfully created on user Desktop.
	private void openOKPressedDialog(ExecutionEvent event) {
		IWorkbenchWindow window;
		try {
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		} catch (ExecutionException e) {
			LOGGER.log(Level.SEVERE ,"Unexpected thing happened with the project dialog..", e);			
			return;
		}
		MessageDialog.openInformation(window.getShell(), "Report Generated", "Please check your desktop for the generated report.");
	}
}
