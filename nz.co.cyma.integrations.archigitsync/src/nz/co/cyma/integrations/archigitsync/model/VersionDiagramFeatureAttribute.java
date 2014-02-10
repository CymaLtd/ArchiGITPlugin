package nz.co.cyma.integrations.archigitsync.model;

public enum VersionDiagramFeatureAttribute {
	DIAGRAM_CONNECTION_ROUTER ("connectionRouterType"),
	ELEMENT_FILL_COLOUR ("fillColour"),
	ELEMENT_FONT ("font"),
	ELEMENT_FONT_COLOUR ("fontColour"),
	ELEMENT_LINE_COLOUR ("ElementLineColour"),
	ELEMENT_BOUNDS_X ("boundsX"),
	ELEMENT_BOUNDS_Y ("boundsY"),
	ELEMENT_BOUNDS_WIDTH ("boundsWidth"),
	ELEMENT_BOUNDS_HEIGHT ("boundsHeight"),
	ELEMENT_TEXT_ALIGNMENT ("textAlignment"),
	ELEMENT_TEXT_POSITION ("textPosition"),
	ELEMENT_OBJECT_ID ("elementObjectId"),
	CONNECTION_LINE_WIDTH ("connectionWidth"),
	CONNECTION_LINE_COLOUR ("connectionColour"),
	SOURCE_RELATIONSHIP_IDS ("sourceRelationshipIds"),
	PARENT_ELEMENT_ID ("parentElementId"),
	DIAGRAM_OBJECT_NAME ("objectName"),
	DIAGRAM_OBJECT_TYPE ("objectType"),
	DIAGRAM_OBJECT_DOCUMENTATION ("objectDocumentation"),
	DIAGRAM_OBJECT_MAP ("diagramElements"),
	DIAGRAM_FEATURE_MAP ("diagramFeatures"),
	DIAGRAM_SPECIFIC_OBJECT_MAP ("diagramSpecificElements"),
	BEND_POINT_LIST ("bendpointList"),
	BEND_POINT_START_X ("bendpointStartX"),
	BEND_POINT_START_Y ("bendbointStartY"),
	BEND_POINT_END_X ("bendpointEndX"),
	BEND_POINT_END_Y ("bendpointEndY"),
	DIAGRAM_CONNECTION_TYPE ("diagramConnectionType"),
	DIAGRAM_CONNECTION_TARGET ("diagramConnectionTarget"),
	NOTE_CONNECTION_TARGET_ID ("noteConnectionTargetId"),
	DIAGRAM_REFERENCE_ID ("diagramReferenceId");
	
	private final String keyName;
	
	VersionDiagramFeatureAttribute(String keyName) {
		this.keyName = keyName;
	}
	
	public String getKeyName() {
		return keyName;
	}
}