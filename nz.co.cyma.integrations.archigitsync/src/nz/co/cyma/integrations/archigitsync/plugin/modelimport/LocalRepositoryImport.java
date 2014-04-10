package nz.co.cyma.integrations.archigitsync.plugin.modelimport;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.co.cyma.integrations.archigitsync.model.IVersionModelPropertyConstants;
import nz.co.cyma.integrations.archigitsync.model.RepositoryDirectory;
import nz.co.cyma.integrations.archigitsync.model.VersionDiagramFeatureAttribute;
import nz.co.cyma.integrations.archigitsync.model.VersionElementAttribute;
import nz.co.cyma.integrations.archigitsync.model.VersionRelationshipAttribute;
import nz.co.cyma.integrations.archigitsync.model.impl.FolderPath;
import nz.co.cyma.integrations.archigitsync.plugin.ArchiUtils;
import nz.co.cyma.integrations.archigitsync.plugin.DialogCancelException;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.RemoteRepositoryDialog;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.NewModelDialog;
import nz.co.cyma.integrations.archigitsync.plugin.dialog.NewRepositoryDialog;
import nz.co.cyma.integrations.archigitsync.plugin.git.GitWrapper;
import nz.co.cyma.integrations.archigitsync.plugin.yaml.YamlReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.model.IModelImporter;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelBendpoint;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.ITextContent;
import com.archimatetool.model.impl.ArchimateFactory;

/**
 * @author michael
 * Class called from the Archi menu that creates a new model from a previously versioned GIT model
 *
 */
public class LocalRepositoryImport implements IModelImporter {

	GitWrapper gitRepo = null;
	URI repoToClone = null;
	File workingDir = null;
	String repoUser = null;
	String repoPassword = null;
	String chosenBranch = "master";
	Map<String, Object> modelElements = new HashMap<String, Object> ();
	Map<String, IRelationship> modelRelationships = new HashMap<String, IRelationship>();
	
	@Override
	public void doImport() throws IOException {
		
		//create our new model and collect some info before checking out from the new repository
		IArchimateModel model;
		try {
			model = IArchimateFactory.eINSTANCE.createArchimateModel();
			model.setDefaults();
			model.setName("Versioned Import");
			
			//find out where the local repository is
			askCloneInfo(model);
			
			//clone the repository
			GitWrapper.cloneArchiRepository(repoToClone, workingDir, repoUser, repoPassword);
			
			//get the model repo file from repository, if we can't find it, throw an error because it means
			//this git repo is not an archi one
			gitRepo = new GitWrapper(workingDir);
			gitRepo.getExistingGitRepo();
			gitRepo.checkoutModelFileFromMaster(IVersionModelPropertyConstants.MODEL_FILE_NAME+".yml");
			//TODO check for existance of repo file
			
			//now ask for info that will allow the model we are about to import to continue to be versioned
			//this will ask which branch contains the model we are importing
			boolean saveToNewBranch = this.askModelUserInfo(model);
			if (saveToNewBranch) {
				gitRepo.createAndCheckoutBranchFromExistingBranch(ArchiUtils.getPropertiesMap(model.getProperties()).get(IVersionModelPropertyConstants.MODEL_REPO_BRANCH_PROPERTY_NAME).getValue(), this.chosenBranch);
			}
			else {
				gitRepo.checkoutRemoteBranch(chosenBranch);
			}
			
			
			gitRepo.close();
		} catch (DialogCancelException e) {
			if(this.gitRepo!=null)
				gitRepo.close();
			return;
		}
		
		//now read in the repository info and set the relevant properties in the model
		this.importRepositoryInfo(workingDir, model);
		this.importBranchInfo(workingDir, model);
		
		//now try to import the objects into the repository
		this.importObjects(model);
		
		
		//lastly if the branch to save to is not the one we imported from, we need to create a new branch and put the model on there

		
		
		//finally open the model in the editor and save it
		IEditorModelManager.INSTANCE.openModel(model);
		IEditorModelManager.INSTANCE.saveModel(model);
		
		
	}
	
	private void importRepositoryInfo(File workingDir, IArchimateModel model) throws IOException {
		Map repoInfo = YamlReader.readVersionObject(new File(workingDir.toString() + File.separatorChar + IVersionModelPropertyConstants.MODEL_FILE_NAME + ".yml"));
		
		this.createModelProperty(model, IVersionModelPropertyConstants.MODEL_REPO_ID_PROPERTY_NAME, (String) repoInfo.get(IVersionModelPropertyConstants.MODEL_REPO_ID_PROPERTY_NAME));
		this.createModelProperty(model, IVersionModelPropertyConstants.MODEL_REPO_DESCRIPTION_PROPERTY_NAME, (String) repoInfo.get(IVersionModelPropertyConstants.MODEL_REPO_DESCRIPTION_PROPERTY_NAME));
	}
	
	private void importBranchInfo(File workingDir, IArchimateModel model) {
		
		Map repoInfo;
		try {
			repoInfo = YamlReader.readVersionObject(new File(workingDir.toString() + File.separatorChar + IVersionModelPropertyConstants.BRANCH_FILE_NAME + ".yml"));
		} catch (IOException e) {
			//the file might not be there, so if we get an error just exit
			return;
		}
		
		model.setName((String) repoInfo.get(IVersionModelPropertyConstants.BRANCH_NAME_PROPERTY_NAME));
		model.setPurpose((String) repoInfo.get(IVersionModelPropertyConstants.BRANCH_DESCRIPTION_PROPERTY_NAME));
	}

    
    private void askCloneInfo(IArchimateModel model) throws DialogCancelException {
    	//first ask for the working directory
    	workingDir = this.askSaveDirectory();
    	
    	//then the clone info
    	RemoteRepositoryDialog dialog = new RemoteRepositoryDialog(Display.getCurrent().getActiveShell());
    	dialog.create();
    	int returnCode = dialog.open();
    	if(returnCode == RemoteRepositoryDialog.CANCEL)
    		throw new DialogCancelException("User cancelled at remote repository dialog");
    	
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
    	
    	this.createModelProperty(model, IVersionModelPropertyConstants.WORKING_DIR_PROPERTY_NAME, workingDir.toString());
    	this.createModelProperty(model, IVersionModelPropertyConstants.REMOTE_REPO_LOCATION_PROPERTY_NAME, repoToClone.toString());
    	this.createModelProperty(model, IVersionModelPropertyConstants.REMOTE_REPO_USER_PROPERTY_NAME, repoUser);
    }
    
    private File askSaveDirectory() throws DialogCancelException {
        DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        //dialog.setFilterPath(this.versionModel.getModelId().toString());
        dialog.setText("GIT repository working directory location");
        //dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD, "*.*" } ); //$NON-NLS-1$
        String path = dialog.open();
        if(path == null) {
            throw new DialogCancelException("User cancelled working directory choice");
        }
        
        
        File file = new File(path);
        
        return file;
    }
    
    private boolean askModelUserInfo(IArchimateModel model) throws DialogCancelException {
    	NewModelDialog dialog = new NewModelDialog(Display.getCurrent().getActiveShell(), true, true);
    	dialog.setBranchList(gitRepo.getBranchList());
    	dialog.create();
    	int returnCode = dialog.open();
    	
    	if(returnCode == NewModelDialog.CANCEL)
    		throw new DialogCancelException("User cancelled at model user info dialog");
    	
    	String remoteBranch = dialog.getBranchToSaveTo();
    	this.createModelProperty(model, IVersionModelPropertyConstants.MODEL_USER_PROPERTY_NAME, dialog.getModelUser());
    	this.createModelProperty(model, IVersionModelPropertyConstants.MODEL_USER_EMAIL_NAME, dialog.getModelUserEmail());
    	this.createModelProperty(model, IVersionModelPropertyConstants.MODEL_REPO_BRANCH_PROPERTY_NAME, remoteBranch.substring(remoteBranch.lastIndexOf('/')+1));

    	chosenBranch = dialog.getChosenBranchToImportFrom();
    	
    	if(remoteBranch.equals(chosenBranch))
    		return false;
    	else
    		return true;
    }

    private void importObjects(IArchimateModel model) throws IOException {
    	Map businessObjects = null;
    	Map applicationObjects = null;
    	Map technologyObjects = null;
    	Map motivationObjects = null;
    	Map implementationObjects = null;
    	Map connectorObjects = null;
    	Map relationshipObjects = null;
    	Map derivedRelationshipObjects = null;
    	Map diagramObjects = null;
    	
    	for(RepositoryDirectory dirName: RepositoryDirectory.values()) {
    		File dir = new File(this.workingDir.toString() + File.separatorChar + dirName.getDirectoryName());

    		switch(dirName) {
    			case BUSINESS_LAYER:
    				businessObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelObjects(model, businessObjects);
    				break;
    			case APPLICATION_LAYER:
    				applicationObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelObjects(model, applicationObjects);
    				break;
    			case TECHNOLOGY_LAYER:
    				technologyObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelObjects(model, technologyObjects);
    				break;
    			case MOTIVATION_LAYER:
    				motivationObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelObjects(model, motivationObjects);
    				break;
    			case IMPLEMENTATION_LAYER:
    				implementationObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelObjects(model, implementationObjects);
    				break;
    			case CONNECTORS:
    				connectorObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelObjects(model, connectorObjects);
    				break;
    			case RELATIONSHIPS:
    				relationshipObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelRelationships(model, relationshipObjects);
    				break;
    			case DERIVED_RELATIONSHIPS:
    				derivedRelationshipObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelRelationships(model, derivedRelationshipObjects);
    				break;
    			case DIAGRAMS:
    				diagramObjects = YamlReader.readDirectoryVersionObjects(dir);
    				this.createModelDiagrams(model, diagramObjects);
    				break;
    		}
    	}
    }
    
    private void createModelObjects(IArchimateModel model, Map objects) {
    	for(Object objectKey: objects.keySet()) {
    		String elementId = (String) objectKey;
    		Map objectFeatureMap = (Map) objects.get(elementId);
    		
    		IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create((EClass)IArchimatePackage.eINSTANCE.getEClassifier((String)objectFeatureMap.get(VersionElementAttribute.ELEMENT_TYPE.getKeyName())));
    		element.setId((String)objectFeatureMap.get(VersionElementAttribute.ID.getKeyName()));
    		element.setName((String)objectFeatureMap.get(VersionElementAttribute.NAME.getKeyName()));
    		element.setDocumentation((String)objectFeatureMap.get(VersionElementAttribute.DOCUMENTATION.getKeyName()));
    		
    		List elementProperties = (ArrayList)objectFeatureMap.get(VersionElementAttribute.PROPERTIES.getKeyName());
    		element.getProperties().addAll(elementProperties);
    		
    		String folderString = (String)objectFeatureMap.get(VersionElementAttribute.FOLDER_PATH.getKeyName());
    		IFolder typeFolder = model.getDefaultFolderForElement(element);
    		
    		//put objects in folders if needs be
    		handleFolders(folderString, typeFolder, element);	
    		
    		//put the element into a map so we can reference it
    		this.modelElements.put(element.getId(), element);
    		
 
    	}

    }
    
    
    private void createModelRelationships(IArchimateModel model, Map objects) {
    	//it's optional, but easier to always create a derived relations folder
    	IFolder derivedRelations = model.addDerivedRelationsFolder();
    	
    	for(Object objectKey: objects.keySet()) {
    		String elementId = (String) objectKey;
    		Map objectFeatureMap = (Map) objects.get(elementId);
    		
    		IRelationship relationship = (IRelationship)IArchimateFactory.eINSTANCE.create((EClass)IArchimatePackage.eINSTANCE.getEClassifier((String)objectFeatureMap.get(VersionElementAttribute.ELEMENT_TYPE.getKeyName())));
    		relationship.setId((String)objectFeatureMap.get(VersionElementAttribute.ID.getKeyName()));
    		relationship.setName((String)objectFeatureMap.get(VersionElementAttribute.NAME.getKeyName()));
    		relationship.setDocumentation((String)objectFeatureMap.get(VersionElementAttribute.DOCUMENTATION.getKeyName()));
    		
    		List elementProperties = (ArrayList)objectFeatureMap.get(VersionElementAttribute.PROPERTIES.getKeyName());
    		relationship.getProperties().addAll(elementProperties);
    		
    		//relationships
    		IArchimateElement source = (IArchimateElement) modelElements.get(objectFeatureMap.get(VersionRelationshipAttribute.SOURCE_ELEMENT.getKeyName()));
    		relationship.setSource(source);
    		
       		IArchimateElement target = (IArchimateElement) modelElements.get(objectFeatureMap.get(VersionRelationshipAttribute.TARGET_ELEMENT.getKeyName()));
    		relationship.setTarget(target);
    		
    		String folderString = (String)objectFeatureMap.get(VersionElementAttribute.FOLDER_PATH.getKeyName());
    		IFolder typeFolder = model.getDefaultFolderForElement(relationship);
    		String relationType = (String)objectFeatureMap.get(VersionElementAttribute.TYPE.getKeyName());
    		if(relationType.equals("derived")) {
    			typeFolder = derivedRelations;
    		}
    		
    		//handle folders if needs be
    		handleFolders(folderString, typeFolder, relationship);	
    		
    		
    		modelRelationships.put(relationship.getId(), relationship);
    	}
    	
    	
    	//
    }
    
    private void createModelDiagrams(IArchimateModel model, Map objects) {
    	List diagramRefList = new ArrayList();
    	Map diagramModelMap = new HashMap();
    	
    	for(Object objectKey: objects.keySet()) {
    		String elementId = (String) objectKey;
    		Map objectFeatureMap = (Map) objects.get(elementId);
    		
    		IArchimateDiagramModel diagram = (IArchimateDiagramModel)IArchimateFactory.eINSTANCE.create((EClass)IArchimatePackage.eINSTANCE.getEClassifier((String)objectFeatureMap.get(VersionElementAttribute.ELEMENT_TYPE.getKeyName())));
    		diagram.setId((String)objectFeatureMap.get(VersionElementAttribute.ID.getKeyName()));
    		diagram.setName((String)objectFeatureMap.get(VersionElementAttribute.NAME.getKeyName()));
    		diagram.setDocumentation((String)objectFeatureMap.get(VersionElementAttribute.DOCUMENTATION.getKeyName()));
    		
    		List elementProperties = (ArrayList)objectFeatureMap.get(VersionElementAttribute.PROPERTIES.getKeyName());
    		diagram.getProperties().addAll(elementProperties);
    		
    		diagram.setConnectionRouterType(this.getSafeFeatureValue(objectFeatureMap, VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_ROUTER.getKeyName()));

    		
    		//first handle diagram elements
    		ArchimateFactory f = new ArchimateFactory();
    		Map diagramElements = (Map)objectFeatureMap.get(VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_MAP.getKeyName());
    		Map<String, Object> archiDiagramObjects = new HashMap();
    		
    		for(Object objectId: diagramElements.keySet()) {
    			Map diagramElement = (Map) diagramElements.get(objectId);
    			IDiagramModelArchimateObject archiDiagramObject = f.createDiagramModelArchimateObject();
    			
    			archiDiagramObject.setId((String) objectId);
    			archiDiagramObject.setArchimateElement((IArchimateElement)modelElements.get((String)diagramElement.get(VersionDiagramFeatureAttribute.ELEMENT_OBJECT_ID.getKeyName())));
    			archiDiagramObject.setBounds(
    					this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_X.getKeyName()), 
    					this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_Y.getKeyName()), 
    					this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_WIDTH.getKeyName()), 
    					this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_HEIGHT.getKeyName()));
 
    			archiDiagramObject.setFillColor((String)diagramElement.get(VersionDiagramFeatureAttribute.ELEMENT_FILL_COLOUR.getKeyName()));
    			archiDiagramObject.setFont((String)diagramElement.get(VersionDiagramFeatureAttribute.ELEMENT_FONT.getKeyName()));
    			archiDiagramObject.setFontColor((String)diagramElement.get(VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR.getKeyName()));
    			archiDiagramObject.setTextAlignment(this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT.getKeyName()));
    			archiDiagramObject.setTextPosition(this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION.getKeyName()));
    			archiDiagramObject.setType(this.getSafeFeatureValue(diagramElement, VersionDiagramFeatureAttribute.ELEMENT_REPRESENTATION_TYPE.getKeyName()));
    			
    			diagram.getChildren().add(archiDiagramObject);
    			archiDiagramObjects.put((String)objectId, archiDiagramObject);
    		}
    		


    		//Now handle diagram specific elements
    		Map diagramSpecificElements = (Map)objectFeatureMap.get(VersionDiagramFeatureAttribute.DIAGRAM_SPECIFIC_OBJECT_MAP.getKeyName());
    		Map<String, IDiagramModelObject> archiDiagramSpecificObjects = new HashMap();
    		
    		for(Object objectId: diagramSpecificElements.keySet()) {
    			Map diagramSpecificElement = (Map) diagramSpecificElements.get(objectId);
    			IDiagramModelObject archiDiagramSpecificObject = (IDiagramModelObject)IArchimateFactory.eINSTANCE.create((EClass)IArchimatePackage.eINSTANCE.getEClassifier((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_TYPE.getKeyName())));
    			archiDiagramSpecificObject.setId((String)objectId);
    			archiDiagramSpecificObject.setBounds(
    					this.getSafeFeatureValue(diagramSpecificElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_X.getKeyName()), 
    					this.getSafeFeatureValue(diagramSpecificElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_Y.getKeyName()), 
    					this.getSafeFeatureValue(diagramSpecificElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_WIDTH.getKeyName()), 
    					this.getSafeFeatureValue(diagramSpecificElement, VersionDiagramFeatureAttribute.ELEMENT_BOUNDS_HEIGHT.getKeyName()));
    			
    			archiDiagramSpecificObject.setFillColor((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.ELEMENT_FILL_COLOUR.getKeyName()));
    			archiDiagramSpecificObject.setFont((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.ELEMENT_FONT.getKeyName()));
    			archiDiagramSpecificObject.setFontColor((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR.getKeyName()));
    			archiDiagramSpecificObject.setTextAlignment(this.getSafeFeatureValue(diagramSpecificElement, VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT.getKeyName()));
    			//TODO something not right with text alignment
    			archiDiagramSpecificObject.setTextPosition(this.getSafeFeatureValue(diagramSpecificElement, VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION.getKeyName()));
    			archiDiagramSpecificObject.setName((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_NAME.getKeyName()));
    			
    			if(archiDiagramSpecificObject instanceof IDiagramModelGroup)
    				 ((IDocumentable) archiDiagramSpecificObject).setDocumentation((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_DOCUMENTATION.getKeyName()));
    			else if (archiDiagramSpecificObject instanceof IDiagramModelNote)
    				 ((ITextContent) archiDiagramSpecificObject).setContent((String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_DOCUMENTATION.getKeyName()));
    			//if it's a diagram reference we need to add it to a list so we can update it after we've gone through all the diagrams
    			else if (archiDiagramSpecificObject instanceof IDiagramModelReference)
    			{
    				Object[] info = new Object[2];
    				info[0] = (IDiagramModelReference) archiDiagramSpecificObject;
    				info[1] = (String)diagramSpecificElement.get(VersionDiagramFeatureAttribute.DIAGRAM_REFERENCE_ID.getKeyName());
    				diagramRefList.add(info);
    			}
    				
    			diagram.getChildren().add(archiDiagramSpecificObject);
    			archiDiagramObjects.put(archiDiagramSpecificObject.getId(), archiDiagramSpecificObject);
    		}
    		
    		
    		//run through a second time and add children elements to parents and create relationships for diagram elements
    		createParentsAndRelationships(diagramElements, archiDiagramObjects, f);
    		
    		//also for diagram specific elements
    		createParentsAndRelationships(diagramSpecificElements, archiDiagramObjects, f);
    		
    		String folderString = (String)objectFeatureMap.get(VersionElementAttribute.FOLDER_PATH.getKeyName());
    		IFolder typeFolder = model.getDefaultFolderForElement(diagram);
    		
    		//handle folders if needs be
    		handleFolders(folderString, typeFolder, diagram);	
    		
    		//put the diagram in a map because we need to find it for any diagram reference elements
    		diagramModelMap.put(diagram.getId(), diagram);
    		
    	}
    	
    	//The last thing we need to do is update any diagram references with the diagram they are referencing
    	Iterator i = diagramRefList.iterator();
    	while(i.hasNext()) {
    		Object info[] = (Object[]) i.next();
    		IDiagramModelReference ref = (IDiagramModelReference) info[0];
    		ref.setReferencedModel((IArchimateDiagramModel) diagramModelMap.get(info[1]));
    	}
    }
    
    
    private void createModelProperty(IArchimateModel model, String key, String value) {
		ArchimateFactory f = new ArchimateFactory();
		IProperty repoProp = f.createProperty();
		repoProp.setKey(key);
		repoProp.setValue(value);
		model.getProperties().add(repoProp);
    }
    
    private int getSafeFeatureValue(Map map, String key) {
    	Object value = map.get(key);
    	if(value==null)
    		return 0;
    	else
    		return new Integer((String)value).intValue();
    	
    }
    
    private void handleFolders(String folderString, IFolder typeFolder, EObject element) {
    	ArchimateFactory f = new ArchimateFactory();
    	
		if(folderString.equals("")) {		
			typeFolder.getElements().add(element);
		}
		else {
			FolderPath folderPath = new FolderPath(typeFolder.getType(), folderString);
			IFolder currentArchiFolder = null;
			IFolder parentFolder = typeFolder;
			
			for(String folder: folderPath.getPathAsArray()) {
				IFolder folderToAddTo = null;
							
				//check if current folder already exists or not
				EList<IFolder> folderList = parentFolder.getFolders();

				for(IFolder archiFolder: folderList) {
					if(archiFolder.getName().equals(folder))
						folderToAddTo = archiFolder;
				}
				
				if(folderToAddTo == null) {
    				folderToAddTo = f.createFolder();
    				folderToAddTo.setName(folder);
    				folderToAddTo.setType(typeFolder.getType());
    				parentFolder.getFolders().add(folderToAddTo);
				}
				
				parentFolder = folderToAddTo;
			}
			parentFolder.getElements().add(element);

		}
    }
    
    private void createDiagramRelationships(Map diagramElement, IDiagramModelObject sourceArchiDiagramObject, Map archiDiagramObjects, ArchimateFactory archimateFactory) {
		if(diagramElement.containsKey(VersionDiagramFeatureAttribute.SOURCE_RELATIONSHIP_IDS.getKeyName())) {  				
			Map sourceRelationships = (Map) diagramElement.get(VersionDiagramFeatureAttribute.SOURCE_RELATIONSHIP_IDS.getKeyName());
			for(Object relIdO: sourceRelationships.keySet()) {
				String relId = (String) relIdO;
				Map sourceRelationship = (Map) sourceRelationships.get(relId);
				
				IDiagramModelConnection archiDiagramRelationship = (IDiagramModelConnection)IArchimateFactory.eINSTANCE.create((EClass)IArchimatePackage.eINSTANCE.getEClassifier((String)sourceRelationship.get(VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_TYPE.getKeyName())));
				//IDiagramModelArchimateConnection archiDiagramRelationship = f.createDiagramModelArchimateConnection();  
				
				if(archiDiagramRelationship instanceof IDiagramModelArchimateConnection) {
    				IRelationship archiRelationship = modelRelationships.get((String)sourceRelationship.get(VersionDiagramFeatureAttribute.ELEMENT_OBJECT_ID.getKeyName()));
    	    		((IDiagramModelArchimateConnection)archiDiagramRelationship).setRelationship(archiRelationship);
				}
				
				archiDiagramRelationship.setId(relId);
	    		archiDiagramRelationship.setFont((String)sourceRelationship.get(VersionDiagramFeatureAttribute.ELEMENT_FONT.getKeyName()));
	    		archiDiagramRelationship.setFontColor((String)sourceRelationship.get(VersionDiagramFeatureAttribute.ELEMENT_FONT_COLOUR.getKeyName()));
	    		archiDiagramRelationship.setTextAlignment(this.getSafeFeatureValue(sourceRelationship, VersionDiagramFeatureAttribute.ELEMENT_TEXT_ALIGNMENT.getKeyName()));
	    		archiDiagramRelationship.setTextPosition(this.getSafeFeatureValue(sourceRelationship, VersionDiagramFeatureAttribute.ELEMENT_TEXT_POSITION.getKeyName()));
	    		archiDiagramRelationship.setLineColor((String)sourceRelationship.get(VersionDiagramFeatureAttribute.CONNECTION_LINE_COLOUR.getKeyName()));
	    		archiDiagramRelationship.setLineWidth(this.getSafeFeatureValue(sourceRelationship, VersionDiagramFeatureAttribute.CONNECTION_LINE_WIDTH.getKeyName()));
	    		archiDiagramRelationship.setSource(sourceArchiDiagramObject);
  	    		
	    		if(sourceRelationship.containsKey(VersionDiagramFeatureAttribute.BEND_POINT_LIST.getKeyName())) {
	    			List bendpoints = (List) sourceRelationship.get(VersionDiagramFeatureAttribute.BEND_POINT_LIST.getKeyName());
	    			for(Object bendpoint: bendpoints) {
	    				Map bendpointMap = (Map) bendpoint;
	    				IDiagramModelBendpoint archiBendpoint = archimateFactory.createDiagramModelBendpoint();
	    				archiBendpoint.setStartX(this.getSafeFeatureValue(bendpointMap, VersionDiagramFeatureAttribute.BEND_POINT_START_X.getKeyName()));
	    				archiBendpoint.setStartY(this.getSafeFeatureValue(bendpointMap, VersionDiagramFeatureAttribute.BEND_POINT_START_Y.getKeyName()));
	    				archiBendpoint.setEndX(this.getSafeFeatureValue(bendpointMap, VersionDiagramFeatureAttribute.BEND_POINT_END_X.getKeyName()));
	    				archiBendpoint.setEndY(this.getSafeFeatureValue(bendpointMap, VersionDiagramFeatureAttribute.BEND_POINT_END_Y.getKeyName()));
	    				archiDiagramRelationship.getBendpoints().add(archiBendpoint);
	    			}
	    		}		
	    		
	    		sourceArchiDiagramObject.getSourceConnections().add(archiDiagramRelationship);
	    		
	    	
	    		
	    		//need to add it to the target as well
	    		Object targetO = null;
	    		
	    		if(archiDiagramRelationship instanceof IDiagramModelArchimateConnection) {
	    			//IRelationship archiRelationship = ((IDiagramModelArchimateConnection) archiDiagramRelationship).getRelationship();
	    			targetO = archiDiagramObjects.get(sourceRelationship.get(VersionDiagramFeatureAttribute.DIAGRAM_CONNECTION_TARGET.getKeyName()));
	    		}
	    		else
	    			targetO = archiDiagramObjects.get(sourceRelationship.get(VersionDiagramFeatureAttribute.NOTE_CONNECTION_TARGET_ID.getKeyName()));
	    		
	    		//make sure we add the target to the diagram relationship
	    		archiDiagramRelationship.setTarget((IDiagramModelObject) targetO);
	    		
	    		if(targetO instanceof IDiagramModelArchimateObject) {
	    			IDiagramModelArchimateObject targetObject = (IDiagramModelArchimateObject) targetO;
    	    		targetObject.getTargetConnections().add(archiDiagramRelationship);
	    		}
	    		//otherwise the object must be a diagram specific object
	    		else {
	    			IDiagramModelObject targetObject = (IDiagramModelObject) targetO;
    	    		targetObject.getTargetConnections().add(archiDiagramRelationship);
	    		}
	    		
			}
			

		}
    }
    
    
    
    private void createParentsAndRelationships(Map objectMap, Map archiDiagramObjects, ArchimateFactory archimateFactory) {
		for(Object objectId: objectMap.keySet()) {
			Map diagramElement = (Map) objectMap.get(objectId);
			IDiagramModelObject thisDiagramElement = (IDiagramModelObject) archiDiagramObjects.get(objectId);
			
			String parentId = (String)diagramElement.get(VersionDiagramFeatureAttribute.PARENT_ELEMENT_ID.getKeyName());
			if(parentId!=null) {
				Object parentO = archiDiagramObjects.get(parentId);
				if(parentO instanceof IDiagramModelArchimateObject) {
					IDiagramModelArchimateObject parent = (IDiagramModelArchimateObject) parentO;
					parent.getChildren().add(thisDiagramElement);
				}
				//else it must be a group object
				else {
					IDiagramModelGroup parent = (IDiagramModelGroup) parentO;
					parent.getChildren().add(thisDiagramElement);
				}
				
				
			}
			
			createDiagramRelationships(diagramElement, thisDiagramElement, archiDiagramObjects, archimateFactory);
				
		}//for each diagram object
    }
    
}
