package nz.co.cyma.integrations.archigitsync.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
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
		versionFields.put(VersionElementAttribute.DOCUMENTATION.getKeyName(), this.normalise(this.archiElement.getDocumentation()));
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
        if(s == null||s.equals("")) {
            return ""; //$NON-NLS-1$
        }
        
        s = s.replaceAll("\r\n", "|"); //$NON-NLS-1$ //$NON-NLS-2$
        
        //strip out double quotes from start and finish of the string due to previous error, should be unncessary in future
        char [] stringValue = s.toCharArray();
        boolean startQuoteMarker = (stringValue[0]=='"');
        String strippedString= "";
        for(int i = 0; i<stringValue.length; i++) {
        	//if a double quote at the start, look to the next character to see if it is the same
        	if (startQuoteMarker) {
        		startQuoteMarker = (stringValue[i+1]=='"');
        	}
        	else {
        		strippedString = strippedString + stringValue[i];
        		
        		//now check to see if the next character is a double quote, if it is, check if the rest are the same and finish if they are
        		if (i+1<stringValue.length && stringValue[i+1]=='"') {
        			boolean isQuote = true;
        			for (int k = i+1; k<stringValue.length; k++) {
        				if(stringValue[k]!='"')
        					isQuote = false;
        			}//for each subsequent character after the double quote
        			
        			//if the isQuote boolean hasn't been reset then all the subsequent characters are double quotes and we are finished
        			if (isQuote)
            			return strippedString;
        		}
        		
        		
        	}
        		
        }
        //s = "\"" + s + "\""; //$NON-NLS-1$ //$NON-NLS-2$
        
        return strippedString;
    }
    
    public Map getVersionProperties() {
    	return this.versionFields;
    }

}
