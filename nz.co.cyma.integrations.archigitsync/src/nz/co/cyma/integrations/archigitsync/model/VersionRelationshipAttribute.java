package nz.co.cyma.integrations.archigitsync.model;

public enum VersionRelationshipAttribute {
	SOURCE_ELEMENT ("sourceElement"),
	TARGET_ELEMENT ("targetElement"),
	ADDITIONAL_ATTRIBUTES ("additionalAttributes"),
	ACCESS_TYPE ("accessType");
	
	private final String keyName;
	
	VersionRelationshipAttribute(String keyName) {
		this.keyName = keyName;
	}
	
	public String getKeyName() {
		return keyName;
	}
}
