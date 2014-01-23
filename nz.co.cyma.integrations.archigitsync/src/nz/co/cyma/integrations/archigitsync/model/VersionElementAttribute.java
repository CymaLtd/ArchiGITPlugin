package nz.co.cyma.integrations.archigitsync.model;

public enum VersionElementAttribute {
	ID ("id"),
	NAME ("name"),
	DOCUMENTATION ("documentation"),
	PROPERTIES ("properties"),
	ELEMENT_TYPE ("elementType"),
	TYPE ("type"),
	FOLDER_PATH ("folderPath");
	
	
	private final String keyName;
	
	VersionElementAttribute(String keyName) {
		this.keyName = keyName;
	}
	
	public String getKeyName() {
		return keyName;
	}
}
