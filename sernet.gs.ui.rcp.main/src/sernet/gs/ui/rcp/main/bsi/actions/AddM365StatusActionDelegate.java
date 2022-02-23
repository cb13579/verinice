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
package sernet.gs.ui.rcp.main.bsi.actions;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import sernet.gs.ui.rcp.main.ApplicationWorkbenchWindowAdvisor;
import sernet.gs.ui.rcp.main.ExceptionUtil;
import sernet.gs.ui.rcp.main.bsi.editors.EditorFactory;
import sernet.hui.common.VeriniceContext;
import sernet.springclient.RightsServiceClient;
import sernet.verinice.interfaces.ActionRightIDs;
import sernet.verinice.interfaces.RightEnabledUserInteraction;
import sernet.verinice.model.bsi.BausteinUmsetzung;
import sernet.verinice.model.bsi.Note;
import sernet.verinice.model.common.CnATreeElement;

public class AddM365StatusActionDelegate implements IObjectActionDelegate, RightEnabledUserInteraction {

    private IWorkbenchPart targetPart;
    
    private static final Logger LOG = Logger.getLogger(AddM365StatusActionDelegate.class);
    

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    public void run(IAction action) {
        try {
            Object sel = ((IStructuredSelection) targetPart.getSite().getSelectionProvider()
                    .getSelection()).getFirstElement();
            if (sel instanceof CnATreeElement) {
                Note note = new Note();
                note.setCnATreeElement(((CnATreeElement) sel));
                note.setCnAElementTitel(((CnATreeElement) sel).getTitle());
                note.setTitel(Messages.AddM365StatusActionDelegate_0);
                EditorFactory.getInstance().openEditor(note);
            }
        } catch (Exception e) {
            ExceptionUtil.log(e, Messages.AddM365StatusActionDelegate_1);
        }
    }

    /*
     * @see
     * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
     * .IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    	LOG.setLevel(Level.DEBUG);
        LOG.debug("selectionChanged is permitted:\t" + checkRights());

        Object sel = ((IStructuredSelection) targetPart.getSite().getSelectionProvider().getSelection()).getFirstElement();
        if (sel instanceof CnATreeElement) {
        	LOG.debug("elementSelected: id:" + ((CnATreeElement)sel).getId());
        	LOG.debug("elementSelected: title:" + ((CnATreeElement)sel).getTitle());
        	LOG.debug("elementSelected: type:" + ((CnATreeElement)sel).getTypeId());
        	LOG.debug("elementSelected: extId:" + ((CnATreeElement)sel).getExtId());
        	LOG.debug("elementSelected: sourceId:" + ((CnATreeElement)sel).getSourceId());
        	    
        	if (sel instanceof BausteinUmsetzung) {
        		BausteinUmsetzung bu = (BausteinUmsetzung) sel;
        		LOG.debug("selectionChanged: Baustein:" + bu.getKapitel());
        	}
        }
        action.setEnabled(checkRights());
    }

    /*
     * @see sernet.verinice.interfaces.RightEnabledUserInteraction#checkRights()
     */
    @Override
    public boolean checkRights() {
        RightsServiceClient service = (RightsServiceClient) VeriniceContext
                .get(VeriniceContext.RIGHTS_SERVICE);
        return service.isEnabled(getRightID());
    }

    /*
     * @see sernet.verinice.interfaces.RightEnabledUserInteraction#getRightID()
     */
    @Override
    public String getRightID() {
        return ActionRightIDs.ADDM365STATUS;
    }

}
