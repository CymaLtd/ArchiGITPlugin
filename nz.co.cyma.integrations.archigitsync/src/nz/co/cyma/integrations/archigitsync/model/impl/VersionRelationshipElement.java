package nz.co.cyma.integrations.archigitsync.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import uk.ac.bolton.archimate.model.FolderType;
import uk.ac.bolton.archimate.model.IArchimateElement;
import uk.ac.bolton.archimate.model.IProperties;
import uk.ac.bolton.archimate.model.IProperty;
import uk.ac.bolton.archimate.model.IRelationship;
import nz.co.cyma.integrations.archigitsync.model.IFolderPath;
import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionRelationshipElement;

public class VersionRelationshipElement extends VersionElement implements IVersionRelationshipElement {
	IVersionElement sourceElement = null;
	IVersionElement targetElement = null;
	
	VersionRelationshipElement(IRelationship archiElement, IFolderPath folderPath, IVersionElement source, IVersionElement target) {
		super(archiElement, folderPath);	
		this.sourceElement = source;
		this.targetElement = target;
		
		this.versionFields.put("sourceElement", this.getSourceElement().getId());
		this.versionFields.put("targetElement", this.getTargetElement().getId());
	}
	
	
    @Override
	public IVersionElement getSourceElement() {
		// TODO Auto-generated method stub
		return sourceElement;
	}



	@Override
	public IVersionElement getTargetElement() {
		// TODO Auto-generated method stub
		return targetElement;
	}



	public Map getVersionProperties() {
    	return this.versionFields;
    }

}
