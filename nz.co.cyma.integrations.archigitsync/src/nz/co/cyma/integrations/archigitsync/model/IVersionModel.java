package nz.co.cyma.integrations.archigitsync.model;

import java.io.File;
import java.util.List;

import nz.co.cyma.integrations.archigitsync.model.impl.VersionElement;
import uk.ac.bolton.archimate.model.FolderType;
import uk.ac.bolton.archimate.model.IArchimateDiagramModel;
import uk.ac.bolton.archimate.model.IArchimateElement;
import uk.ac.bolton.archimate.model.IDiagramModel;
import uk.ac.bolton.archimate.model.IFolder;
import uk.ac.bolton.archimate.model.IRelationship;

public interface IVersionModel {
	//public void createBusinessVersionObjects(IFolder folder);
	public List<IVersionElement> getBusinessVersionObjects();
	public List<IVersionElement> getApplicationVersionObjects();
	public List<IVersionElement> getTechnologyVersionObjects();
	public List<IVersionElement> getMotivationVersionObjects();
	public List<IVersionElement> getImplementationVersionObjects();
	public List<IVersionElement> getConnectorVersionObjects();
	public List<IVersionElement> getRelationshipVersionObjects();
	public List<IVersionElement> getDerivedRelationshipVersionObjects();
	public List<IVersionElement> getDiagramVersionObjects();
	public File getRepoLocation();
	public File getDefaultRepoLocation();
	public void setRepoLocation(File repoLocation);
	public void setWorkingDirLocation(File workingDirLocation);
	public File getWorkingDirLocation();
	public File getDefaultWorkingDirLocation();
	public String getModelName();
	public String getModelId();
	public String getRepositoryId();
	public void setRepositoryId(String repositoryId);
	public String getRepositoryDescription();
	public void setRepositoryDescription(String repositoryDescription);
	public String getModelUserName();
	public void setModelUserName(String modelUserName);
	public String getModelUserEmail();
	public void setModelUserEmail(String modelUserEmail);
	public IVersionElement createVersionElement(IArchimateElement archiElement, IFolderPath folderPath);
	public IVersionElement createVersionRelationshipElement(IRelationship archiElement, IFolderPath folderPath);
	public IVersionElement createVersionDiagramElement(IArchimateDiagramModel archiElement, IFolderPath folderPath);
	public void addVersionElementToModel(IVersionElement element, FolderType elementType);
	public void setRepoBranch(String repoBranch);
	public String getRepoBranch();
}
