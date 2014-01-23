package nz.co.cyma.integrations.archigitsync.plugin.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import nz.co.cyma.integrations.archigitsync.model.RepositoryDirectory;
import nz.co.cyma.integrations.archigitsync.plugin.VersioningException;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


/**
 * @author michael
 * This is a wrapping class for the GIT library that abstracts the lower level GIT calls
 *
 */
public class GitWrapper {
	public static String DEFAULT_GIT_BRANCH_NAME = "master";
	
	File gitDir = null;
	Git archiRepo = null;
	
	public GitWrapper(File gitDir) {
		this.gitDir = gitDir;
		//this.initialiseGitRepo();
	}
	
	public GitWrapper() {
		//allow this to be instantiated with no file if not known
	}
	
	public void initialiseGitRepo() throws Exception {
		//FileRepository archiFileRepo = new FileRepository(gitDir);
		InitCommand repoInit = Git.init();
		repoInit.setDirectory(gitDir);
		//repoInit.setBare(true);
		archiRepo = repoInit.call();
		
	}
	
	
	public File getGitDir() {
		return gitDir;
	}

	public void setGitDir(File gitDir) {
		this.gitDir = gitDir;
	}
	
	public void close() {
		archiRepo.getRepository().close();
	}

	/**
	 * Try to open a git repository for the location given
	 * @return true if able to successfully open repository
	 */
	public boolean getExistingGitRepo() {
		//try to open the GIT repository, 
		if (gitDir != null)
		try {
			archiRepo = Git.open(gitDir);
			return true;
		} catch (IOException e) {
			return false;
		}
		else
			return false;
	}
	
	public void commitArchiFilesToGit(String userName, String userEmail, String message) throws VersioningException {
		
		try {
			this.addNewArchiFilesToGit(userName, userEmail, message);
			this.updateRemoveNewArchiFilesToGit(userName, userEmail, message);
		} catch (IOException e1) {
			throw new VersioningException(e1.getMessage(), e1);
		}
		
		CommitCommand commit = archiRepo.commit();
		
		//TODO fix these with proper info
		commit.setMessage(message);
		commit.setAuthor(userName, userEmail);
		
		try {
			commit.call();
			
		} catch (NoHeadException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (NoMessageException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (UnmergedPathsException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (ConcurrentRefUpdateException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (WrongRepositoryStateException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
		
		
	}
	
	public void addNewArchiFilesToGit(String userName, String userEmail, String message) throws VersioningException {
		AddCommand add = archiRepo.add();
		add.addFilepattern(".");
		
		for (RepositoryDirectory dir: RepositoryDirectory.values()) {
			add.addFilepattern(dir.getDirectoryName());
		}
		
		try {
			add.call();
		} catch (NoFilepatternException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
	}
	
	public void updateRemoveNewArchiFilesToGit(String userName, String userEmail, String message) throws VersioningException {
		AddCommand add = archiRepo.add();
		add.setUpdate(true);
		add.addFilepattern(".");
		
		for (RepositoryDirectory dir: RepositoryDirectory.values()) {
			add.addFilepattern(dir.getDirectoryName());
		}
		
		try {
			add.call();
		} catch (NoFilepatternException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			throw new VersioningException(e.getMessage(), e);
		}
	}
	
	//make sure we have the model file from the master branch
	public void checkoutModelFileFromMaster(String modelFile) throws VersioningException {
		try {
			archiRepo.checkout().addPath(modelFile).call();
		} catch (RefAlreadyExistsException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (RefNotFoundException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (InvalidRefNameException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (CheckoutConflictException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
	}
	
	public void createAndCheckoutBranchFromExistingBranch(String branchName, String existingBranch) throws VersioningException {
		CreateBranchCommand branchCreate = archiRepo.branchCreate();
		branchCreate.setName(branchName);
		branchCreate.setStartPoint(existingBranch);
		branchCreate.setUpstreamMode(SetupUpstreamMode.TRACK);
		
		CheckoutCommand branchCheckout = archiRepo.checkout();
		branchCheckout.setName(branchName);
		
		
		try {
			branchCreate.call();
			branchCheckout.call();
		} catch (RefAlreadyExistsException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (RefNotFoundException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (InvalidRefNameException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
	}
	
	public void createBranch(String branch) throws VersioningException {
		CheckoutCommand branchCreate = archiRepo.checkout();
		branchCreate.setCreateBranch(true);
		branchCreate.setName(branch);
		
		try {
			branchCreate.call();
		} catch (RefAlreadyExistsException e) {
			//if the branch already exists, then change to it
			this.changeBranch(branch);
		} catch (RefNotFoundException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (InvalidRefNameException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
	}
	
	public void changeBranch(String branch) throws VersioningException {
		CheckoutCommand branchCheckout = archiRepo.checkout();
		branchCheckout.setName(branch);
		
		try {
			branchCheckout.call();
		} catch (RefAlreadyExistsException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (RefNotFoundException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (InvalidRefNameException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
		
	}
	
	public void checkoutRemoteBranch(String remoteBranch) throws VersioningException {
		CheckoutCommand branchCheckout = archiRepo.checkout();
		branchCheckout.setName(remoteBranch.substring(remoteBranch.lastIndexOf('/')+1));
		branchCheckout.setCreateBranch(true);
		branchCheckout.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
		branchCheckout.setStartPoint(remoteBranch);
		
		try {
			branchCheckout.call();
		} catch (RefAlreadyExistsException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (RefNotFoundException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (InvalidRefNameException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
		
	}
	
	public String [] getBranchList() {
		ListBranchCommand branchListCmd = archiRepo.branchList();
		branchListCmd.setListMode(ListMode.REMOTE);
		List<Ref> branchList = null;
		try {
			branchList = branchListCmd.call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String [] stringList = new String[branchList.size()+1];
		stringList[0] = "master";
		int ctr = 1;
		for(Ref branch: branchList) {
			
			stringList[ctr] = branch.getName();
			ctr++;
		}
		
		return stringList;
	}
	
	public static void cloneArchiRepository(URI repoToClone, File workingDir, String repoUser, String repoPassword) throws VersioningException {

			
		
		CloneCommand clone = Git.cloneRepository();

		if(repoUser != null && repoPassword != null) {
			UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(repoUser, repoPassword);
			clone.setCredentialsProvider(credentialsProvider);
		}
		
		//clone.setNoCheckout(true);
		clone.setURI(repoToClone.toString());
		clone.setDirectory(workingDir);
		//clone.setCloneAllBranches(true);
		//clone.setRemote("master_repo");
		
		try {
			clone.call();
		} catch (InvalidRemoteException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (TransportException e) {
			throw new VersioningException(e.getMessage(), e);
		} catch (GitAPIException e) {
			throw new VersioningException(e.getMessage(), e);
		}
	}
}
