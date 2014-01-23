package nz.co.cyma.integrations.archigitsync.model;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import uk.ac.bolton.archimate.model.IProperty;

public interface IVersionDiagramElement extends IVersionElement {
	public List<IVersionElement> getDiagramElements();
	public List<IVersionElement> getDiagramRelationships();
}
