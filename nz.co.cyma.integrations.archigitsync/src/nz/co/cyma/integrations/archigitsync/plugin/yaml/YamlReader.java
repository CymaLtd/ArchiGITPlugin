package nz.co.cyma.integrations.archigitsync.plugin.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import nz.co.cyma.integrations.archigitsync.model.VersionElementAttribute;

import org.yaml.snakeyaml.Yaml;

public class YamlReader {
	public static Map readVersionObject(File location) throws IOException {
		InputStream input = new FileInputStream(location);
	    Yaml yaml = new Yaml();
	    Map data = (Map) yaml.load(input);
	    return data;
	}

	public static Map readDirectoryVersionObjects(File location) throws IOException {
		Map versionObjects = new HashMap();
		
		if(location.isDirectory()) {
			for(File versionObjectFile: location.listFiles()) {
				InputStream input = new FileInputStream(versionObjectFile);
			    Yaml yaml = new Yaml();
			    Map data = (Map) yaml.load(input);
			    versionObjects.put(data.get(VersionElementAttribute.ID.getKeyName()), data);
			}
		}
		return versionObjects;
	}
	
}
 