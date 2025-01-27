/*******************************************************************************
 * Copyright (c) 2010 Daniel Murygin.
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.bpm;

import java.util.Date;

import sernet.verinice.bpm.rcp.TaskChangeRegistry;
import sernet.verinice.interfaces.bpm.ITaskService;

/**
 * Loads new tasks using the ITaskService.
 * You can use TaskLoader in a Spring/Quartz cron job.
 * See: sernet/springclient/veriniceclient.xml
 * 
 * TaskLoader was disabled after release verinice 1.9.
 * It is not used anymore in verinice > 1.9.
 * 
 * (If configured in veriniceclient.xml) method load is called periodically and loads 
 * newly created tasks after last call.
 * 
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public class TaskLoader {

    private ITaskService taskService;
    
    private Date lastChecked = null;
    
    /**
     * Is called periodically by Spring/Quartz cron job 
     * and loads newly created tasks after last call.
     */
    public void load() {
        Date now = new Date(System.currentTimeMillis());
        if(lastChecked!=null) {
            // loadNewTasks() does not detects delete task
            // loadNewTasks();
            // We just notify listeners, that something has changed to 
            // do a reload if needed
            TaskChangeRegistry.tasksAdded();
        }
        
        lastChecked = now;
    }
    
    public ITaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }
    
}
