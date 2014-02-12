package nz.co.cyma.integrations.archigitsync.plugin.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import nz.co.cyma.integrations.archigitsync.model.IVersionElement;

public class YamlWriter {
	public static void writeVersionedYamlObjects(File location, List<IVersionElement> versionList) throws IOException {
		Yaml yaml = new Yaml();
		
		for(IVersionElement versionEl: versionList) {
			String versionFile = location.toString() + File.separatorChar + versionEl.getId() + ".yml";
			FileWriter sw = new FileWriter(versionFile);
			yaml.dump(versionEl.getVersionProperties(), sw);
		}
	}
	
	public static void writeModelFile(File modelFile, Map modelContent) throws IOException {
		Yaml yaml = new Yaml();
		
		FileWriter sw = new FileWriter(modelFile);
		yaml.dump(modelContent, sw);

	}	
	
	public static void writeBranchFile(File branchFile, Map branchContent) throws IOException {
		Yaml yaml = new Yaml();
		
		FileWriter sw = new FileWriter(branchFile);
		yaml.dump(branchContent, sw);

	}	

}
