package nz.co.cyma.integrations.archigitsync.plugin.dialog;


	import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

	/**
	 * @author michael
	 * Dialog that prompts user for information about a remote repository - used by both the export when using and
	 * existing repository and by the import.
	 *
	 */
	public class CloneRepositoryDialog extends TitleAreaDialog {

	  private Text txtRepositoryToClone;
	  private Text txtWorkingDirectory;
	  private Text txtRepoUser;
	  private Text txtRepoPassword;

	  private String repositoryToClone;
	  private String workingDirectory;
	  private String repoUser;
	  private String repoPassword;

	  public CloneRepositoryDialog(Shell parentShell) {
	    super(parentShell);
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle("Remote Repository Information");
	    setMessage("Provide information about the remote repository you are pulling from", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    createRepositoryToClone(container);
	    //createWorkingDirectory(container);
	    this.createRepoUser(container);
	    this.createRepoPassword(container);

	    return area;
	  }

	  private void createRepositoryToClone(Composite container) {
	    Label lbtRepoToClone = new Label(container, SWT.NONE);
	    lbtRepoToClone.setText("Repository To Clone Location");

	    GridData dataRepoToClone = new GridData();
	    dataRepoToClone.grabExcessHorizontalSpace = true;
	    dataRepoToClone.horizontalAlignment = GridData.FILL;

	    txtRepositoryToClone = new Text(container, SWT.BORDER);
	    txtRepositoryToClone.setLayoutData(dataRepoToClone);
	    
	    txtRepositoryToClone.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if (!txtRepositoryToClone.getText().toLowerCase().startsWith("http")) {
					txtRepoUser.setEnabled(false);
					txtRepoPassword.setEnabled(false);
				}
				else {
					txtRepoUser.setEnabled(true);
					txtRepoPassword.setEnabled(true);
				}
				
				super.focusLost(e);
			}
	    	
	    });
	  }
	  
	  private void createWorkingDirectory(Composite container) {
	    Label lbtWorkingDir = new Label(container, SWT.NONE);
	    lbtWorkingDir.setText("Working Directory For Clone Repository");

	    GridData dataWorkingDir = new GridData();
	    dataWorkingDir.grabExcessHorizontalSpace = true;
	    dataWorkingDir.horizontalAlignment = GridData.FILL;

	    txtWorkingDirectory = new Text(container, SWT.BORDER);
	    txtWorkingDirectory.setLayoutData(dataWorkingDir);
	  }

	  private void createRepoUser(Composite container) {
	    Label lbtRepoUser = new Label(container, SWT.NONE);
	    lbtRepoUser.setText("Remote Repository User Name");

	    GridData dataRepoUser = new GridData();
	    dataRepoUser.grabExcessHorizontalSpace = true;
	    dataRepoUser.horizontalAlignment = GridData.FILL;

	    txtRepoUser = new Text(container, SWT.BORDER);
	    txtRepoUser.setLayoutData(dataRepoUser);
	    
	  }
	  
	  private void createRepoPassword(Composite container) {
		    Label lbtPassword = new Label(container, SWT.NONE);
		    lbtPassword.setText("Remote Repository User Password");

		    GridData dataPassword = new GridData();
		    dataPassword.grabExcessHorizontalSpace = true;
		    dataPassword.horizontalAlignment = GridData.FILL;

		    txtRepoPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		    txtRepoPassword.setLayoutData(dataPassword);
		  }


	  @Override
	  protected boolean isResizable() {
	    return true;
	  }

	  // save content of the Text fields because they get disposed
	  // as soon as the Dialog closes
	  private void saveInput() {
		  repositoryToClone = txtRepositoryToClone.getText();
		  //workingDirectory = txtWorkingDirectory.getText();
		  repoUser = (txtRepoUser.getText().equals("")?null:txtRepoUser.getText());
		  repoPassword = (txtRepoPassword.getText().equals("")?null:txtRepoPassword.getText());
	  }

	  @Override
	  protected void okPressed() {
	    saveInput();
	    super.okPressed();
	  }

	  public String getRepositoryToClone() {
	    return repositoryToClone;
	  }
	  
	  public String getWorkingDirectory() {
		    return workingDirectory;
		  }

	  
	  public String getRepoUser() {
		  return repoUser;
	  }
	  
	  public String getRepoPassword() {
		  return repoPassword;
	  }
}
