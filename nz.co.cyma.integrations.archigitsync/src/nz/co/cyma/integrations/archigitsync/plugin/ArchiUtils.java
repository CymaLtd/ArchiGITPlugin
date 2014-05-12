package nz.co.cyma.integrations.archigitsync.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nz.co.cyma.integrations.archigitsync.model.IVersionModelPropertyConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;

public class ArchiUtils {
	private final static String BASE_PATH = System.getProperty("user.dir");
	private final static String PLUGIN_DIR_NAME = "archi_git_plugin";
	private final static String PROPERTIES_NAME_SUFFIX = "repo_preferences.properties";
	
	public static Map<String, IProperty> getPropertiesMap(EList<IProperty> archPropertyList) {
		Map<String, IProperty> propertiesMap = new <String, IProperty>HashMap();
		
		for(IProperty modelProperty: archPropertyList ) {
			propertiesMap.put(modelProperty.getKey(), modelProperty);
		}
		
		return propertiesMap;
	}
	
	public static Map<String, IFolder> getFolderMap(EList<IFolder> folders) {
		Map folderMap = new HashMap();
		for(IFolder folder: folders) {
			folderMap.put(folder.getName(), folder);
		}
		
		return folderMap;
	}
	
	public static File getPluginDirHome() {
		File pluginDir = new File(BASE_PATH+File.separatorChar+PLUGIN_DIR_NAME);
		
		if(!pluginDir.exists()) {
			pluginDir.mkdir();
		}
		
		return pluginDir;
	}
	
	/*
	 * Return the directory where the git repository is or create a directory if it doesn't exist. The expectation is that a
	 * valid directory name is passed in for the git directory. If the newModel flag is set to true and the directory exists,
	 * then delete it and replace with the one passed in.
	 */
	public static File getModelGitDir(String modelGitDirName, boolean newModel) {
		
		File gitDir = new File(ArchiUtils.getPluginDirHome().getPath()+File.separatorChar+modelGitDirName);
		if(!gitDir.exists()) {
			gitDir.mkdir();
		}
		else if(newModel) {
			FileUtils.deleteQuietly(gitDir);
		}
		
		return gitDir;
	}
	
	public static void savePluginRepoInfo(String repoId, Properties pluginProperties) throws VersioningException {
		File propertiesFile = new File(ArchiUtils.getPluginDirHome().toString()+File.separatorChar+repoId+"_"+PROPERTIES_NAME_SUFFIX);
		try {
			pluginProperties.store(new FileOutputStream(propertiesFile), "persistent git plugin properties");
		} catch (FileNotFoundException e) {
			throw new VersioningException("issue with properties file", e);
		} catch (IOException e) {
			throw new VersioningException("issue with properties file", e);
		}
	}

	
	public static Properties loadPluginRepoInfo(String repoId) throws VersioningException {
		File propertiesFile = new File(ArchiUtils.getPluginDirHome().toString()+File.separatorChar+repoId+"_"+PROPERTIES_NAME_SUFFIX);
		Properties pluginProperties = new Properties();
		try {
			pluginProperties.load(new FileInputStream(propertiesFile));
			return pluginProperties;
		} catch (FileNotFoundException e) {
			return pluginProperties;
		} catch (IOException e) {
			throw new VersioningException("issue with properties file", e);
		}
	}
	
	/*
	 * Return a string that is safe to use as a directory/branch/file name for a model as well as unique
	 */
	public static String getSafeUniqueModelName(String modelName, String modelId) {
		//String uniquePart = Double.toString(Math.random());
		
		//use a regular expression to address
		return modelName.replaceAll("[^a-z0-9A-Z]", "_").toLowerCase() + "__" +modelId;
	}
	
	/*
	 * Get a map of the repo property files for this user
	 */
	public static Map getRepoPropertyMap() throws VersioningException {
		Iterator i = FileUtils.listFiles(getPluginDirHome(), new SuffixFileFilter(".properties"), null).iterator();
		Map propertyFileMap = new HashMap();
		while(i.hasNext()) {
			File propertyFile = (File) i.next();
			Properties repoProperties = new Properties();
			try {
				repoProperties.load(new FileInputStream(propertyFile));
			} catch (FileNotFoundException e) {
				throw new VersioningException("issue with properties file", e);
			} catch (IOException e) {
				throw new VersioningException("issue with properties file", e);
			}
			
			propertyFileMap.put(repoProperties.get(IVersionModelPropertyConstants.MODEL_REPO_ID_PROPERTY_NAME), repoProperties);
		}
		
		return propertyFileMap;
	}
}
