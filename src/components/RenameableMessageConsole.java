package components;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;

/**
 * An overridden Message Console that is renameable.
 * 
 * @author Umut
 *
 */
public class RenameableMessageConsole extends MessageConsole{

	public RenameableMessageConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
	}
	
	public void setConsoleName(String newName) {
		super.setName(newName);
	}
}
