package fortifyscanner.handlers;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import components.ProjectListDialog;

public class OnTheFlyHandler extends AbstractHandler {

	public OnTheFlyHandler() {
		//ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
	}	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//command();
		
		openDialog();
		
		
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		MessageDialog.openInformation(window.getShell(), "FortifyScanner", this.matchCount + " matches found");
		return null;
	}
	
	private void openDialog() {

        List<Entry<String,String>> projectList = new ArrayList<>();

        //Project list will be extracted and put to the list...
        projectList.add(new SimpleEntry<String,String>("D:/dev/workspace/sample", "Sample"));
        projectList.add(new SimpleEntry<String,String>("D:/dev/workspace/sample2", "Sample2"));

        String title = "Choose a Project:";
        String explanation = " Chosen project will be scanned by the Fortify SCA and issues will be logged to the console.";
        ProjectListDialog myDialog = new ProjectListDialog(new Shell(), title, explanation, projectList, IMessageProvider.INFORMATION);
        int returnValue = myDialog.open();

        switch (returnValue) {
        case Window.OK:
            System.out.println("OK: " +  myDialog.getSelectedButton());
            break;
        case Window.CANCEL:
            System.out.println("CANCEL");
            break;
        default:
            System.out.println("Unexpected..");
            break;
        }
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
