package nz.co.cyma.integrations.archigitsync.plugin.dialog;


	import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

	public class NewModelDialog extends TitleAreaDialog {

		private static int BRANCH_ID = 0;
		private static int BRANCH_NAME = 1;
		private static int BRANCH_DESCRIPTION = 2;
		
	  private Text txtModelUser;
	  private Text txtModelUserEmail;
	  private Text txtBranchComment;
	  private Text txtBranchName;
	  private Combo cmbBranchList;
	  private Combo cmbImportBranchList;

	  private String modelUser;
	  private String modelUserEmail;
	  private String branchComment;
	  private String branchName;
	  private String chosenBranch;
	  private String chosenImportBranch;
	  private String [] branchList;
	  
	  private boolean existingBranchAllowed = true;
	  private boolean importMode = false;

	  public NewModelDialog(Shell parentShell, boolean existingBranchAllowed, boolean importMode) {
		  super(parentShell);
		  this.existingBranchAllowed = existingBranchAllowed;
		  this.importMode = importMode;
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle("Model User Information");
	    setMessage("Provide information about yourself for versioning", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    createModelUser(container);
	    this.createModelUserEmail(container);
	    if(this.importMode) 
	    	this.createImportFromBranchCombo(container);
	    
	    this.createBranchCombo(container);
	    if(!this.importMode) {
		    this.createBranchName(container);
		    this.createBranchComment(container);
	    }

	    return area;
	  }

	  private void createModelUser(Composite container) {
	    Label lbtModelUser = new Label(container, SWT.NONE);
	    lbtModelUser.setText("Your Name");

	    GridData dataModelUser = new GridData();
	    dataModelUser.grabExcessHorizontalSpace = true;
	    dataModelUser.horizontalAlignment = GridData.FILL;

	    txtModelUser = new Text(container, SWT.BORDER);
	    txtModelUser.setLayoutData(dataModelUser);
	  }
	  
	  private void createModelUserEmail(Composite container) {
	    Label lbtModelUserEmail = new Label(container, SWT.NONE);
	    lbtModelUserEmail.setText("Your Email Address");

	    GridData dataModelUserEmail = new GridData();
	    dataModelUserEmail.grabExcessHorizontalSpace = true;
	    dataModelUserEmail.horizontalAlignment = GridData.FILL;

	    txtModelUserEmail = new Text(container, SWT.BORDER);
	    txtModelUserEmail.setLayoutData(dataModelUserEmail);
	  }
	  
	  private void createBranchName(Composite container) {
		    Label lbtBranchName = new Label(container, SWT.NONE);
		    lbtBranchName.setText("Model Name");

		    GridData dataBranchName = new GridData();
		    dataBranchName.grabExcessHorizontalSpace = true;
		    dataBranchName.horizontalAlignment = GridData.FILL;

		    txtBranchName = new Text(container, SWT.BORDER);
		    txtBranchName.setLayoutData(dataBranchName);
		    txtBranchName.setEnabled(!importMode);
		  }
	  
	  private void createBranchComment(Composite container) {
		    Label lbtComment = new Label(container, SWT.NONE);
		    lbtComment.setText("Model Description");

		    GridData dataBranchComment = new GridData();
		    dataBranchComment.grabExcessHorizontalSpace = true;
		    dataBranchComment.horizontalAlignment = GridData.FILL;
		    dataBranchComment.heightHint= 40;
		    

		    txtBranchComment = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		    txtBranchComment.setLayoutData(dataBranchComment);
		    txtBranchComment.setEnabled(!importMode);
		    //txtVersionComment.set
		  }
	  
	  private void createBranchCombo(Composite container) {
	    Label lbtSaveToBranch = new Label(container, SWT.NONE);
	    lbtSaveToBranch.setText("Branch Name/Id To Save Model To");
	    
	    GridData dataBranch = new GridData();
	    dataBranch.grabExcessHorizontalSpace = true;
	    dataBranch.horizontalAlignment = GridData.FILL;

	    this.cmbBranchList = new Combo(container, SWT.BORDER);

	    cmbBranchList.setItems(branchList);
	    cmbBranchList.setLayoutData(dataBranch);
	    
	    cmbBranchList.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {

				if(!existingBranchAllowed) {
					for(String branch: branchList) {
						//if the entered branch matches then give an error
						if (cmbBranchList.getText().equals(branch) || cmbBranchList.getText().equals(branch.substring(branch.lastIndexOf('/')+1))) {
							MessageBox dialog = new MessageBox(getParentShell(), SWT.ICON_ERROR | SWT.OK);
							dialog.setText("Branch name error");
							dialog.setMessage("Sorry, you can't choose an existing branch for this, please use a different branch name");
							dialog.open(); 
							cmbBranchList.setText("");
						}
					}
				}
				
//MJT Can't do this at the moment
//				//update the name & comment to reflect the branch chosen
//			    if(cmbBranchList.getSelectionIndex()>=0) {
//			    	txtBranchName.setText(branchList[cmbBranchList.getSelectionIndex()][BRANCH_NAME]);
//			    	txtBranchComment.setText(branchList[cmbBranchList.getSelectionIndex()][BRANCH_DESCRIPTION]);
//			    }
			    	
				
				super.focusLost(e);
			}
	    	
	    });
	    
	    cmbBranchList.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				 if (importMode) {
				    	String importBranchName = cmbImportBranchList.getText();
				    	String userName = txtModelUser.getText();
				    	int finish = userName.indexOf(' ') == -1?userName.length(): userName.indexOf(' ');
				    	
				    	//set the name of branch that will be saved to to be the name of the import concatenated with the first name of the user by default
				    	cmbBranchList.setText(importBranchName.substring(importBranchName.lastIndexOf('/')+1) + "_" + userName.subSequence(0, finish));
				    }
				
				super.focusGained(e);
			}
	    	
	    });
	    //txtModelUserEmail = new Text(container, SWT.BORDER);
	    //txtModelUserEmail.setLayoutData(dataModelUserEmail);	    
	  }
	  
	  private void createImportFromBranchCombo(Composite container) {
		    Label lbtImportBranch = new Label(container, SWT.NONE);
		    lbtImportBranch.setText("Branch To Import Model From");
		    
		    GridData dataBranch = new GridData();
		    dataBranch.grabExcessHorizontalSpace = true;
		    dataBranch.horizontalAlignment = GridData.FILL;

		    this.cmbImportBranchList = new Combo(container, SWT.BORDER);

		    cmbImportBranchList.setItems(branchList);
		    cmbImportBranchList.setLayoutData(dataBranch);
		    
		    
		    //txtModelUserEmail = new Text(container, SWT.BORDER);
		    //txtModelUserEmail.setLayoutData(dataModelUserEmail);	    
		  }



	  @Override
	  protected boolean isResizable() {
	    return true;
	  }

	  // save content of the Text fields because they get disposed
	  // as soon as the Dialog closes
	  private void saveInput() {
	    modelUser = txtModelUser.getText();
	    modelUserEmail = txtModelUserEmail.getText();
	    this.chosenBranch = (this.cmbBranchList.getText().equals("")?"master":this.cmbBranchList.getText());
	    if(cmbImportBranchList!=null)
	    	this.chosenImportBranch = (this.cmbImportBranchList.getText().equals("")?"master":this.cmbImportBranchList.getText());
	    if(txtBranchComment!=null)
	    	this.branchComment = txtBranchComment.getText();
	    
	    if(txtBranchName!=null)
	    	this.branchName = txtBranchName.getText();
	  }

	  @Override
	  protected void okPressed() {
	    saveInput();
	    super.okPressed();
	  }

	  public String getModelUser() {
		  return this.modelUser;
	  }
	  
	  public String getModelUserEmail() {
		  return this.modelUserEmail;
	  }

	  public void setBranchList(String [] branchList) {
		  this.branchList = branchList;
	  }
	  
	  public void setBranchComment(String comment) {
		  this.branchComment = comment;
		  if(comment!=null)
			  this.txtBranchComment.setText(comment);
	  }
	  
	  public String getBranchComment() {
		  return this.branchComment;
	  }
	  
	  public void setBranchName(String name) {
		  this.branchName = name;
		  this.txtBranchName.setText(name);
	  }
	  
	  public String getBranchName() {
		  return this.branchName;
	  }
	  
	  public String getBranchToSaveTo() {
		  return this.chosenBranch;
	  }
	  
	  public String getChosenBranchToImportFrom() {
		  return chosenImportBranch;
	  }
}
