package nz.co.cyma.integrations.archigitsync.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;

public class ArchiUtils {
	public static Map<String, IProperty> getPropertiesMap(EList<IProperty> archPropertyList) {
		Map<String, IProperty> propertiesMap = new HashMap<String, IProperty>();
		
		for(IProperty modelProperty: archPropertyList ) {
			propertiesMap.put(modelProperty.getKey(), modelProperty);
		}
		
		return propertiesMap;
	}
	
	public static Map<String, IFolder> getFolderMap(EList<IFolder> folders) {
		Map<String, IFolder> folderMap = new HashMap<String, IFolder>();
		for(IFolder folder: folders) {
			folderMap.put(folder.getName(), folder);
		}
		
		return folderMap;
	}

}
