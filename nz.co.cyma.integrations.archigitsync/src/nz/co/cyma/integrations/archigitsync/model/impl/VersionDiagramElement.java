package nz.co.cyma.integrations.archigitsync.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.IRelationship;
import nz.co.cyma.integrations.archigitsync.model.VersionDiagramFeatureAttribute;
import nz.co.cyma.integrations.archigitsync.model.IFolderPath;
import nz.co.cyma.integrations.archigitsync.model.IVersionDiagramElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionRelationshipElement;
import nz.co.cyma.integrations.archigitsync.model.VersionElementAttribute;

public class VersionDiagramElement implements IVersionDiagramElement {
	private IDiagramModel archiElement = null;
	

	Map<String, IVersionElement> diagramElements = new <String, IVersionElement> LinkedHashMap();

	//Map<String, IVersionElement> relationshipElements = new <String, IVersionElement> HashMap();
	Map<String, Map> diagramElementFeatures = new <String, Map> LinkedHashMap();
	Map<String, Map> diagramSpecificFeatures = new <String, Map> LinkedHashMap();
	Map<String, Map> diagramRelationshipFeatures = new <String, Map> LinkedHashMap();
	Map<String, String> diagramFeatures = new <String, String> LinkedHashMap();
	protected Map versionFields = new HashMap();
	
	VersionDiagramElement(IDiagramModel archiElement, IFolderPath folderPath) {
		this.archiElement = archiElement;
		
		versionFields.put(VersionElementAttribute.ID.getKeyName(), this.archiElement.getId());
		versionFields.put(VersionElementAttribute.NAME.getKeyName(), this.archiElement.getName());
		versionFields.put(VersionElementAttribute.DOCUMENTATION.getKeyName(), this.archiElement.getDocumentation());
		versionFields.put(VersionElementAttribute.PROPERTIES.getKeyName(), this.archiElement.getProperties());
		versionFields.put(VersionElementAttribute.ELEMENT_TYPE.getKeyName(), archiElement.eClass().getName());
		versionFields.put(VersionElementAttribute.TYPE.getKeyName(), folderPath.getFolderPathTypeAsString());
		versionFields.put(VersionElementAttribute.FOLDER_PATH.getKeyName(), folderPath.getPathAsString());
		
	}
	
	

	public Map getVersionProperties() {
		versionFields.put(VersionDiagramFeatureAttribute.DIAGRAM_OBJECT_MAP.getKeyName(), diagramElementFeatures);
		//versionFields.put("diagramRelationships", diagramRelationshipFeatures);
		versionFields.put(VersionDiagramFeatureAttribute.DIAGRAM_FEATURE_MAP.getKeyName(), diagramFeatures);
		versionFields.put(VersionDiagramFeatureAttribute.DIAGRAM_SPECIFIC_OBJECT_MAP.getKeyName(), diagramSpecificFeatures);

		return versionFields;
    }


	@Override
	public List<IVersionElement> getDiagramElements() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<IVersionElement> getDiagramRelationships() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addDiagramFeature(VersionDiagramFeatureAttribute featureType, String value) {
		diagramFeatures.put(featureType.getKeyName(), value);
	}

	public void addDiagramElementFeature(String elementId, VersionDiagramFeatureAttribute featureType, String value) {

		if(this.diagramElementFeatures.containsKey(elementId)) {
			Map<String, String> elementFeatures = diagramElementFeatures.get(elementId);
			elementFeatures.put(featureType.getKeyName(), value);
		}
		else {
			Map<String, String> elementFeatures = new <String, String> LinkedHashMap();
			elementFeatures.put(featureType.getKeyName(), value);
			this.diagramElementFeatures.put(elementId, elementFeatures);
		}
		
	}
	
	public void addDiagramSpecificElementFeature(String elementId, VersionDiagramFeatureAttribute featureType, String value) {

		if(this.diagramSpecificFeatures.containsKey(elementId)) {
			Map<String, String> elementFeatures = diagramSpecificFeatures.get(elementId);
			elementFeatures.put(featureType.getKeyName(), value);
		}
		else {
			Map<String, String> elementFeatures = new <String, String> LinkedHashMap();
			elementFeatures.put(featureType.getKeyName(), value);
			this.diagramSpecificFeatures.put(elementId, elementFeatures);
		}
		
	}
	
	public void addDiagramRelationshipFeature(boolean sourceIsNote, String sourceElementId, String relationshipId, VersionDiagramFeatureAttribute featureType, String value) {

		Map elementFeatures = null;
		if(sourceIsNote)
			elementFeatures = diagramSpecificFeatures.get(sourceElementId);
		else
			elementFeatures = this.diagramElementFeatures.get(sourceElementId);
		
		if(elementFeatures.containsKey(VersionDiagramFeatureAttribute.SOURCE_RELATIONSHIP_IDS.getKeyName())) {
			Map sourceRelationships = (Map) elementFeatures.get(VersionDiagramFeatureAttribute.SOURCE_RELATIONSHIP_IDS.getKeyName());
			
			if(sourceRelationships.containsKey(relationshipId)) {
				Map relationshipFeatures = (Map) sourceRelationships.get(relationshipId);	
				relationshipFeatures.put(featureType.getKeyName(), value);
				
			}
			else {
				Map <String, String> relationshipFeatures = new <String, String> LinkedHashMap();					
				relationshipFeatures.put(featureType.getKeyName(), value);	
				sourceRelationships.put(relationshipId, relationshipFeatures);
			}
			
		}
		else {
			
			Map sourceRelationships = new LinkedHashMap();
			Map relationshipFeatures = new LinkedHashMap();
			
			relationshipFeatures.put(featureType.getKeyName(), value);				
			sourceRelationships.put(relationshipId, relationshipFeatures);
			elementFeatures.put(VersionDiagramFeatureAttribute.SOURCE_RELATIONSHIP_IDS.getKeyName(), sourceRelationships);
			
		}
			
	}
	
	public void addDiagramRelationshipBendpointFeatures(boolean sourceIsNote, String sourceElementId, String relationshipId, int startx, int starty, int endx, int endy) {
		
		Map elementFeatures = null;
		if(sourceIsNote)
			elementFeatures = this.diagramSpecificFeatures.get(sourceElementId);
		else
			elementFeatures = this.diagramElementFeatures.get(sourceElementId);
		
		Map sourceRelationships = (Map) elementFeatures.get(VersionDiagramFeatureAttribute.SOURCE_RELATIONSHIP_IDS.getKeyName());
		Map relationshipFeatures = (Map) sourceRelationships.get(relationshipId);
		List bendpointList = null;
		if(relationshipFeatures.containsKey(VersionDiagramFeatureAttribute.BEND_POINT_LIST.getKeyName())) {
			bendpointList = (List) relationshipFeatures.get(VersionDiagramFeatureAttribute.BEND_POINT_LIST.getKeyName());
		}
		else {
			bendpointList = new ArrayList();
			relationshipFeatures.put(VersionDiagramFeatureAttribute.BEND_POINT_LIST.getKeyName(), bendpointList);
		}
		
		Map bendpointAtts = new HashMap();
		bendpointAtts.put(VersionDiagramFeatureAttribute.BEND_POINT_START_X.getKeyName(), Integer.toString(startx));
		bendpointAtts.put(VersionDiagramFeatureAttribute.BEND_POINT_START_Y.getKeyName(), Integer.toString(starty));
		bendpointAtts.put(VersionDiagramFeatureAttribute.BEND_POINT_END_X.getKeyName(), Integer.toString(endx));
		bendpointAtts.put(VersionDiagramFeatureAttribute.BEND_POINT_END_Y.getKeyName(), Integer.toString(endy));
		
		bendpointList.add(bendpointAtts);
	}


	@Override
	public String getId() {
		return (String)this.versionFields.get(VersionElementAttribute.ID.getKeyName());
	}



	@Override
	public String getName() {
		return (String)this.versionFields.get(VersionElementAttribute.NAME.getKeyName());
	}



	@Override
	public String getDocumentation() {
		return (String)this.versionFields.get(VersionElementAttribute.DOCUMENTATION.getKeyName());
	}



	@Override
	public EList<IProperty> getProperties() {
		return (EList)this.versionFields.get(VersionElementAttribute.PROPERTIES.getKeyName());
	}
}