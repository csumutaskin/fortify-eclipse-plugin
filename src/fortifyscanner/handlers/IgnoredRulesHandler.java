package fortifyscanner.handlers;

import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

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
		return null;
	}
}
