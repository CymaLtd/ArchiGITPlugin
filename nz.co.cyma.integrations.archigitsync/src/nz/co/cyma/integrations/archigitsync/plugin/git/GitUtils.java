package nz.co.cyma.integrations.archigitsync.plugin.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;

public class GitUtils {
	public static Git getExistingGitRepo(File location) {
		Git repo = null;
		
		//try to open the GIT repository, 
		try {
			repo = Git.open(location);
		} catch (IOException e) {
			return null;
		}
		
		return repo;
	}
}
