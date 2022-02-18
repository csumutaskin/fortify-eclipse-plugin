package fortifyscanner.handlers;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.handlers.HandlerUtil;

public class OnTheFlyHandler extends AbstractHandler implements IConsoleListener, IPatternMatchListener {

	private String pattern;
	private int matchCount = 0;
	
	public OnTheFlyHandler() {
		pattern = "\\d+";
		matchCount = 0;
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
	}	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		command();
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "FortifyScanner", this.matchCount + " matches found");
		return null;
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

	@Override
	public void consolesAdded(IConsole[] consoles) {
		for (IConsole console : consoles) {
			if (console instanceof TextConsole) {
				((TextConsole) console).addPatternMatchListener(this);
			}
		}

	}

	@Override
	public void consolesRemoved(IConsole[] consoles) {
		for(IConsole console : consoles) {
			if(console instanceof TextConsole) {
				((TextConsole) console).removePatternMatchListener(this);
			}
		}

	}

	@Override
	public void connect(TextConsole arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void matchFound(PatternMatchEvent arg0) {
		this.matchCount++;
		
	}

	@Override
	public int getCompilerFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLineQualifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPattern() {
		return pattern;
	}
}
