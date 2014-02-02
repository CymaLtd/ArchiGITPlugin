package nz.co.cyma.integrations.archigitsync.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import uk.ac.bolton.archimate.model.FolderType;
import uk.ac.bolton.archimate.model.IArchimateElement;
import uk.ac.bolton.archimate.model.IProperties;
import uk.ac.bolton.archimate.model.IProperty;
import nz.co.cyma.integrations.archigitsync.model.IFolderPath;
import nz.co.cyma.integrations.archigitsync.model.IVersionElement;
import nz.co.cyma.integrations.archigitsync.model.VersionElementAttribute;

public class VersionElement implements IVersionElement {
	protected IArchimateElement archiElement = null;
	protected Map versionFields = new HashMap();
	
	VersionElement(IArchimateElement archiElement, IFolderPath folderPath) {
		//hold onto the original, but don't really use it at the moment
		this.archiElement = archiElement;
		
		
		versionFields.put(VersionElementAttribute.ID.getKeyName(), this.archiElement.getId());
		versionFields.put(VersionElementAttribute.NAME.getKeyName(), this.archiElement.getName());
		versionFields.put(VersionElementAttribute.DOCUMENTATION.getKeyName(), this.archiElement.getDocumentation());
		versionFields.put(VersionElementAttribute.PROPERTIES.getKeyName(), this.archiElement.getProperties());
		versionFields.put(VersionElementAttribute.ELEMENT_TYPE.getKeyName(), archiElement.eClass().getName());
		versionFields.put(VersionElementAttribute.TYPE.getKeyName(), folderPath.getFolderPathTypeAsString());
		versionFields.put(VersionElementAttribute.FOLDER_PATH.getKeyName(), folderPath.getPathAsString());
		
		
	}
	
	public String getId() {
		return (String)this.versionFields.get(VersionElementAttribute.ID.getKeyName());
	}
	
	public String getName() {
		return (String)this.versionFields.get(VersionElementAttribute.NAME.getKeyName());
	}
	
	public String getDocumentation() {
		return (String)this.versionFields.get(VersionElementAttribute.DOCUMENTATION.getKeyName());
	}
	
	public EList<IProperty> getProperties() {
		return (EList)this.versionFields.get(VersionElementAttribute.PROPERTIES.getKeyName());
	}
	
    private String normalise(String s) {
        if(s == null) {
            return ""; //$NON-NLS-1$
        }
        
        s = s.replaceAll("\r\n", " "); //$NON-NLS-1$ //$NON-NLS-2$
        s = "\"" + s + "\""; //$NON-NLS-1$ //$NON-NLS-2$
        
        return s;
    }
    
    public Map getVersionProperties() {
    	return this.versionFields;
    }

}
