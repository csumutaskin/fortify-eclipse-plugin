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

public class ProjectListDialog extends TitleAreaDialog {
	 
    private String selectedButton;
    private String title, bodyMsg; 
    private int msgType; //IMessageProvider  
    private List<Entry<String, String>> userButtonList; // ButtonID , Button Label
    private List<Button> widgetButtonList;
 
    /** 
     * <h1> Construct dialogue </h1>
     * <p> Specify paramaters, then use open. </p>
     * 
     * <p> Please see <a href="https://wiki.eclipse.org/Eclipse_Plug-in_Development_FAQ/TitleAreaDialogWithRadioButtons""> wiki page </a>
     * for additional details & example.</p>
     * 
     * @param parentShell    - Parent Shell 
     * @param title          - Title of the dialogue. 
     * @param bodyMsg        - Body message of the dialogue. 
     * @param userButtonList - A list of SimpleEntry<String,String> mapping ButtonIDs and their visable text. 
     * (see <a href="https://wiki.eclipse.org/Eclipse_Plug-in_Development_FAQ/TitleAreaDialogWithRadioButtons#Example_usage"> wiki example </a> for details)
     * @param msgType        - 'IMessageProvider.INFORMATION '  Can be one of: NONE ERROR INFORMATION WARNING
     */
    public ProjectListDialog(
            Shell parentShell, String title, String bodyMsg, 
            List<Entry<String, String>> userButtonList,
            int msgType)  { //for type see: IMessageProvider
 
        super(parentShell);
 
        // Set the Buttons that will be used listed.
        this.userButtonList = userButtonList;
 
        //Set labels. 
        this.title = title;
        this.bodyMsg = bodyMsg;
 
        //set type
        this.msgType = msgType;
 
        // avoid help button poping up.
        this.setHelpAvailable(false);
 
        selectedButton = null;
    }
 
    /** Dialogue constructor */
    @Override
    public void create() {
 
        super.create();
 
        //The 'Message' of a TitleArea dialogue only spans 1-2 lines. Then text is cut off. 
        //It is not very efficient for longer messages. 
        //Thus we utilize it as a 'title' and instaed we appeng a label to act as body. (see below). 
        setMessage(this.title, this.msgType); //$NON-NLS-1$
        //setTitle(); //not used.
 
        //Set the size of the dialogue. 
        //We avoid hard-coding size, instead we tell it to figure out the most optimal size.
        //this.getShell().setSize(650, 550); //Hard-Coded = bad.
        this.getShell().setSize(getInitialSize());
    }
 
    /** Return the buttonID of the button that the user selected if he pressed ok. 
     * 
     * @return ButtonID of selected button.
     */
    public String getSelectedButton() {
        return selectedButton;
    }
 
 
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(1, false);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(layout);
 
        //Append a label to act as message body
        Label label = new Label(container, 0);
        label.setText(this.bodyMsg); 
 
        //----- Add Radio buttons to dialogue.
        widgetButtonList = new ArrayList<>();
        int ButtonCount = 1;
        for (Entry<String, String> usrbutton : userButtonList) {
             Button tmpButton = new Button(container, SWT.RADIO);
             tmpButton.setText(usrbutton.getValue());
 
             if (ButtonCount == 1) {
                 tmpButton.setSelection(true); //Make first button be auto-selected. 
                 ButtonCount++;
             }
            widgetButtonList.add(tmpButton);
        }
        return area;
    }
 
    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    protected void saveInput() {
 
        //Figure out which button was selected and set 'selectedButton' to it's key. 
        for (int i = 0; i < widgetButtonList.size(); i++) {
            if (widgetButtonList.get(i).getSelection()) {
                selectedButton = userButtonList.get(i).getKey();
            }
        }
    }
 
    /** Called when the ok button is pressed */
    @Override
    protected void okPressed() {
        saveInput(); // save input.
        super.okPressed(); // close dialogue
    }
 
}