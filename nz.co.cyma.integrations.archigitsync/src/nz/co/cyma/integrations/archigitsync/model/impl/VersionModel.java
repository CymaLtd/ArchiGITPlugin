package nz.co.cyma.integrations.archigitsync.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelBendpoint;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.ITextContent;
import com.archimatetool.model.impl.ArchimateFactory;
import com.archimatetool.model.impl.Property;
import com.archimatetool.model.util.ArchimateAdapterFactory;
import nz.co.cyma.integrations.archigitsync.model.VersionDiagramFeatureAttribute;
import nz.co.cyma.integrations.archigitsync.model.IFolderPath;
import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionModel;
import nz.co.cyma.integrations.archigitsync.model.IVersionModelPropertyConstants;
import nz.co.cyma.integrations.archigitsync.plugin.ArchiUtils;
import nz.co.cyma.integrations.archigitsync.plugin.Preferences;

public class VersionModel implements IVersionModel, IVersionModelPropertyConstants {
	private IArchimateModel archiModel = null;
	private Map<String, IVersionElement> businessElements = null;
	private Map<String, IVersionElement> applicationElements = null;
	private Map<String, IVersionElement> technologyElements = null;
	private Map<String, IVersionElement> motivationElements = null;
	private Map<String, IVersionElement> implementationElements = null;
	private Map<String, IVersionElement> connectorElements = null;
	private Map<String, IVersionElement> relationshipElements = null;
	private Map<String, IVersionElement> derivedRelationshipElements = null;
	private Map<String, IVersionElement> diagramElements = null;
	
	private String modelName = null;
	private File versionWorkingDir = null;
	private File versionRepoDir = null;
	private Preferences versionPrefs = null;
	private String repositoryId = null;
	private String repositoryDescription = null;
	private String modelUser = null;
	private String modelUserEmail = null;
	private String repoBranch = null;
	private String remoteRepoLocation = null;
	private String remoteUser = null;
	
	protected VersionModel(IArchimateModel archiModel) {
		this.archiModel = archiModel;
		this.setupLocations();
		this.versionPrefs = new Preferences();
		this.businessElements = new <String, IVersionElement>HashMap();
		this.applicationElements = new <String, IVersionElement>HashMap();
		this.technologyElements = new <String, IVersionElement>HashMap();
		this.motivationElements = new <String, IVersionElement>HashMap();
		this.implementationElements = new <String, IVersionElement>HashMap();
		this.connectorElements = new <String, IVersionElement>HashMap();
		this.relationshipElements = new <String, IVersionElement>HashMap();
		this.derivedRelationshipElements = new <String, IVersionElement>HashMap();
		this.diagramElements = new <String, IVersionElement>HashMap();
	}

//	@Override
//	public void createVersionElement(IArchimateElement archiElement,
//			FolderType layerType, IFolderPath folderPath) {
//		// TODO Auto-generated method stub
//		
//	}
	
	
	/**
	 * If the locations for the working dir and repo are already set up, default to them
	 */
	private void setupLocations() {
		
		List<IProperty> modelProperties = archiModel.getProperties();
		for(IProperty modelProperty: modelProperties ) {
			if (modelProperty.getKey().equals(this.WORKING_DIR_PROPERTY_NAME)) {
				this.versionWorkingDir = new File((String) modelProperty.getValue());
			}
			else if (modelProperty.getKey().equals(this.VERSION_REPO_DIR_PROPERTY_NAME)) {
				this.versionRepoDir = new File((String) modelProperty.getValue());
			}
			else if (modelProperty.getKey().equals(this.MODEL_REPO_ID_PROPERTY_NAME)) {
				this.repositoryId = (String) modelProperty.getValue();
			}
			else if (modelProperty.getKey().equals(this.MODEL_REPO_DESCRIPTION_PROPERTY_NAME)) {
				this.repositoryDescription = (String) modelProperty.getValue();
			}
			else if (modelProperty.getKey().equals(this.REMOTE_REPO_LOCATION_PROPERTY_NAME)) {
				this.remoteRepoLocation = (String) modelProperty.getValue();
			}
			else if (modelProperty.getKey().equals(this.REMOTE_REPO_USER_PROPERTY_NAME)) {
				this.remoteUser = (String) modelProperty.getValue();
			}
		}
	}
	
//	public void createBusinessVersionObjects(IFolder folder) {
//		IFolderPath folderPath = new FolderPath(FolderType.BUSINESS);
//		
//		businessElements = this.createVersionObjects(folderPath, folder);
//		//
//	}
	
	public List<IVersionElement> getBusinessVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(businessElements.values());
		return list;
	}
	
	public List<IVersionElement> getApplicationVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(applicationElements.values());
		return list;
	}

	public List<IVersionElement> getTechnologyVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(technologyElements.values());
		return list;
	}
	
	public List<IVersionElement> getMotivationVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(motivationElements.values());
		return list;
	}
	
	public List<IVersionElement> getImplementationVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(implementationElements.values());
		return list;
	}
	
	public List<IVersionElement> getConnectorVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(connectorElements.values());
		return list;
	}
	
	public List<IVersionElement> getRelationshipVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(relationshipElements.values());
		return list;
	}
	
	public List<IVersionElement> getDerivedRelationshipVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(derivedRelationshipElements.values());
		return list;
	}
	
	public List<IVersionElement> getDiagramVersionObjects() {
		List<IVersionElement> list = new ArrayList<IVersionElement>(diagramElements.values());
		return list;
	}
	
	public IVersionElement createVersionElement(IArchimateElement archiElement, IFolderPath folderPath) {
		return new VersionElement(archiElement, folderPath);
	}
	
	public IVersionElement createVersionRelationshipElement(IRelationship archiElement, IFolderPath folderPath) {
		IVersionElement source = this.getModelElement(archiElement.getSource().getId());
		IVersionElement target = this.getModelElement(archiElement.getTarget().getId());
		return new VersionRelationshipElement(archiElement, folderPath, source, target);
	}
	
	public IVersionElement createVersionDiagramElement(IArchimateDiagramModel archiElement, IFolderPath folderPath) {
		VersionDiagramElement versionDiagramElement = new VersionDiagramElement(archiElement, folderPath);
		versionDiagramElement.addDiagramFeature(VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_ROUTER, Integer.toString(archiElement.getConnectionRouterType()));
		
		createVersionDiagramObjects(archiElement.getChildren(), folderPath, versionDiagramElement, null);
		
		return versionDiagramElement;
	}
	
	private IVersionElement createVersionDiagramObjects(EList<IDiagramModelObject> diagramObjects, IFolderPath folderPath, VersionDiagramElement versionDiagramElement, String parentId) {
		for(IDiagramModelObject object: diagramObjects) {
			if(object instanceof IDiagramModelArchimateObject) {
				IDiagramModelArchimateObject diagramElement = (IDiagramModelArchimateObject) object;
				handleDiagramElementFeatures(versionDiagramElement, diagramElement, parentId);
				
				EList<IDiagramModelConnection> connectionList = diagramElement.getSourceConnections();
				for(IDiagramModelConnection connection: connectionList) {
					if(connection instanceof IDiagramModelArchimateConnection) {
						IDiagramModelArchimateConnection connectionElement = (IDiagramModelArchimateConnection) connection;
						this.handleDiagramRelationshipFeatures(false, diagramElement.getId(), versionDiagramElement, connectionElement);
					} 
					//if it's not a model connection, then it is one to a note on a diagram
					else {
						IDiagramModelConnection noteConnection = (IDiagramModelConnection) connection;
						this.handleDiagramNoteRelationshipFeatures(false, diagramElement.getId(), versionDiagramElement, noteConnection);
					}
				}
				
				createVersionDiagramObjects(diagramElement.getChildren(), folderPath, versionDiagramElement, diagramElement.getId());
				
			}
			//otherwise this is a diagram specific element i.e. a note or a group
			else {
				handleDiagramSpecificFeatures(versionDiagramElement, object, parentId);
				
				//only group elements can contain other elements
				if(object instanceof IDiagramModelGroup) {
					IDiagramModelGroup groupObject = (IDiagramModelGroup) object;
					createVersionDiagramObjects(groupObject.getChildren(), folderPath, versionDiagramElement, object.getId());
				}
				else {
					IDiagramModelNote noteObject = (IDiagramModelNote) object;
					EList<IDiagramModelConnection> connectionList = noteObject.getSourceConnections();
					for(IDiagramModelConnection connection: connectionList) {
						IDiagramModelConnection noteConnection = (IDiagramModelConnection) connection;
						this.handleDiagramNoteRelationshipFeatures(true, noteObject.getId(), versionDiagramElement, noteConnection);
					}
				}
				
				
			}
		}
		
		return versionDiagramElement;
	}
	
	private void handleDiagramSpecificFeatures(VersionDiagramElement versionDiagramElement, IDiagramModelObject diagramElement, String parentId) {
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FILL_COLOUR, diagramElement.getFillColor());
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT, diagramElement.getFont());
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR, diagramElement.getFontColor());
		
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_X, Integer.toString(diagramElement.getBounds().getX()));
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_Y, Integer.toString(diagramElement.getBounds().getY()));
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_WIDTH, Integer.toString(diagramElement.getBounds().getWidth()));
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_HEIGHT, Integer.toString(diagramElement.getBounds().getHeight()));
	
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT, Integer.toString(diagramElement.getTextAlignment()));
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION, Integer.toString(diagramElement.getTextPosition()));
		
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_NAME, diagramElement.getName());
		versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_TYPE, diagramElement.eClass().getName());
		
		if(diagramElement instanceof IDiagramModelGroup)
			versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_DOCUMENTATION, ((IDocumentable) diagramElement).getDocumentation());
		else
			versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_DOCUMENTATION, ((ITextContent) diagramElement).getContent());
		
		if(parentId !=null)
			versionDiagramElement.addDiagramSpecificElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.PARENT_ELEMENT_ID, parentId);

	}
	
	private void handleDiagramElementFeatures(VersionDiagramElement versionDiagramElement, IDiagramModelArchimateObject diagramElement, String parentId) {
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_OBJECT_ID, diagramElement.getArchimateElement().getId());
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FILL_COLOUR, diagramElement.getFillColor());
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT, diagramElement.getFont());
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR, diagramElement.getFontColor());
		
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_X, Integer.toString(diagramElement.getBounds().getX()));
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_Y, Integer.toString(diagramElement.getBounds().getY()));
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_WIDTH, Integer.toString(diagramElement.getBounds().getWidth()));
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_HEIGHT, Integer.toString(diagramElement.getBounds().getHeight()));
	
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT, Integer.toString(diagramElement.getTextAlignment()));
		versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION, Integer.toString(diagramElement.getTextPosition()));
		
		if(parentId !=null)
			versionDiagramElement.addDiagramElementFeature(diagramElement.getId(), VersionDiagramFeatureAttribute.PARENT_ELEMENT_ID, parentId);

	}
	
	private void handleDiagramRelationshipFeatures(boolean sourceIsNote, String sourceId, VersionDiagramElement versionDiagramElement, IDiagramModelArchimateConnection diagramElement) {
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_OBJECT_ID, diagramElement.getRelationship().getId());
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT, diagramElement.getFont());
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR, diagramElement.getFontColor());
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT, Integer.toString(diagramElement.getTextAlignment()));
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION, Integer.toString(diagramElement.getTextPosition()));
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.CONNECTION_LINE_COLOUR, diagramElement.getLineColor());
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.CONNECTION_LINE_WIDTH, Integer.toString(diagramElement.getLineWidth()));
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_TYPE, diagramElement.eClass().getName());
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_TARGET, diagramElement.getTarget().getId());
		
		this.handleDiagramRelationshipBendPoints(sourceIsNote, sourceId, versionDiagramElement, diagramElement);
		
	}
	
	private void handleDiagramNoteRelationshipFeatures(boolean sourceIsNote, String sourceId, VersionDiagramElement versionDiagramElement, IDiagramModelConnection diagramElement) {
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT, diagramElement.getFont());
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR, diagramElement.getFontColor());
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT, Integer.toString(diagramElement.getTextAlignment()));
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION, Integer.toString(diagramElement.getTextPosition()));
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.CONNECTION_LINE_COLOUR, diagramElement.getLineColor());
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.CONNECTION_LINE_WIDTH, Integer.toString(diagramElement.getLineWidth()));
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_TYPE, diagramElement.eClass().getName());
		//TODO: need to deal with names, documentation & properties for note relationships at some stage too
		
		
		
		String targetId = diagramElement.getTarget().getId();
//		if(diagramElement.getTarget() instanceof IDiagramModelArchimateObject)
//			targetId = ((IDiagramModelArchimateObject)diagramElement.getTarget()).getArchimateElement().getId();
//		else
//			targetId = diagramElement.getTarget().getId();
		
		versionDiagramElement.addDiagramRelationshipFeature(sourceIsNote, sourceId, diagramElement.getId(), VersionDiagramFeatureAttribute.NOTE_CONNECTION_TARGET_ID, targetId);
		
		this.handleDiagramRelationshipBendPoints(sourceIsNote, sourceId, versionDiagramElement, diagramElement);
		
	}
	
	private void handleDiagramRelationshipBendPoints(boolean sourceIsNote, String sourceId, VersionDiagramElement versionDiagramElement, IDiagramModelConnection diagramElement) {
		EList<IDiagramModelBendpoint> bendpoints = diagramElement.getBendpoints();
		String relationshipId = diagramElement.getId();
//		if(diagramElement instanceof IDiagramModelArchimateConnection)
//			relationshipId = ((IDiagramModelArchimateConnection) diagramElement).getRelationship().getId();
//		else
//			relationshipId = diagramElement.getId();
		
		for(IDiagramModelBendpoint bendpoint: bendpoints) {
			versionDiagramElement.addDiagramRelationshipBendpointFeatures(sourceIsNote, sourceId, relationshipId, bendpoint.getStartX(), bendpoint.getStartY(), bendpoint.getEndX(), bendpoint.getEndY());
		}
	}
	
	public void addVersionElementToModel(IVersionElement element, FolderType elementType) {
		switch(elementType.getValue()) {
			case FolderType.BUSINESS_VALUE:
				businessElements.put(element.getId(), element);
				break;
			case FolderType.APPLICATION_VALUE:
				applicationElements.put(element.getId(), element);
				break;	
			case FolderType.TECHNOLOGY_VALUE:
				technologyElements.put(element.getId(), element);
				break;	
			case FolderType.MOTIVATION_VALUE:
				motivationElements.put(element.getId(), element);
				break;	
			case FolderType.IMPLEMENTATION_MIGRATION_VALUE:
				implementationElements.put(element.getId(), element);
				break;	
			case FolderType.CONNECTORS_VALUE:
				connectorElements.put(element.getId(), element);
				break;	
			case FolderType.RELATIONS_VALUE:
				relationshipElements.put(element.getId(), element);
				break;	
			case FolderType.DERIVED_VALUE:
				derivedRelationshipElements.put(element.getId(), element);
				break;	
			case FolderType.DIAGRAMS_VALUE:
				diagramElements.put(element.getId(), element);
				break;	
		}
		
	}
	
//    private List<IVersionElement> createVersionObjects(IFolderPath folderPath, IFolder folder) {
//    	List<IVersionElement> folderListElements = new ArrayList<IVersionElement>();
//    	
//        List<EObject> list = folder.getElements();
//        //getElements(folder, list);
//        for(EObject eObject : list) {
//            if(eObject instanceof IArchimateElement) {
//                IArchimateElement element = (IArchimateElement)eObject;
//                folderListElements.add(new VersionElement(element, folderPath));
//            }
//        }
//        
//        return folderListElements;
//    }

	@Override
	public File getRepoLocation() {
		return this.versionRepoDir;
	}

	@Override
	public File getWorkingDirLocation() {
		return this.versionWorkingDir;
	}

	@Override
	public File getDefaultRepoLocation() {
		return this.versionPrefs.DEFAULT_GIT_REPO_DIR;
	}

	@Override
	public File getDefaultWorkingDirLocation() {
		return this.versionPrefs.VC_WORKING_DIR;
	}

	@Override
	public String getModelName() {
		return this.archiModel.getName();
	}

	@Override
	public String getModelId() {
		return this.archiModel.getId();
	}

	@Override
	public void setRepoLocation(File repoLocation) {
		this.versionRepoDir = repoLocation;
		
	}

	@Override
	public void setWorkingDirLocation(File workingDirLocation) {
		this.versionWorkingDir = workingDirLocation;
		this.setArchiModelProperty(WORKING_DIR_PROPERTY_NAME, workingDirLocation.toString());
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
		this.setArchiModelProperty(MODEL_REPO_ID_PROPERTY_NAME, repositoryId);

	}

	public String getRepositoryDescription() {
		return repositoryDescription;
	}

	public void setRepositoryDescription(String repositoryDescription) {
		this.repositoryDescription = repositoryDescription;
		this.setArchiModelProperty(MODEL_REPO_DESCRIPTION_PROPERTY_NAME, repositoryDescription);
		
	}

	@Override
	public String getModelUserName() {
		return this.modelUser;
		
	}

	@Override
	public void setModelUserName(String modelUserName) {
		this.modelUser = modelUserName;
		this.setArchiModelProperty(MODEL_USER_PROPERTY_NAME, modelUserName);
		
	}

	@Override
	public String getModelUserEmail() {
		return this.modelUser;
		
	}

	@Override
	public void setModelUserEmail(String modelUserEmail) {
		this.modelUserEmail = modelUserEmail;
		this.setArchiModelProperty(MODEL_USER_EMAIL_NAME, modelUserEmail);
		
	}

	public String getRepoBranch() {
		return repoBranch;
	}

	public void setRepoBranch(String repoBranch) {
		this.repoBranch = repoBranch;
		this.setArchiModelProperty(MODEL_REPO_BRANCH_PROPERTY_NAME, repoBranch);
				
	}
	
	public String getRemoteRepoLocation() {
		return this.remoteRepoLocation;
	}
	
	public void setRemoteRepoLocation(String remoteRepoLocation) {
		this.remoteRepoLocation = remoteRepoLocation;
		this.setArchiModelProperty(REMOTE_REPO_LOCATION_PROPERTY_NAME, remoteRepoLocation);
	}
	
	public String getRemoteUser() {
		return this.remoteUser;
	}
	
	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
		this.setArchiModelProperty(REMOTE_REPO_USER_PROPERTY_NAME, remoteUser);
	}

	private void setArchiModelProperty(String propertyName, String propertyValue) {
		Map<String, IProperty> existingProps = ArchiUtils.getPropertiesMap(archiModel.getProperties());
		if(existingProps.containsKey(propertyName)) {
			IProperty prop = existingProps.get(propertyName);
			prop.setValue(propertyValue);
		}
		else {
			ArchimateFactory f = new ArchimateFactory();
			IProperty repoProp = f.createProperty();
			repoProp.setKey(propertyName);
			repoProp.setValue(propertyValue);
			this.archiModel.getProperties().add(repoProp);
		}		
	}

    private IVersionElement getModelElement(String id) {
    	if(this.businessElements.containsKey(id))
    		return businessElements.get(id);
    	else if(this.applicationElements.containsKey(id))
    		return applicationElements.get(id);    	
    	else if(this.technologyElements.containsKey(id))
    		return technologyElements.get(id);
    	else if(this.motivationElements.containsKey(id))
    		return motivationElements.get(id);
    	else if(this.implementationElements.containsKey(id))
    		return implementationElements.get(id);
    	else if(this.connectorElements.containsKey(id))
    		return connectorElements.get(id);
    	else if(this.relationshipElements.containsKey(id))
    		return relationshipElements.get(id);
    	else if(this.derivedRelationshipElements.containsKey(id))
    		return derivedRelationshipElements.get(id);
    	else
    		return null;
    }

}
