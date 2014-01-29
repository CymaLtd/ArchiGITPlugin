/**
 * 
 */
package nz.co.cyma.integrations.archigitsync.plugin;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * @author michael
 *
 */
public class VersioningException extends IOException {
	public VersioningException (String message) {
		
		super(message);
	}
	
	public VersioningException (String message, Throwable exception) {
		
		super(message, exception);
		

	}

}
