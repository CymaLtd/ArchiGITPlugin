package nz.co.cyma.integrations.archigitsync.model;

import java.util.Map;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.IProperty;

public interface IVersionElement {
	public String getId();
	public String getName();
	public String getDocumentation();
	public EList<IProperty> getProperties();
	public Map getVersionProperties();
}
