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
package com.docdoku.plm.server.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yassine Belouad
 */

@ApiModel(value="RoleDTO", description="This class is a representation of a {@link com.docdoku.plm.server.core.workflow.Role} entity")
public class RoleDTO implements Serializable {

    @ApiModelProperty(value = "Role id")
    private String id;

    @ApiModelProperty(value = "Role name")
    private String name;

    @ApiModelProperty(value = "Workspace id")
    private String workspaceId;

    @ApiModelProperty(value = "Default assigned users for role")
    private List<UserDTO> defaultAssignedUsers;

    @ApiModelProperty(value = "Default assigned groups for role")
    private List<UserGroupDTO> defaultAssignedGroups;

    public RoleDTO() {
    }

    public RoleDTO(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<UserDTO> getDefaultAssignedUsers() {
        return defaultAssignedUsers;
    }

    public void setDefaultAssignedUsers(List<UserDTO> defaultAssignedUsers) {
        this.defaultAssignedUsers = defaultAssignedUsers;
    }

    public List<UserGroupDTO> getDefaultAssignedGroups() {
        return defaultAssignedGroups;
    }

    public void setDefaultAssignedGroups(List<UserGroupDTO> defaultAssignedGroups) {
        this.defaultAssignedGroups = defaultAssignedGroups;
    }
}
