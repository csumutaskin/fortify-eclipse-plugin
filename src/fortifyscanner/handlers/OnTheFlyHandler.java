package fortifyscanner.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.service.log.LoggerFactory;

import components.ProjectListDialog;
import model.ProjectDTO;

public class OnTheFlyHandler extends AbstractHandler {

	public OnTheFlyHandler() {
		//ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
	}	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//command();
		List<ProjectDTO> workspaceProjects = getProjectsInWorkspace(event);
		openDialog(workspaceProjects);
		
		
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		MessageDialog.openInformation(window.getShell(), "FortifyScanner", this.matchCount + " matches found");
		return null;
	}
	
	private void openDialog(List<ProjectDTO> workspaceProjects) {
 
        String title = "Choose a Project:";
        String explanation = " Chosen project will be scanned by the Fortify SCA and issues will be logged to the console.";
        
        List<Entry<String,String>> projectList = new ArrayList<>();
        for(ProjectDTO projectDTO: workspaceProjects) {
        	projectList.add(new SimpleEntry<String,String>(projectDTO.getProjectPath(), projectDTO.getProjectName()));
        }

        ProjectListDialog projectsDialog = new ProjectListDialog(new Shell(), title, explanation, projectList, IMessageProvider.INFORMATION);
        int returnValue = projectsDialog.open();

        switch (returnValue) {
        case Window.OK:
            System.out.println("OK: " +  projectsDialog.getSelectedButton());
            String runFortifyScanOnPath = runFortifyScanOnPath(projectsDialog.getSelectedButton());
            System.out.println(runFortifyScanOnPath);
            printToEclipseConsole(runFortifyScanOnPath);
            break;
        case Window.CANCEL:
            System.out.println("CANCEL");
            break;
        default:
            System.out.println("Unexpected..");
            break;
        }
	}
	
	private String runFortifyScanOnPath(String path) {
		String resultLog = "";
		String command = "sourceanalyzer " + path + " -scan";
		System.out.println(command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null) {
			    resultLog += line;
			    resultLog += System.lineSeparator();
			}
			input.close();						
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultLog;
	}
	
	private void printToEclipseConsole(String toPrint) {
		MessageConsole console = new MessageConsole("Fortify SCA On The Fly Report", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
		MessageConsoleStream stream = console.newMessageStream();
		stream.println(toPrint);
		stream.println("...Scan completed successfully...");
//		
//		System.setOut(new PrintStream(stream));
//		System.setErr(new PrintStream(stream));
	}
	
	public List<ProjectDTO> getProjectsInWorkspace(ExecutionEvent event) throws ExecutionException {
		List<ProjectDTO> toReturn = new ArrayList<ProjectDTO>();
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();
	    String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	    for (IProject project : projects) {	    	
	    	toReturn.add(new ProjectDTO(project.getName(), workspacePath + "/" + project.getName()));
	    }	
	    return toReturn;
	}        
	
	private void command() {
		try {
						
			//sourceanalyzer -b 1 D:\Dev\workspaces\java\sandbox\Sample
			//sourceanalyzer -b 1 -scan -f Sample.fpr
			//ReportGenerator.bat -format pdf -f C:\Users\UMUT\Desktop\Fortify-SCA-Report.pdf -source D:\Dev\workspaces\reports\FortifySCAReports\Sample2.fpr -showRemoved -showSuppressed -showHidden -template D:\Dev\tools\Fortify\Fortify_SCA_and_Apps_20.1.1\bin\AllIssues.xml
			String[] command = {"cmd.exe", "/C", "Start" , "C:/Users/Umut/Desktop/fortify.bat"};
			Runtime.getRuntime().exec(command);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
