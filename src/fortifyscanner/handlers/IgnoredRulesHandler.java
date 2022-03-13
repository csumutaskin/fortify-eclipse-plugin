package fortifyscanner.handlers;

import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import fortifyscanner.ui.dialog.IgnoredRulesDialog;

/**
 * Handler that is triggered to see ignored rule list by the end user workspace wide and application wide.
 * @author Umut
 *
 */
public class IgnoredRulesHandler extends AbstractHandler {
	
	private static final Logger LOGGER = Logger.getLogger(IgnoredRulesHandler.class.getName());
	
	/**
	 * Executes the handler to see the ignored list popup
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		LOGGER.info("Triggering ignored rules dialog...");
        IgnoredRulesDialog ignoredRulesDialog = new IgnoredRulesDialog(new Shell(), "Ignored Rules List");
        ignoredRulesDialog.open();
		
		return null;
	}
}
