package nz.co.cyma.integrations.archigitsync.plugin.modelexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import uk.ac.bolton.archimate.model.FolderType;
import uk.ac.bolton.archimate.model.IArchimateDiagramModel;
import uk.ac.bolton.archimate.model.IArchimateElement;
import uk.ac.bolton.archimate.model.IArchimateModel;
import uk.ac.bolton.archimate.model.IDiagramModel;
import uk.ac.bolton.archimate.model.IFolder;
import uk.ac.bolton.archimate.model.IProperty;
import uk.ac.bolton.archimate.model.IRelationship;
import nz.co.cyma.integrations.archigitsync.model.IFolderPath;
import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionModel;
import nz.co.cyma.integrations.archigitsync.model.IVersionModelPropertyConstants;
import nz.co.cyma.integrations.archigitsync.model.impl.FolderPath;
import nz.co.cyma.integrations.archigitsync.model.impl.VersionFactory;
import nz.co.cyma.integrations.archigitsync.plugin.ArchiUtils;


/**
 * @author michael
 * Runs through the ArchiModel and creates the versioning objects that will be written
 * out to the git repository.
 *
 */
public class ModelPreparer {
	IVersionModel versionModel = null;
	IFolderPath folderPath = null;
	
	public IVersionModel generateVersionModel(IArchimateModel model) throws IOException {
		versionModel = VersionFactory.init(model);
		
		//add in the properties held on the model
		Map<String, IProperty> modelProperties = ArchiUtils.getPropertiesMap(model.getProperties());
		if(modelProperties.containsKey(IVersionModelPropertyConstants.MODEL_USER_PROPERTY_NAME))
			versionModel.setModelUserName(modelProperties.get(IVersionModelPropertyConstants.MODEL_USER_PROPERTY_NAME).getValue());
		
		if(modelProperties.containsKey(IVersionModelPropertyConstants.MODEL_USER_EMAIL_NAME))
			versionModel.setModelUserEmail(modelProperties.get(IVersionModelPropertyConstants.MODEL_USER_EMAIL_NAME).getValue());	
		
		if(modelProperties.containsKey(IVersionModelPropertyConstants.WORKING_DIR_PROPERTY_NAME))
			versionModel.setWorkingDirLocation(new File(modelProperties.get(IVersionModelPropertyConstants.WORKING_DIR_PROPERTY_NAME).getValue()));
		
		if(modelProperties.containsKey(IVersionModelPropertyConstants.MODEL_REPO_BRANCH_PROPERTY_NAME))
			versionModel.setRepoBranch(modelProperties.get(IVersionModelPropertyConstants.MODEL_REPO_BRANCH_PROPERTY_NAME).getValue());
		
		
		this.createFolderObjects(model.getFolder(FolderType.BUSINESS));
		this.createFolderObjects(model.getFolder(FolderType.APPLICATION));
		this.createFolderObjects(model.getFolder(FolderType.TECHNOLOGY));
		this.createFolderObjects(model.getFolder(FolderType.MOTIVATION));
		this.createFolderObjects(model.getFolder(FolderType.IMPLEMENTATION_MIGRATION));
		this.createFolderObjects(model.getFolder(FolderType.CONNECTORS));
		if (model.getFolder(FolderType.DERIVED) != null)
			this.createFolderObjects(model.getFolder(FolderType.DERIVED));
		this.createFolderObjects(model.getFolder(FolderType.RELATIONS));
		this.createFolderObjects(model.getFolder(FolderType.DIAGRAMS));
		//versionModel.createBusinessVersionObjects(model.getFolder(FolderType.BUSINESS));
		
		//model.getFolder(FolderType.BUSINESS)
		
		return versionModel;
	}
	

    private void createFolderObjects(IFolder folder) throws IOException {
        List<IVersionElement> list = new ArrayList<IVersionElement>();
        folderPath = new FolderPath(folder.getType());
        
        getElements(folder, list);
        for(IVersionElement versionElement : list) {
        	versionModel.addVersionElementToModel(versionElement, folder.getType());
        }
    }
    
    private void getElements(IFolder folder, List<IVersionElement> list) {
        for(EObject object : folder.getElements()) {
        	if(object instanceof IRelationship)
        		list.add(versionModel.createVersionRelationshipElement((IRelationship) object, folderPath.clonePath()));
        	else if(object instanceof IArchimateElement)
        		list.add(versionModel.createVersionElement((IArchimateElement) object, folderPath.clonePath()));
        	else if(object instanceof IArchimateDiagramModel)
        		list.add(versionModel.createVersionDiagramElement((IArchimateDiagramModel) object, folderPath.clonePath()));
        }
        
        for(IFolder f : folder.getFolders()) {
        	folderPath.addFolderToPath(f.getName());
            getElements(f, list);
            folderPath.moveUpFolderTree();
        }
    }
	
	
	
	
}
