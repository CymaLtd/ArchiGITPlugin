package nz.co.cyma.integrations.archigitsync.plugin.modelexport;

import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.co.cyma.integrations.archigitsync.model.IVersionModel;
import nz.co.cyma.integrations.archigitsync.model.IVersionModelPropertyConstants;
import nz.co.cyma.integrations.archigitsync.plugin.VersioningException;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.RemoteRepositoryDialog;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.NewModelDialog;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.NewRepositoryDialog;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.VersionModelDialog;
import nz.co.cyma.integrations.archigitsync.plugin.git.GitWrapper;
import nz.co.cyma.integrations.archigitsync.plugin.yaml.YamlReader;
import nz.co.cyma.integrations.archigitsync.plugin.yaml.YamlWriter;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.archimatetool.editor.model.IModelImporter;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.model.IModelExporter;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;

/**
 * @author michael
 * This is the class called from the menu in Archi and controls the process of exporting the
 * model out into files and then commiting those files to GIT.
 *
 */
public class ModelVersioner implements IModelExporter {
	
	GitWrapper gitRepo = null;
	IVersionModel versionModel = null;
	boolean createNewRepository = false;
	private URI repoToClone = null;
	private String repoUser = null;
	private String repoPassword = null;
	private String versionComment = "";
	private boolean pushToRemoteOnVersion = false;
	
    public ModelVersioner() {
    }
	
	@Override
    public void export(IArchimateModel model) throws IOException {

		
		try {
			//first prepare the model to be versioned
			ModelPreparer modelPreparer = new ModelPreparer();
			versionModel = modelPreparer.generateVersionModel(model);
			
			//next get any comment on the versioning, and if the model is new ask how the user wants to handle it
			this.askBasicVersionInfo();
			
			//next we need to set things up if the model hasn't been versioned before, if it hasn't then we should just be able to go
			//straight to versioning it in the chosen working directory
			if(versionModel.getRepositoryId()==null) {
				//always start with where the working directory will be
				File workingDir = setupWorkingDirectory();
				
				//if it's the first time versioning, need to know if we are creating a brand new repository or adding to an existing one
				if(createNewRepository)
					versionToNewRepository(workingDir);
				else
					versionToExistingRepository(workingDir);
			}
			else {
				//if the repository id is not null then the rest of the model properties should allow us to version the model without further prompting
				versionExistingVersionedModel();
			}
			
			//lastly, if the user chose to push the versioned model to a remote repository, we need to confirm the information for that repository
			if(this.pushToRemoteOnVersion) {
				pushModelToRemoteRepository();
			}
			
			//make sure we save the model after all this so that the properties are kept
			//TODO doesn't work because the model doesn't think it is dirty, need to fix...
			//IEditorModelManager.INSTANCE.saveModelAs(model);

		} catch (VersioningException e) {
			MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Versioning Issue");
			dialog.setMessage(e.getMessage());		
			dialog.open();
			e.printStackTrace();
		}

    }
	
	private void pushModelToRemoteRepository() throws VersioningException {
		URI remoteRepoLocation = null;
		
		
    	//then the remote info
    	RemoteRepositoryDialog dialog = new RemoteRepositoryDialog(Display.getCurrent().getActiveShell());
    	dialog.setRemoteRepository(versionModel.getRemoteRepoLocation());
    	dialog.setRemoteUser(versionModel.getRemoteUser());
    	dialog.create();
    	dialog.open();
    	
    	try {
    		remoteRepoLocation = new URI(dialog.getRepositoryToClone());
		} catch (URISyntaxException e) {
			MessageBox errorDialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			errorDialog.setText("Remote repository error");
			errorDialog.setMessage("Sorry, the URI for the remote repository does not appear to be valid.");

			dialog.open(); 

			e.printStackTrace();
		}
    	
    	//set the values
    	String remoteRepoUser = dialog.getRepoUser();
    	String remoteRepoPassword = dialog.getRepoPassword();
    	versionModel.setRemoteRepoLocation(remoteRepoLocation.toString());
    	versionModel.setRemoteUser(remoteRepoUser);
    	
    	//now call the git wrapper to perform the push
    	gitRepo.pushModelToRemoteRepo(remoteRepoLocation, remoteRepoUser, remoteRepoPassword, versionModel.getRepoBranch());
	}
	
	private void versionExistingVersionedModel() throws VersioningException {
		//get the working directory location
		File workingDirLocation = this.getWorkingDirLocation(versionModel);
		gitRepo = new GitWrapper(workingDirLocation);
		gitRepo.getExistingGitRepo();
		
		//write and remove files to/from the archiMate objects as files to the working directory
		VersionModelFileWriter modelFileWriter = new VersionModelFileWriter(versionModel);
		modelFileWriter.writeModel();
		
		//make sure we're on the branch in the repository
		gitRepo.changeBranch(versionModel.getRepoBranch());
		
		//add and commit as necessary to the GIT repo
		gitRepo.commitArchiFilesToGit(versionModel.getModelUserName(), versionModel.getModelUserEmail(), this.versionComment);
		
		
		//make sure we always close the repository
		gitRepo.close();
	}
	
	private void versionToNewRepository(File repoLocation) throws VersioningException {
		
		//it's possible there is already a git repository in the location that has been chosen so see if that is the case and if not
		//initialise a new one
    	if (!gitRepo.getExistingGitRepo()) {
    		
    		try {
				gitRepo.initialiseGitRepo();
				this.askRepoInfo();
				this.askModelUserInfo();
				
				//set up the repo file
				VersionModelFileWriter.createModelFile(repoLocation, versionModel.getRepositoryId(), versionModel.getRepositoryDescription());
				gitRepo.commitArchiFilesToGit(versionModel.getModelUserName(), versionModel.getModelUserEmail(), this.versionComment);
				
				//write and remove files to/from the archiMate objects as files to the working directory
				VersionModelFileWriter modelFileWriter = new VersionModelFileWriter(versionModel);
				modelFileWriter.writeModel();
				
				//make sure we're on the branch in the repository
				gitRepo.createBranch(versionModel.getRepoBranch());
				
				//add and commit as necessary to the GIT repo
				gitRepo.commitArchiFilesToGit(versionModel.getModelUserName(), versionModel.getModelUserEmail(), this.versionComment);
				
				
				//make sure we always close the repository
				gitRepo.close();
			
			} 
    		catch (Exception e) {
				throw new VersioningException("Sorry, there was an issue with GIT - " + e.getMessage(), e);
			}
    		
    	}
    	else {
    		throw new VersioningException("Found an existing GIT repository where you want to create a new one, please choose a different director");
    	}
    	

	}
	
	private void versionToExistingRepository(File workingDirLocation) throws VersioningException {
		//prompt for the remote repository information
		this.askExistingRepoInfo();
		
		//clone the repository
		GitWrapper.cloneArchiRepository(repoToClone, workingDirLocation, repoUser, repoPassword);
		versionModel.setWorkingDirLocation(workingDirLocation);
		
		//get the model repo file from repository, if we can't find it, throw an error because it means
		//this git repo is not an archi one
		gitRepo = new GitWrapper(workingDirLocation);
		gitRepo.getExistingGitRepo();
		gitRepo.checkoutModelFileFromMaster(IVersionModelPropertyConstants.MODEL_FILE_NAME+".yml");
		
		//read the repo file and set up
		this.setupRepoInfo(workingDirLocation);
		
		//get the model and branch information and create the new branch this model will go on
		this.askModelUserInfo();

		
		//write and remove files to/from the archiMate objects as files to the working directory
		VersionModelFileWriter modelFileWriter = new VersionModelFileWriter(versionModel);
		modelFileWriter.writeModel();
		
		//make sure we're on the branch in the repository
		gitRepo.createBranch(versionModel.getRepoBranch());
		
		//add and commit as necessary to the GIT repo
		gitRepo.commitArchiFilesToGit(versionModel.getModelUserName(), versionModel.getModelUserEmail(), this.versionComment);
		
		
		//make sure we always close the repository
		gitRepo.close();
		
	}
	
	private File setupWorkingDirectory() {
		File repoLocation = this.askSaveDirectory();
		versionModel.setWorkingDirLocation(repoLocation);
		versionModel.setRepoBranch(GitWrapper.DEFAULT_GIT_BRANCH_NAME);
		gitRepo = new GitWrapper(repoLocation);
		return repoLocation;
	}
	
    /**
     * Ask user for the directory to save to
     */
    private File askSaveDirectory() {
        DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        //dialog.setFilterPath(this.versionModel.getModelId().toString());
        dialog.setText("GIT repository working directory location");
        //dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD, "*.*" } ); //$NON-NLS-1$
        String path = dialog.open();
        if(path == null) {
            return null;
        }
        
        
        File file = new File(path);
        
        return file;
    }
    
    private void askBasicVersionInfo() {
    	//if the version models repository id is null then this model can't have been versioned before, so ask
    	VersionModelDialog dialog = new VersionModelDialog(Display.getCurrent().getActiveShell(), versionModel.getRepositoryId()==null);
    	dialog.create();
    	dialog.open();
    	
    	createNewRepository = dialog.createNewRepository();
    	this.versionComment = dialog.getVersionComment();
    	this.pushToRemoteOnVersion = dialog.pushToRemoteOnVersion();
    }
    
    private void askRepoInfo() {
    	NewRepositoryDialog dialog = new NewRepositoryDialog(Display.getCurrent().getActiveShell());
    	dialog.create();
    	dialog.open();
    	
    	this.versionModel.setRepositoryId(dialog.getRepositoryId());
    	this.versionModel.setRepositoryDescription(dialog.getRepoDescription());
    }
    
    private void askExistingRepoInfo() {
    	RemoteRepositoryDialog dialog = new RemoteRepositoryDialog(Display.getCurrent().getActiveShell());
    	dialog.create();
    	dialog.open();
    	
    	try {
			repoToClone = new URI(dialog.getRepositoryToClone());
		} catch (URISyntaxException e) {
			MessageBox errorDialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			errorDialog.setText("Repository to clone error");
			errorDialog.setMessage("Sorry, the you have entered an invalid URI for the repository to clone.");

			dialog.open(); 

			e.printStackTrace();
		}
    	
    	repoUser = dialog.getRepoUser();
    	repoPassword = dialog.getRepoPassword();
    	versionModel.setRemoteRepoLocation(repoToClone.toString());
    	versionModel.setRemoteUser(repoUser);
    	
    }
    
    private void askModelUserInfo() {
    	NewModelDialog dialog = new NewModelDialog(Display.getCurrent().getActiveShell(), true, false);
    	dialog.setBranchList(gitRepo.getBranchList());
    	dialog.create();
    	dialog.open();
    	
    	this.versionModel.setModelUserName(dialog.getModelUser());
    	this.versionModel.setModelUserEmail(dialog.getModelUserEmail());
    	this.versionModel.setRepoBranch(dialog.getBranchToSaveTo());
    }
    
	private void setupRepoInfo(File workingDir) throws VersioningException {
		Map repoInfo;
		try {
			repoInfo = YamlReader.readVersionObject(new File(workingDir.toString() + File.separatorChar + IVersionModelPropertyConstants.MODEL_FILE_NAME + ".yml"));
		} catch (IOException e) {
			throw new VersioningException("Sorry had an issue trying to read the repository file - " + e.getMessage(), e);
		}
		
    	this.versionModel.setRepositoryId((String) repoInfo.get(IVersionModelPropertyConstants.MODEL_REPO_ID_PROPERTY_NAME));
    	this.versionModel.setRepositoryDescription((String) repoInfo.get(IVersionModelPropertyConstants.MODEL_REPO_DESCRIPTION_PROPERTY_NAME));
	}
    
    
    private File getWorkingDirLocation(IVersionModel versionModel) {
		if (versionModel.getWorkingDirLocation() != null) {

			return versionModel.getWorkingDirLocation();
		}
		else {
			return null;
		}
    }

}
