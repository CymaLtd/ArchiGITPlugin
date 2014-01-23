package nz.co.cyma.integrations.archigitsync.model;

import java.util.Map;

import org.eclipse.emf.common.util.EList;

import uk.ac.bolton.archimate.model.IProperty;

public interface IVersionRelationshipElement extends IVersionElement {
	public IVersionElement getSourceElement();
	public IVersionElement getTargetElement();
}
