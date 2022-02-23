package sernet.gs.ui.rcp.main.bsi.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class StatusSelectionAdapter extends SelectionAdapter {
	   
	private final Shell shell;
	private Text resultWidget;
	private String targetFile;
	
	private static String stdout = null;
	private static PowerShellSkriptHandler handler = new PowerShellSkriptHandler();
	

	    public StatusSelectionAdapter(Shell shell, Text textWidget, String file ) {
	        this.shell = shell;
	        this.resultWidget = textWidget;
	        this.targetFile = file;
	    }

	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        Job job = new Job("Call M365 Security Center") {
	            @Override
	            protected IStatus run(IProgressMonitor monitor) {
	                doLongThing();
	                syncWithUi();
	                // use this to open a Shell in the UI thread
	                return Status.OK_STATUS;
	            }

	        };
	        job.setUser(true);
	        job.schedule();
	    }

	    private void doLongThing() {
	    	
	    	try {
	    		String sScript = System.getProperty("m365StatusScript");
	    		stdout = handler.executePSScript(sScript, targetFile);
	          } catch (Exception e) {
	    		stdout = e.getMessage();
				e.printStackTrace();
			}
	    }

	    private void triggerResultWidget( final String result ) {
	        final Display d = resultWidget.getDisplay();
	        if( !d.isDisposed () ) {
	            if( !resultWidget.isDisposed()) {
	            	resultWidget.setText( result );
	            }
	        }
	    }
	    
	    private void syncWithUi() {
	        Display.getDefault().asyncExec(new Runnable() {
	            public void run() {
	                MessageDialog.openInformation(shell, "M365 Security Call",
	                        "Your call has finished.");
	                triggerResultWidget(stdout);
	            }
	        });
	    }

		private static String getStdout() {
			return stdout;
		}
}
