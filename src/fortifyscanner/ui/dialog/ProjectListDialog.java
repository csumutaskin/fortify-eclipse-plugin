package fortifyscanner.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Pop up used to choose a project in current workspace. Project list in current
 * eclipse workspace is given as a list each preceded with a radio button, 
 * to allow end user to choose only one among all for fortify static code analysis.
 * 
 * @author Umut
 */
public class ProjectListDialog extends TitleAreaDialog {

	private String chosenProjectRootPathByEndUser;
	private String chosenProjectName;
	private String windowTitle;
	private String windowInfo;
	private int messageType;
	private List<Entry<String, String>> projectInfoList; // Project Name and Project Full Path.
	private List<Button> projectRadioButtonList;

	public ProjectListDialog(Shell parentShell, String windowTitle, String windowInfo,
			List<Entry<String, String>> projectInfoList, int messageType) {

		super(parentShell);
		this.setHelpAvailable(false);
		this.windowTitle = windowTitle;
		this.windowInfo = windowInfo;
		this.projectInfoList = projectInfoList;
		this.messageType = messageType;
	}

	/**
	 * Initializes the popup.
	 */
	@Override
	public void create() {
		super.create();
		setMessage(this.windowTitle, this.messageType);
		this.getShell().setSize(getInitialSize());
	}

	/**
	 * Creates and visualizes the pop up.
	 * @param parent parent container. 
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		Label label = new Label(container, 0);
		label.setText(this.windowInfo);
		setupRadioButtonGroup(container, projectInfoList);
		return area;
	}

	// Sets up radio button list using all project information in the workspace
	private void setupRadioButtonGroup(Composite containerForRadioButtons, List<Entry<String, String>> projects) {
		
		Label space = new Label(containerForRadioButtons, SWT.FILL);
		space.setBounds(0, 0, 1, 50);
		projectRadioButtonList = new ArrayList<>();
		int currentButtonIndex = 1;
		for (Entry<String, String> project : projects) {
			Button radioButton = new Button(containerForRadioButtons, SWT.RADIO);
			radioButton.setText(project.getValue());
			if (currentButtonIndex == 1) {
				radioButton.setSelection(true);
				currentButtonIndex++;
			}
			projectRadioButtonList.add(radioButton);
		}
	}

	/**
	 * Triggers when confirm button is pressed in current dialog.
	 */
	@Override
	protected void okPressed() {
		for (int i = 0; i < projectRadioButtonList.size(); i++) {
			if (projectRadioButtonList.get(i).getSelection()) {
				chosenProjectRootPathByEndUser = projectInfoList.get(i).getKey();
				chosenProjectName = projectInfoList.get(i).getValue();
			}
		}
		super.okPressed();
	}

	/**
	 * Returns full path of the project, stored in projectRootPath field.
	 * 
	 * @return project root's full path
	 */
	public String getChosenProjectRootPath() {
		return chosenProjectRootPathByEndUser;
	}

	/**
	 * Returns name of the project, stored in chosenProjectName field.
	 * 
	 * @return project root's full path
	 */
	public String getChosenProjectName() {
		return chosenProjectName;
	}

}