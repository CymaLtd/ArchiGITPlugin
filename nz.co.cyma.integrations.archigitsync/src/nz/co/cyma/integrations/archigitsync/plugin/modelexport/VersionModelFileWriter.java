package nz.co.cyma.integrations.archigitsync.plugin.modelexport;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionModel;
import nz.co.cyma.integrations.archigitsync.model.IVersionModelPropertyConstants;
import nz.co.cyma.integrations.archigitsync.model.RepositoryDirectory;
import nz.co.cyma.integrations.archigitsync.plugin.VersioningException;
import nz.co.cyma.integrations.archigitsync.plugin.yaml.YamlWriter;


/**
 * @author michael
 * This class is responsible for writing out the contents of the version model as files
 * to the working directory.
 *
 */
public class VersionModelFileWriter {
	private IVersionModel versionModel = null;
	
	public VersionModelFileWriter(IVersionModel versionModel) {
		this.versionModel = versionModel;
	}
	
	public void writeModel() throws VersioningException {
		this.createBusinessLayerFiles();
		this.createApplicationLayerFiles();
		this.createTechnologyLayerFiles();
		this.createMotivationLayerFiles();
		this.createImplementationLayerFiles();
		this.createConnectorLayerFiles();
		this.createRelationshipFiles();
		this.createDerivedRelationshipFiles();
		this.createDiagramFiles();
	}
	
	public static void createModelFile(File workingDirectory, String repositoryId, String repositoryDescription) throws VersioningException {
		File modelFile = addYamlFileToDir(workingDirectory, IVersionModelPropertyConstants.MODEL_FILE_NAME);
		Map modelProperties = new HashMap();
		modelProperties.put(IVersionModelPropertyConstants.MODEL_REPO_ID_PROPERTY_NAME, repositoryId);
		modelProperties.put(IVersionModelPropertyConstants.MODEL_REPO_DESCRIPTION_PROPERTY_NAME, repositoryDescription);
		//modelProperties.put(IVersionModelPropertyConstants.VERSION_REPO_DIR_PROPERTY_NAME, this.versionModel.getRepoLocation().toString());
		//modelProperties.put(IVersionModelPropertyConstants.WORKING_DIR_PROPERTY_NAME, this.versionModel.getWorkingDirLocation().toString());
		
		try {
			YamlWriter.writeModelFile(modelFile, modelProperties);
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the repository file - " + e.getMessage(),e);
		}
	}
	
	public static void updateBranchFile(File workingDirectory, String branchId, String branchName, String branchDescription) throws VersioningException {
		File branchFile = addYamlFileToDir(workingDirectory, IVersionModelPropertyConstants.BRANCH_FILE_NAME);
		Map branchProperties = new HashMap();
		branchProperties.put(IVersionModelPropertyConstants.BRANCH_ID_PROPERTY_NAME, branchId);
		branchProperties.put(IVersionModelPropertyConstants.BRANCH_NAME_PROPERTY_NAME, branchName);
		branchProperties.put(IVersionModelPropertyConstants.BRANCH_DESCRIPTION_PROPERTY_NAME, branchDescription);
	
		try {
			YamlWriter.writeBranchFile(branchFile, branchProperties);
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the repository file - " + e.getMessage(),e);
		}
	}
	
	private void createBusinessLayerFiles() throws VersioningException {
		File businessLayerDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.BUSINESS_LAYER.getDirectoryName());
		if(!businessLayerDir.exists()) {
			if(!businessLayerDir.mkdir()) {
				throw new VersioningException("couldn't create business layer directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(businessLayerDir, versionModel.getBusinessVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the business object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getBusinessVersionObjects(), businessLayerDir);
			
	}
	
	private void createApplicationLayerFiles() throws VersioningException {
		File applicationLayerDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.APPLICATION_LAYER.getDirectoryName());
		if(!applicationLayerDir.exists()) {
			applicationLayerDir.setWritable(true, false);
			
			if(!applicationLayerDir.mkdir()) {
				throw new VersioningException("couldn't create application layer directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(applicationLayerDir, versionModel.getApplicationVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the application object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getApplicationVersionObjects(), applicationLayerDir);
			
	}
	
	private void createTechnologyLayerFiles() throws VersioningException {
		File technologyLayerDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.TECHNOLOGY_LAYER.getDirectoryName());
		if(!technologyLayerDir.exists()) {
			technologyLayerDir.setWritable(true, false);
			
			if(!technologyLayerDir.mkdir()) {
				throw new VersioningException("couldn't create technology layer directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(technologyLayerDir, versionModel.getTechnologyVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the technology object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getTechnologyVersionObjects(), technologyLayerDir);
			
	}
	
	private void createMotivationLayerFiles() throws VersioningException {
		File motivationLayerDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.MOTIVATION_LAYER.getDirectoryName());
		if(!motivationLayerDir.exists()) {
			motivationLayerDir.setWritable(true, false);
			
			if(!motivationLayerDir.mkdir()) {
				throw new VersioningException("couldn't create motivation layer directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(motivationLayerDir, versionModel.getMotivationVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the motivation extension object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getMotivationVersionObjects(), motivationLayerDir);
			
	}
	
	private void createImplementationLayerFiles() throws VersioningException {
		File implementationLayerDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.IMPLEMENTATION_LAYER.getDirectoryName());
		if(!implementationLayerDir.exists()) {
			implementationLayerDir.setWritable(true, false);
			
			if(!implementationLayerDir.mkdir()) {
				throw new VersioningException("couldn't create technology layer directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(implementationLayerDir, versionModel.getImplementationVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the implementation extension object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getImplementationVersionObjects(), implementationLayerDir);
			
	}
	
	private void createConnectorLayerFiles() throws VersioningException {
		File connectorLayerDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.CONNECTORS.getDirectoryName());
		if(!connectorLayerDir.exists()) {
			connectorLayerDir.setWritable(true, false);
			
			if(!connectorLayerDir.mkdir()) {
				throw new VersioningException("couldn't create connectors directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(connectorLayerDir, versionModel.getConnectorVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the connector object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getConnectorVersionObjects(), connectorLayerDir);
			
	}
	
	private void createRelationshipFiles() throws VersioningException {
		File relationshipsDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.RELATIONSHIPS.getDirectoryName());
		if(!relationshipsDir.exists()) {
			relationshipsDir.setWritable(true, false);
			if(!relationshipsDir.mkdir()) {
				throw new VersioningException("couldn't create relationships directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(relationshipsDir, versionModel.getRelationshipVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the relationship object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getRelationshipVersionObjects(), relationshipsDir);
			
	}
	
	private void createDerivedRelationshipFiles() throws VersioningException {
		File derivedRelationshipsDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.DERIVED_RELATIONSHIPS.getDirectoryName());
		if(!derivedRelationshipsDir.exists()) {
			derivedRelationshipsDir.setWritable(true, false);
			if(!derivedRelationshipsDir.mkdir()) {
				throw new VersioningException("couldn't create derived relationships directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(derivedRelationshipsDir, versionModel.getDerivedRelationshipVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the derived relationship object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getDerivedRelationshipVersionObjects(), derivedRelationshipsDir);
			
	}
	
	private void createDiagramFiles() throws VersioningException {
		File diagramsDir = this.appendDirectory(versionModel.getWorkingDirLocation(), RepositoryDirectory.DIAGRAMS.getDirectoryName());
		if(!diagramsDir.exists()) {
			diagramsDir.setWritable(true, false);
			if(!diagramsDir.mkdir()) {
				throw new VersioningException("couldn't create diagrams directory");
			}
		}
		
		try {
			YamlWriter.writeVersionedYamlObjects(diagramsDir, versionModel.getDiagramVersionObjects());
		} catch (IOException e) {
			throw new VersioningException("Sorry, had an issue writing out the diagram object files - " + e.getMessage(),e);
		}
		
		//need to deal with removals as well, anything that is listed as a file, but is no longer in the model should be
		//deleted as a file
		this.deleteFilesForDeletedElements(versionModel.getDiagramVersionObjects(), diagramsDir);
			
	}
	
	private static File addYamlFileToDir(File directory, String name) {
		return new File(directory.toString()+File.separatorChar+name+".yml");
	}
	
	private File appendDirectory(File corePath, String directoryName) {
		return new File(corePath.toString()+File.separatorChar+directoryName);
	}
	
	private Map<String,IVersionElement> getVersionElementFileMap(List<IVersionElement> versionElementList) {
		Map<String,IVersionElement> fileMap = new HashMap<String,IVersionElement>();
		for(IVersionElement versionElement: versionElementList) {
			fileMap.put(versionElement.getId() + ".yml", versionElement);
		}
		
		return fileMap;
	}

	private void deleteFilesForDeletedElements(List<IVersionElement> versionElementList, File directory) {
		Map<String, IVersionElement> versionFileMap = this.getVersionElementFileMap(versionElementList);
		File [] fileList = directory.listFiles();
		for(int i=0; i<fileList.length; i++) {
			if(!versionFileMap.containsKey(fileList[i].getName())) {
				//if we can't find an object in our map for the file, then it must have been deleted in the model so delete the
				//corresponding file
				fileList[i].delete();
			}
		}
	}
	
}
