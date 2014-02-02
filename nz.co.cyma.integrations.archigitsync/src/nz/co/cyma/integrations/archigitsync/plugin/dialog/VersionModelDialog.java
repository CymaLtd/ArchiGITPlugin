package nz.co.cyma.integrations.archigitsync.plugin.dialog;


	import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

	public class VersionModelDialog extends TitleAreaDialog {

	  private Button[] btnRepoChoice;
	  private Text txtVersionComment;
	  private Button chkPushToRemoteChoice;
	  

	  private boolean createNewRepo;
	  private String versionComment;
	  private boolean firstVersioning = false;
	  private boolean pushToRemoteOnVersion = false;

	  public VersionModelDialog(Shell parentShell, boolean firstVersioning) {
		  super(parentShell);
		  this.firstVersioning = firstVersioning;
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle("Version Information");
	    setMessage("Provide information for version of the modeel", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    if (this.firstVersioning)
	    	createRepositoryChoice(container);
	    this.createVersionComment(container);
	    createPushToRemoteChoice(container);

	    return area;
	  }

	  private void createRepositoryChoice(Composite container) {
	    Label lbtRepoChoice = new Label(container, SWT.NONE);
	    lbtRepoChoice.setText("New Repository Or Existing?");

	    GridData dataRepoId = new GridData();
	    dataRepoId.grabExcessHorizontalSpace = true;
	    dataRepoId.horizontalAlignment = GridData.FILL;
	    
//	    RowLayout buttonLayout = new RowLayout(SWT.HORIZONTAL);
//	    buttonLayout.marginLeft = 0;
//	    buttonLayout.marginRight = 0;
//	    buttonLayout.marginTop = 0;
//	    buttonLayout.marginBottom = 0;
	    
	    Group group = new Group(container, SWT.NONE);
	    group.setText("Choice");
	    group.setLayoutData(dataRepoId);
	    RowLayout layout=new RowLayout();
	    layout.pack=false;
	    group.setLayout(layout);
	    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    
	    btnRepoChoice = new Button[2];
	    
	    btnRepoChoice[0] = new Button(group, SWT.RADIO);
	    btnRepoChoice[0].setSelection(true);
	    btnRepoChoice[0].setText("Create In Existing Repository");
	    //btnRepoChoice[0].setLayoutData(dataRepoId);

	    btnRepoChoice[1] = new Button(group, SWT.RADIO);
	    btnRepoChoice[1].setSelection(false);
	    btnRepoChoice[1].setText("Create In New Repository");
	    //btnRepoChoice[1].setLayoutData(dataRepoId);
	    
	  }
	  
	  private void createVersionComment(Composite container) {
	    Label lbtComment = new Label(container, SWT.NONE);
	    lbtComment.setText("Provide A Comment To Describe What You\'ve Changed");

	    GridData dataRepoDesc = new GridData();
	    dataRepoDesc.grabExcessHorizontalSpace = true;
	    dataRepoDesc.horizontalAlignment = GridData.FILL;
	    dataRepoDesc.heightHint= 40;
	    

	    txtVersionComment = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	    txtVersionComment.setLayoutData(dataRepoDesc);
	    //txtVersionComment.set
	  }
	  
	  private void createPushToRemoteChoice(Composite container) {
		    Label lbtPushToRemoteChoice = new Label(container, SWT.NONE);
		    lbtPushToRemoteChoice.setText("Push The Change To A Remote Repository?");

		    GridData dataPush = new GridData();
		    dataPush.grabExcessHorizontalSpace = true;
		    dataPush.horizontalAlignment = GridData.FILL;
		    dataPush.heightHint= 40;
		    
		    chkPushToRemoteChoice = new Button(container, SWT.CHECK);
		    chkPushToRemoteChoice.setText("Push?");
		    
		  }



	  @Override
	  protected boolean isResizable() {
	    return true;
	  }

	  // save content of the Text fields because they get disposed
	  // as soon as the Dialog closes
	  private void saveInput() {
		if (btnRepoChoice!=null)
			this.createNewRepo = this.btnRepoChoice[1].getSelection();
	    this.versionComment = this.txtVersionComment.getText();
	    this.pushToRemoteOnVersion = this.chkPushToRemoteChoice.getSelection();
	  }

	  @Override
	  protected void okPressed() {
	    saveInput();
	    super.okPressed();
	  }

	  public boolean createNewRepository() {
	    return this.createNewRepo;
	  }
	  
	  public String getVersionComment() {
		    return this.versionComment;
		  }

	  public boolean pushToRemoteOnVersion() {
		  return this.pushToRemoteOnVersion;
	  }
}
