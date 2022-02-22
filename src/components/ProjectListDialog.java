package components;

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
 * Popup dialog used to choose a project in current workspace.
 * Project list is given with radio button group, to allow end user 
 * to choose only one among workspace projects.
 * 
 * @author Umut
 */
public class ProjectListDialog extends TitleAreaDialog {
	 
    private String chosenProjectRootPathByEndUser;
    private String windowTitle;
    private String windowInfo;
    private int messageType;  
    private List<Entry<String, String>> projectInfoList; //Project Name and Project Full Path.
    private List<Button> projectRadioButtonList;
 
    public ProjectListDialog(Shell parentShell, String windowTitle, String windowInfo, List<Entry<String, String>> projectInfoList, int messageType)  { 
        
    	super(parentShell);
        this.setHelpAvailable(false);
         
        this.windowTitle = windowTitle;
        this.windowInfo = windowInfo;
        this.projectInfoList = projectInfoList;
        this.messageType = messageType;
        chosenProjectRootPathByEndUser = null;
    }
 
    @Override
    public void create() {
        super.create();
        setMessage(this.windowTitle, this.messageType);
        this.getShell().setSize(getInitialSize());
    }
  
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(1, false);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(layout);
        Label label = new Label(container, 0);
        label.setText(this.windowInfo); 
        setupRadioButtonGroup(container, projectInfoList);
        return area;
    }
    
    private void setupRadioButtonGroup(Composite containerForRadioButtons, List<Entry<String, String>> projects) {
        projectRadioButtonList = new ArrayList<>();
        int ButtonCount = 1;
        for (Entry<String, String> usrbutton : projects) {
             Button tmpButton = new Button(containerForRadioButtons, SWT.RADIO);
             tmpButton.setText(usrbutton.getValue());
 
             if (ButtonCount == 1) {
                 tmpButton.setSelection(true); //Make first button be auto-selected. 
                 ButtonCount++;
             }
            projectRadioButtonList.add(tmpButton);
        }
    }
 
    @Override
    protected void okPressed() {
    	 for (int i = 0; i < projectRadioButtonList.size(); i++) {
             if (projectRadioButtonList.get(i).getSelection()) {
                 chosenProjectRootPathByEndUser = projectInfoList.get(i).getKey();
             }
         }
        super.okPressed(); 
    }
    
    /** 
     * Returns full path of the project, stored in projectRootPath field.
     * @return project root's full path
     */
    public String getProjectRootPath() {
        return chosenProjectRootPathByEndUser;
    }
 
}