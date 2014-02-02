package nz.co.cyma.integrations.archigitsync.plugin;

import java.io.File;


/**
 * @author michael
 * Class to hold onto general preferences (most likely loaded from somewhere)
 *
 */
public class Preferences {
	public File VC_WORKING_DIR = null;
	public File DEFAULT_GIT_REPO_DIR = null;
	
	public Preferences() {
		this.VC_WORKING_DIR = new File("C:\\Users\\michael.cyma-laptop\\Documents\\Development\\test_archi_plugin\\archi_working_dir");
		this.DEFAULT_GIT_REPO_DIR = new File("C:\\Users\\michael.cyma-laptop\\Documents\\Development\\test_archi_plugin\\git_repo");
	}
	
}
