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
import model.FortifyIssueDto;
import model.ProjectDto;
import util.ConsoleUtils;
import util.FortifyScanUtils;
import util.WorkspaceUtils;

public class FileReportHandler extends AbstractHandler {

	private static final Logger LOGGER = Logger.getLogger(FileReportHandler.class.getName());
	private List<ProjectDto> allWorkspaceProjects;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		triggerWizard(event);
		return null;
	}

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

	private int openChooseProjectDialog() throws IOException {
		int responseCode = -1;
		String title = "Choose a Project:";
		String explanation = " A Fortify Scan Analysis Report will be created with Project name on desktop."
				+ System.lineSeparator() + "You will be informed when this process is completed"
				+ System.lineSeparator();

		List<Entry<String, String>> projectList = new ArrayList<>();
		for (ProjectDto projectDTO : allWorkspaceProjects) {
			projectList.add(new SimpleEntry<String, String>(projectDTO.getProjectPath(), projectDTO.getProjectName()));
		}

		ProjectListDialog projectsDialog = new ProjectListDialog(new Shell(), title, explanation, projectList,
				IMessageProvider.INFORMATION);
		int returnValue = projectsDialog.open();

		switch (returnValue) {
		case Window.OK:
			System.out.println("OK: " + projectsDialog.getProjectRootPath());
			// String runFortifyScanOnPath =
			// runFortifyScanOnPath(projectsDialog.getSelectedButton());
			ConsoleUtils.printMessageToConsoleWithNameConsole("... Check desktop for the report ...");
			List<FortifyIssueDto> scanned = FortifyScanUtils.scanOnTheFly(projectsDialog.getProjectRootPath());

			// printToEclipseConsole(runFortifyScanOnPath);
			// ConsoleUtils.printMessageToConsoleWithNameConsole(runFortifyScanOnPath);
			responseCode = Window.OK;
			break;
		case Window.CANCEL:
			System.out.println("CANCEL");
			responseCode = Window.CANCEL;
			break;
		default:
			System.out.println("Unexpected..");
			break;
		}
		return responseCode;
	}

	private void openOKPressedDialog(ExecutionEvent event) {
		IWorkbenchWindow window;
		try {
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return;
		}
		MessageDialog.openInformation(window.getShell(), "On-the-fly Report",
				"FortifyScanner issues are logged to the console.");
	}

	private void command() {
		try {
			// sourceanalyzer -b 1 D:\Dev\workspaces\java\sandbox\Sample
			// sourceanalyzer -b 1 -scan -f Sample.fpr
			// ReportGenerator.bat -format pdf -f
			// C:\Users\UMUT\Desktop\Fortify-SCA-Report.pdf -source
			// D:\Dev\workspaces\reports\FortifySCAReports\Sample2.fpr -showRemoved
			// -showSuppressed -showHidden -template
			// D:\Dev\tools\Fortify\Fortify_SCA_and_Apps_20.1.1\bin\AllIssues.xml
			String[] command = { "cmd.exe", "/C", "Start", "C:/Users/Umut/Desktop/fortify.bat" };
			Runtime.getRuntime().exec(command);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
