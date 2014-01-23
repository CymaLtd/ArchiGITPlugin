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
		
		MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
		dialog.setText("Versioning Issue");
		dialog.setMessage(message);
	}
	
	public VersioningException (String message, Throwable exception) {
		
		MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
		dialog.setText("Versioning Issue");
		dialog.setMessage(message);

		//throw new IOException(message, exception);
		

	}

}
