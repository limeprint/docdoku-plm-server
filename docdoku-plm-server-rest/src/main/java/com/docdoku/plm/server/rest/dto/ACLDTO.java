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
import com.docdoku.plm.server.core.security.ACLPermission;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ApiModel(value = "ACLDTO", description = "This class is the representation of an {@link com.docdoku.plm.server.core.security.ACL} entity")
public class ACLDTO implements Serializable {


    @ApiModelProperty(value = "Users ACL entries")
    @JsonbProperty(nillable = true)
    private List<ACLEntryDTO> userEntries = new ArrayList<>();

    @ApiModelProperty(value = "Groups ACL entries")
    @JsonbProperty(nillable = true)
    private List<ACLEntryDTO> groupEntries = new ArrayList<>();

    public ACLDTO() {
    }

    public void addUserEntry(String login, ACLPermission perm) {
        ACLEntryDTO aclEntryDTO = new ACLEntryDTO(login, perm);
        userEntries.add(aclEntryDTO);
    }

    public void addGroupEntry(String groupId, ACLPermission perm) {
        ACLEntryDTO aclEntryDTO = new ACLEntryDTO(groupId, perm);
        groupEntries.add(aclEntryDTO);
    }

    public List<ACLEntryDTO> getGroupEntries() {
        return groupEntries;
    }

    public void setGroupEntries(List<ACLEntryDTO> groupEntries) {
        this.groupEntries = groupEntries;
    }

    public List<ACLEntryDTO> getUserEntries() {
        return userEntries;
    }

    public void setUserEntries(List<ACLEntryDTO> userEntries) {
        this.userEntries = userEntries;
    }

    public boolean hasEntries(){
        return !userEntries.isEmpty() || !groupEntries.isEmpty();
    }

    public Map<String, String> getUserEntriesMap() {
        Map<String, String> map = new HashMap<>();
        for (ACLEntryDTO entry : getUserEntries()) {
            map.put(entry.getKey(), entry.getValue().name());
        }
        return map;
    }

    public Map<String, String> getUserGroupEntriesMap() {
        Map<String, String> map = new HashMap<>();
        for (ACLEntryDTO entry : getGroupEntries()) {
            map.put(entry.getKey(), entry.getValue().name());
        }
        return map;
    }

}
