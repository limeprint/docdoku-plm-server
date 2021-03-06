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

package com.docdoku.plm.server.rest.converters;

import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import com.docdoku.plm.server.core.workflow.Activity;
import com.docdoku.plm.server.core.workflow.ParallelActivity;
import com.docdoku.plm.server.core.workflow.SequentialActivity;
import com.docdoku.plm.server.core.workflow.Task;
import com.docdoku.plm.server.rest.dto.ActivityDTO;
import com.docdoku.plm.server.rest.dto.ActivityType;
import com.docdoku.plm.server.rest.dto.TaskDTO;

import java.util.ArrayList;
import java.util.List;

public class ActivityDozerConverter extends DozerConverter<Activity, ActivityDTO> {

    private Mapper mapper;

    public ActivityDozerConverter() {
        super(Activity.class, ActivityDTO.class);
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }

    @Override
    public ActivityDTO convertTo(Activity activity, ActivityDTO activityDTO) {
        List<TaskDTO> tasksDTO = new ArrayList<>();

        for (int i = 0; i < activity.getTasks().size(); i++) {
            tasksDTO.add(mapper.map(activity.getTasks().get(i), TaskDTO.class));
        }

        ActivityType type;
        Integer tasksToComplete = null;
        Integer relaunchStep = null;

        if (activity.getRelaunchActivity() != null) {
            relaunchStep = activity.getRelaunchActivity().getStep();
        }

        if (activity instanceof SequentialActivity) {
            type = ActivityType.SEQUENTIAL;
        } else if (activity instanceof ParallelActivity) {
            type = ActivityType.PARALLEL;
            tasksToComplete = ((ParallelActivity) activity).getTasksToComplete();
        } else {
            throw new IllegalArgumentException("Activity type not supported");
        }

        return new ActivityDTO(activity.getStep(), tasksDTO, activity.getLifeCycleState(), type, tasksToComplete, activity.isComplete(), activity.isStopped(), activity.isInProgress(), activity.isToDo(), relaunchStep);
    }

    @Override
    public Activity convertFrom(ActivityDTO activityDTO, Activity pActivity) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < activityDTO.getTasks().size(); i++) {
            tasks.add(mapper.map(activityDTO.getTasks().get(i), Task.class));
        }

        Activity activity;

        switch (activityDTO.getType()) {
            case SEQUENTIAL:
                activity = new SequentialActivity();
                break;
            case PARALLEL:
                activity = new ParallelActivity();
                ((ParallelActivity) activity).setTasksToComplete(activityDTO.getTasksToComplete());
                break;
            default:
                throw new IllegalArgumentException("ActivityDTO type not supported");
        }

        activity.setStep(activityDTO.getStep());
        activity.setTasks(tasks);
        activity.setLifeCycleState(activityDTO.getLifeCycleState());
        return activity;
    }
}
