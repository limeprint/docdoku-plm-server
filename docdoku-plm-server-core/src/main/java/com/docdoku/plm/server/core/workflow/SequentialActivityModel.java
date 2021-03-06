/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2020 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.plm.server.core.workflow;

import com.docdoku.plm.server.core.common.User;
import com.docdoku.plm.server.core.common.UserGroup;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is the model used to create instances
 * of {@link SequentialActivity} attached to workflows.
 * 
 * @author Florent Garin
 * @version 1.0, 02/06/08
 * @since   V1.0
 */
@Table(name="SEQUENTIALACTIVITYMODEL")
@Entity
public class SequentialActivityModel extends ActivityModel {


    
    public SequentialActivityModel() {
    }

    public SequentialActivityModel(WorkflowModel pWorkflowModel, int pStep, List<TaskModel> pTaskModels, String pLifeCycleState) {
        super(pWorkflowModel, pStep, pTaskModels, pLifeCycleState);
    }

    public SequentialActivityModel(WorkflowModel pWorkflowModel, String pLifeCycleState) {
        this(pWorkflowModel, 0,  new LinkedList<>(), pLifeCycleState);
    }
        
    @Override
    public Activity createActivity(Map<Role,Collection<User>> roleUserMap, Map<Role,Collection<UserGroup>> roleGroupMap) {
        Activity activity = new SequentialActivity(step, lifeCycleState);
        List<Task> tasks = activity.getTasks();
        for(TaskModel model:taskModels){
            Task task = model.createTask(roleUserMap, roleGroupMap);
            task.setActivity(activity);
            tasks.add(task);
        }
        return activity;
    }

    @Override
    public String toString() {
        return taskModels.toString();
    }

    public void moveUpTaskModel(int pSelectedIndex) {
        if (pSelectedIndex > 0) {
            TaskModel taskModel = taskModels.remove(pSelectedIndex);
            int newIndex=pSelectedIndex-1;
            taskModels.get(newIndex).setNum(pSelectedIndex);
            taskModels.add(newIndex, taskModel);
            taskModel.setNum(newIndex);
        }
    }

    public void moveDownTaskModel(int pSelectedIndex) {
        if (pSelectedIndex < taskModels.size() - 1) {
            TaskModel taskModel = taskModels.remove(pSelectedIndex);
            int newIndex=pSelectedIndex+1;
            taskModels.get(pSelectedIndex).setNum(pSelectedIndex);
            taskModels.add(newIndex, taskModel);
            taskModel.setNum(newIndex);
        }
    }

    @Override
    public SequentialActivityModel clone() {
        return (SequentialActivityModel) super.clone();
    }
}
