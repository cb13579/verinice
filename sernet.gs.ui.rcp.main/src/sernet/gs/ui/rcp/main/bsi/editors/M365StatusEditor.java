/*******************************************************************************
 * Copyright (c) 2009 Daniel Murygin <dm[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.gs.ui.rcp.main.bsi.editors;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.EditorPart;

import sernet.gs.ui.rcp.main.ExceptionUtil;
import sernet.gs.ui.rcp.main.ImageCache;
import sernet.gs.ui.rcp.main.service.ServiceFactory;
import sernet.hui.common.connect.IEntityChangedListener;
import sernet.hui.common.connect.PropertyChangedEvent;
import sernet.hui.common.multiselectionlist.IMLPropertyOption;
import sernet.hui.common.multiselectionlist.IMLPropertyType;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.interfaces.ICommandService;
import sernet.verinice.model.bsi.Addition.INoteChangedListener;
import sernet.verinice.model.bsi.Note;
import sernet.verinice.service.commands.SaveNote;

public class M365StatusEditor extends EditorPart {

	private static final Logger LOG = Logger.getLogger(M365StatusEditor.class);
	
	public static final String EDITOR_ID = "sernet.gs.ui.rcp.main.bsi.editors.m365statuseditor"; //$NON-NLS-1$
	
	private Note note;
	
	private Text textNote;
	
	private ICommandService	commandService;

	private boolean isModelModified = false;
	
	private IEntityChangedListener modelListener = new IEntityChangedListener() {
		public void dependencyChanged(IMLPropertyType arg0, IMLPropertyOption arg1) {
			// not relevant
		}	
		public void selectionChanged(IMLPropertyType arg0, IMLPropertyOption arg1) {
			modelChanged();
		}
		public void propertyChanged(PropertyChangedEvent evt) {
			modelChanged();
		}
	};
	
	void modelChanged() {
		boolean wasDirty = isDirty();
		isModelModified = true;
		
		if (!wasDirty){
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
	
	@Override
	public boolean isDirty() {
		return isModelModified;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		LOG.debug("doSave called");
		
		monitor.beginTask(Messages.M365StatusEditor_0, IProgressMonitor.UNKNOWN);
		Set<INoteChangedListener> listener = note.getListener(); 
		SaveNote command = new SaveNote(note);		
		try {
			command = getCommandService().executeCommand(command);
		} catch (CommandException e) {
			LOG.error("Error while saving status", e); //$NON-NLS-1$
			ExceptionUtil.log(e, Messages.M365StatusEditor_1);
		}
		monitor.done();
		note = (Note) command.getAddition();
		note.getListener().addAll(listener);
		isModelModified = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
		note.getEntity().addChangeListener(this.modelListener);
		setPartName(note.getTitel());
		note.fireChange();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		LOG.debug("init called");
	
		if (! (input instanceof M365StatusEditorInput)) {
			throw new PartInitException(Messages.M365StatusEditor_2);
		}
		M365StatusEditorInput noteEditorInput = (M365StatusEditorInput) input;
		note=noteEditorInput.getInput();
		setSite(site);
		setInput(noteEditorInput);
		setPartName(noteEditorInput.getName());
		// add listener to mark editor as dirty on changes:
		noteEditorInput.getInput().getEntity().addChangeListener(this.modelListener);
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		LOG.debug("createPartControl called");
	
	    final int contentCompLayoutMarginWidth = 5;
        final int contentCompLayoutMarginHeight = contentCompLayoutMarginWidth;
        final int contentCompLayoutHorizontalSpacing = contentCompLayoutMarginHeight;
        final int contentCompLayoutVerticalSpacing = contentCompLayoutMarginWidth;
        final int contentCompLayoutNumColumns = 2;
        final int gdTextHeightHint = 200;
        
        Composite contentComp = new Composite(parent, SWT.NULL);
		
		GridData contentCompLD = new GridData();
		contentCompLD.grabExcessHorizontalSpace = true;
		contentCompLD.horizontalAlignment = GridData.FILL;
		contentCompLD.grabExcessHorizontalSpace = true;
		contentComp.setLayoutData(contentCompLD);
		
		GridLayout contentCompLayout = new GridLayout(2, false);
		contentCompLayout.marginWidth = contentCompLayoutMarginWidth;
		contentCompLayout.marginHeight = contentCompLayoutMarginHeight;
		contentCompLayout.numColumns = contentCompLayoutNumColumns;
		contentCompLayout.horizontalSpacing = contentCompLayoutHorizontalSpacing;
		contentCompLayout.verticalSpacing = contentCompLayoutVerticalSpacing;
		
		contentComp.setLayout(contentCompLayout);
		
		GridData gdElement= new GridData();
		gdElement.grabExcessHorizontalSpace = true;
		gdElement.horizontalAlignment = GridData.FILL;
		gdElement.horizontalSpan=2;
		Label labelElement = new Label(contentComp,SWT.TOP );
		labelElement.setText(Messages.M365StatusEditor_3 + note.getCnAElementTitel());
		labelElement.setLayoutData(gdElement);
		
		// create Link to Security Score
				
		GridData gdLink= new GridData();
		gdLink.grabExcessHorizontalSpace = true;
		gdLink.horizontalAlignment = GridData.FILL;
		gdLink.horizontalSpan=2;
		Control link = addLinkToDialog(contentComp, "M365 Security Center", "https://security.microsoft.com/securescore?viewid=overview");
		link.setLayoutData(gdLink);

		// create debug log
		
		GridData gdLabel= new GridData();
		gdLabel.grabExcessHorizontalSpace = true;
		gdLabel.horizontalAlignment = GridData.FILL;
		gdLabel.horizontalSpan=2;
		Label labelNote = new Label(contentComp,SWT.TOP);
		labelNote.setText(Messages.M365StatusEditor_5);	
		labelNote.setLayoutData(gdLabel);
				
		GridData gdText = new GridData();
		gdText.grabExcessHorizontalSpace = true;
		gdText.grabExcessVerticalSpace = false;
		gdText.horizontalSpan=2;
		gdText.horizontalAlignment = GridData.FILL;
		gdText.verticalAlignment = GridData.CENTER;
		gdText.heightHint=gdTextHeightHint;
		textNote = new Text(contentComp,SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		textNote.setLayoutData(gdText);
		textNote.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				Text field = (Text) e.widget;
				note.setText(field.getText());
			}
		});
				
		// create Status Report Button
		
		GridData gdReportButton= new GridData();
		gdReportButton.grabExcessVerticalSpace = false;
		gdReportButton.grabExcessHorizontalSpace = false;
		gdReportButton.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		gdReportButton.verticalAlignment = GridData.BEGINNING;
		Button button = new Button(contentComp, SWT.PUSH);
		button.setImage(ImageCache.getInstance().getImage(ImageCache.BUTTON_GET_M365STATUS));
		button.setText("Status abrufen");
		button.setLayoutData(gdReportButton);
		String sReportFile = System.getProperty("m365StatusReport");
		button.addSelectionListener(new StatusSelectionAdapter(getSite().getShell(), textNote, sReportFile));
		
		// create Show Status Report Button
		
		GridData gdShowReportButton= new GridData();
		gdShowReportButton.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
	    gdShowReportButton.grabExcessHorizontalSpace = true;
		gdShowReportButton.grabExcessVerticalSpace = false;
		gdShowReportButton.verticalAlignment = GridData.BEGINNING;
		
		File file = new File(sReportFile);
		LOG.debug("PATH: " + file.getPath());

		// Convert file to URI	
		URI uri = file.toURI();
		// Convert URI to URL
		URL url=null;
		try {
			url = uri.toURL();
			System.out.println("URL: " + url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Control showReportButton = addButtonToDialog(contentComp, new String("Report ansehen"), url);
		showReportButton.setLayoutData(gdShowReportButton);
		
		if(note!=null) {
			/*if(note.getTitel()!=null) {
				title.setText(note.getTitel());
			}*/
			if(note.getText()!=null) {
				textNote.setText(note.getText());
			}
		}
			
		contentComp.pack();
		contentComp.layout();
	}

	
	public Control addLinkToDialog(Composite parent, String linkLabel, String linkUrl) {
		Link link = new Link(parent, SWT.WRAP);
		link.setText("<a>" + linkLabel + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser = browserSupport.getExternalBrowser();
					URL url = new URL(linkUrl);
					browser.openURL(url);
				} catch (Exception e) {
					LOG.error("An error occurred trying to open an external browser at: " + link, e); //$NON-NLS-1$
				}
			}
		});
		return link;
	}
	public Control addButtonToDialog(Composite parent, String label, URL buttonURL) {
		Button showReportButton = new Button(parent, SWT.PUSH);
		showReportButton.setText(label);
		showReportButton.setImage(ImageCache.getInstance().getImage(ImageCache.BUTTON_SHOW_M365STATUS));
		showReportButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser = browserSupport.getExternalBrowser();
					browser.openURL(buttonURL);
				} catch (Exception e) {
					LOG.error("An error occurred trying to open an external browser at: " + buttonURL, e); //$NON-NLS-1$
				}
			}
		});
		return showReportButton;
	}
	
	@Override
	public void setFocus() {
		textNote.setFocus();
	}
	
	@Override
	public void dispose() {
		EditorRegistry.getInstance().closeEditor(String.valueOf(((M365StatusEditorInput)getEditorInput()).getId()));
		super.dispose();
	}
	
	public ICommandService getCommandService() {
		if(commandService==null) {
			commandService = createCommandServive();
		}
		return commandService;
	}

	private ICommandService createCommandServive() {
		return ServiceFactory.lookupCommandService();
	}

}
