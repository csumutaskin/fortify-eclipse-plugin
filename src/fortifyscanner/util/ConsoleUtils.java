package fortifyscanner.util;

import java.io.PrintStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import fortifyscanner.ui.console.RenameableMessageConsole;

/**
 * Contains utility functions to output to Eclipse IDE's console.
 * 
 * @author Umut
 *
 */
public class ConsoleUtils {

	private static MessageConsole currentConsole;

	static {
		currentConsole = getAndFocusConsoleWithName(null);
	}

	/**
	 * Gets console with given name. if no console exists with given name, creates a
	 * new console with that name.
	 * 
	 * @param name name of the console to be retrieved among consoles.
	 * @return message console with given name.
	 */
	public static MessageConsole getConsoleWithName(String name) {

		if (name == null) {
			name = "Console";
		}
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] existingConsoles = consoleManager.getConsoles();
		if (existingConsoles != null) {
			for (int i = 0; i < existingConsoles.length; i++)
				if (name.equals(existingConsoles[i].getName()))
					return (MessageConsole) existingConsoles[i];
		}
		MessageConsole consoleToAdd = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[] { consoleToAdd });
		return consoleToAdd;
	}

	/**
	 * Returns the console with given name and sets focus to console view.
	 * 
	 * @param name name of the console (if not exists before, created by the
	 *             utility)
	 * @return message console with focused console view.
	 */
	public static MessageConsole getAndFocusConsoleWithName(String name) {
		MessageConsole currentConsole = getConsoleWithName(name);
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(currentConsole);
		return currentConsole;
	}

	/**
	 * Prints given message to the eclipse console.
	 * 
	 * @param message string that will be printed.
	 */
	public static void printMessageToConsoleWithNameConsole(String message) {
		MessageConsoleStream out = currentConsole.newMessageStream();
		PrintStream printStream = new PrintStream(out, true);
		printStream.println(message);
	}

	/**
	 * Prints given message to an eclipse console with given name by the developer
	 * (Think of it as a specialized console window)
	 * 
	 * @param nameOfTheSpecializedConsole name of the console which the message will
	 *                                    be prompted to.
	 * @param message                     string content to print out to the eclipse
	 *                                    console.
	 */
	public static void printMessageToSpecializedConsole(String nameOfTheSpecializedConsole, String message) {
		RenameableMessageConsole console = new RenameableMessageConsole("Fortify SCA On The Fly Report", null);
		console.setConsoleName(nameOfTheSpecializedConsole);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
		MessageConsoleStream stream = console.newMessageStream();
		stream.println(message);		
	}
}