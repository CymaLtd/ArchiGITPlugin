package nz.co.cyma.integrations.archigitsync.plugin.dialog;


	import org.eclipse.jface.dialogs.IMessageProvider;
	import org.eclipse.jface.dialogs.TitleAreaDialog;
	import org.eclipse.swt.SWT;
	import org.eclipse.swt.layout.GridData;
	import org.eclipse.swt.layout.GridLayout;
	import org.eclipse.swt.widgets.Composite;
	import org.eclipse.swt.widgets.Control;
	import org.eclipse.swt.widgets.Label;
	import org.eclipse.swt.widgets.Shell;
	import org.eclipse.swt.widgets.Text;

	/**
	 * @author michael
	 * If a new GIT repository is being created this dialog prompts for an id and description
	 *
	 */
	public class NewRepositoryDialog extends TitleAreaDialog {

	  private Text txtRepositoryId;
	  private Text txtRepoDescription;

	  private String repositoryId;
	  private String repoDescription;

	  public NewRepositoryDialog(Shell parentShell) {
	    super(parentShell);
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle("New Repository Information");
	    setMessage("Provide information about your new repository", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    createRepositoryId(container);
	    this.createRepoDescription(container);

	    return area;
	  }

	  private void createRepositoryId(Composite container) {
	    Label lbtRepoId = new Label(container, SWT.NONE);
	    lbtRepoId.setText("Repository Id/Name");

	    GridData dataRepoId = new GridData();
	    dataRepoId.grabExcessHorizontalSpace = true;
	    dataRepoId.horizontalAlignment = GridData.FILL;

	    txtRepositoryId = new Text(container, SWT.BORDER);
	    txtRepositoryId.setLayoutData(dataRepoId);
	  }
	  
	  private void createRepoDescription(Composite container) {
	    Label lbtRepoDesc = new Label(container, SWT.NONE);
	    lbtRepoDesc.setText("Repository Description");

	    GridData dataRepoDesc = new GridData();
	    dataRepoDesc.grabExcessHorizontalSpace = true;
	    dataRepoDesc.horizontalAlignment = GridData.FILL;

	    txtRepoDescription = new Text(container, SWT.BORDER);
	    txtRepoDescription.setLayoutData(dataRepoDesc);
	  }



	  @Override
	  protected boolean isResizable() {
	    return true;
	  }

	  // save content of the Text fields because they get disposed
	  // as soon as the Dialog closes
	  private void saveInput() {
	    repositoryId = txtRepositoryId.getText();
	    this.repoDescription = this.txtRepoDescription.getText();
	  }

	  @Override
	  protected void okPressed() {
	    saveInput();
	    super.okPressed();
	  }

	  public String getRepositoryId() {
	    return repositoryId;
	  }
	  
	  public String getRepoDescription() {
		    return repoDescription;
		  }

}
