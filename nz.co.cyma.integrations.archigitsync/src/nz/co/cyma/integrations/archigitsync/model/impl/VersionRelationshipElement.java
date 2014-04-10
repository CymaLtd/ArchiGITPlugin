package nz.co.cyma.integrations.archigitsync.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IAccessRelationship;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.IRelationship;
import nz.co.cyma.integrations.archigitsync.model.IFolderPath;
import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.IVersionRelationshipElement;
import nz.co.cyma.integrations.archigitsync.model.VersionElementAttribute;
import nz.co.cyma.integrations.archigitsync.model.VersionRelationshipAttribute;

public class VersionRelationshipElement extends VersionElement implements IVersionRelationshipElement {
	IVersionElement sourceElement = null;
	IVersionElement targetElement = null;
	Map additionalAttributes = null;
	
	VersionRelationshipElement(IRelationship archiElement, IFolderPath folderPath, IVersionElement source, IVersionElement target) {
		super(archiElement, folderPath);	
		this.sourceElement = source;
		this.targetElement = target;
		this.additionalAttributes = new HashMap();
		
		this.versionFields.put("sourceElement", this.getSourceElement().getId());
		this.versionFields.put("targetElement", this.getTargetElement().getId());
		this.versionFields.put("additionalAttributes", additionalAttributes);
		
		
		if(archiElement instanceof IAccessRelationship) {
			IAccessRelationship accessRel = (IAccessRelationship) archiElement;
			additionalAttributes.put(VersionRelationshipAttribute.ACCESS_TYPE.getKeyName(), Integer.toString(accessRel.getAccessType()));
		}
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
	
	public void setAdditionalAttribute(String attributeName, String attributeValue) {
		this.additionalAttributes.put(attributeName, attributeValue);
	}
	
	public Map getAdditionalAttributes() {
		return this.additionalAttributes;
	}

}
