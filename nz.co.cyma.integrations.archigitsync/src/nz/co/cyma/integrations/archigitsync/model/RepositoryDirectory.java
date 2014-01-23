package nz.co.cyma.integrations.archigitsync.model;

import java.io.File;

public enum RepositoryDirectory {
	//the order is important here, objects before relationships, before diagrams - the importer relies on this
	BUSINESS_LAYER ("business_layer"),
	APPLICATION_LAYER ("application_layer"),
	TECHNOLOGY_LAYER ("technology_layer"),
	MOTIVATION_LAYER ("motivation_extension"),
	IMPLEMENTATION_LAYER ("implementation_extension"),
	CONNECTORS ("connectors"),
	RELATIONSHIPS ("relationships"),
	DERIVED_RELATIONSHIPS ("derived_relationships"),
	DIAGRAMS ("diagrams");
	
	
	private final String directoryName;
	
	RepositoryDirectory(String directoryName) {
		this.directoryName = directoryName;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	public File getDirectory() {
		return new File(directoryName);
	}
}
