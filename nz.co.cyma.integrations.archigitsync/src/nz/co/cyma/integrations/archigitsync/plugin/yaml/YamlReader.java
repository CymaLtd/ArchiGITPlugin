package nz.co.cyma.integrations.archigitsync.plugin.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import nz.co.cyma.integrations.archigitsync.model.VersionElementAttribute;

import org.yaml.snakeyaml.Yaml;

public class YamlReader {
	public static Map<String,Object> readVersionObject(File location) throws IOException {
		InputStream input = new FileInputStream(location);
	    Yaml yaml = new Yaml();
	    @SuppressWarnings("unchecked")
		Map<String,Object> data = (Map<String,Object>) yaml.load(input);
	    return data;
	}

	public static Map<String,Map<String,Object>> readDirectoryVersionObjects(File location) throws IOException {
		Map<String,Map<String,Object>> versionObjects = new HashMap<String,Map<String,Object>>();
		
		if(location.isDirectory()) {
			for(File versionObjectFile: location.listFiles()) {
				InputStream input = new FileInputStream(versionObjectFile);
			    Yaml yaml = new Yaml();
			    @SuppressWarnings("unchecked")
				Map<String,Object> data = (Map<String,Object>) yaml.load(input);
			    versionObjects.put((String) data.get(VersionElementAttribute.ID.getKeyName()), data);
			}
		}
		return versionObjects;
	}
	
}
 