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
 * Generated when a user cancels out of one of the dialogs
 */
public class DialogCancelException extends Exception {
	public DialogCancelException (String message) {
		
		super(message);
	}
	
	public DialogCancelException (String message, Throwable exception) {
		
		super(message, exception);
		

	}

}
