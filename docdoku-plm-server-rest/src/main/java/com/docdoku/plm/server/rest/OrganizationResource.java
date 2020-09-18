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

package com.docdoku.plm.server.rest;

import io.swagger.annotations.*;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;
import com.docdoku.plm.server.core.common.Account;
import com.docdoku.plm.server.core.common.Organization;
import com.docdoku.plm.server.core.exceptions.*;
import com.docdoku.plm.server.core.exceptions.NotAllowedException;
import com.docdoku.plm.server.core.security.UserGroupMapping;
import com.docdoku.plm.server.core.services.IAccountManagerLocal;
import com.docdoku.plm.server.core.services.IOrganizationManagerLocal;
import com.docdoku.plm.server.rest.dto.AccountDTO;
import com.docdoku.plm.server.rest.dto.OrganizationDTO;
import com.docdoku.plm.server.rest.dto.UserDTO;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequestScoped
@Api(value = "organizations", description = "Operations about organizations",
        authorizations = {@Authorization(value = "authorization")})
@Path("organizations")
@DeclareRoles(UserGroupMapping.REGULAR_USER_ROLE_ID)
@RolesAllowed(UserGroupMapping.REGULAR_USER_ROLE_ID)
public class OrganizationResource {

    @Inject
    private IOrganizationManagerLocal organizationManager;

    @Inject
    private IAccountManagerLocal accountManager;

    private Mapper mapper;

    public OrganizationResource() {
    }

    @PostConstruct
    public void init() {
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }


    @GET
    @ApiOperation(value = "Get organization for authenticated user",
            response = OrganizationDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of OrganizationDTO. It may be a null object"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public OrganizationDTO getOrganization()
            throws EntityNotFoundException {

        try {
            Organization organization = organizationManager.getMyOrganization();
            return mapper.map(organization, OrganizationDTO.class);
        } catch (OrganizationNotFoundException e) {
            return null;
        }
    }

    @POST
    @ApiOperation(value = "Create authenticated user's organization",
            response = OrganizationDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of created OrganizationDTO"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public OrganizationDTO createOrganization(
            @ApiParam(required = true, value = "Organization to create") OrganizationDTO organizationDTO)
            throws NotAllowedException, EntityNotFoundException, EntityAlreadyExistsException, CreationException {

        Organization organization = organizationManager.createOrganization(organizationDTO.getName(), organizationDTO.getDescription());
        return mapper.map(organization, OrganizationDTO.class);
    }

    @PUT
    @ApiOperation(value = "Update authenticated user's organization",
            response = OrganizationDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of updated OrganizationDTO"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public OrganizationDTO updateOrganization(
            @ApiParam(required = true, value = "Updated organization") OrganizationDTO organizationDTO)
            throws EntityNotFoundException, AccessRightException {

        Organization organization = organizationManager.getMyOrganization();
        organization.setDescription(organizationDTO.getDescription());
        organizationManager.updateOrganization(organization);
        return organizationDTO;
    }

    @DELETE
    @ApiOperation(value = "Delete authenticated user's organization",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successful deletion of OrganizationDTO"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrganization()
            throws EntityNotFoundException, AccessRightException {

        Organization organization = organizationManager.getMyOrganization();
        organizationManager.deleteOrganization(organization.getName());
        return Response.noContent().build();
    }

    @GET
    @Path("members")
    @ApiOperation(value = "Get members of the authenticated user's organization",
            response = AccountDTO.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of AccountDTOs. It can be an empty list."),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers()
            throws EntityNotFoundException, AccessRightException {

        Organization organization = organizationManager.getMyOrganization();
        List<Account> accounts = organization.getMembers();
        List<AccountDTO> accountsDTOs = new ArrayList<>();

        for (Account account : accounts) {
            accountsDTOs.add(mapper.map(account, AccountDTO.class));
        }

        return Response.ok(new GenericEntity<List<AccountDTO>>((List<AccountDTO>) accountsDTOs) {
        }).build();
    }

    @PUT
    @Path("add-member")
    @ApiOperation(value = "Add a member to the authenticated user's organization",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successful member add operation"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMember(
            @ApiParam(required = true, value = "User to add") UserDTO userDTO)
            throws EntityNotFoundException, AccessRightException {

        Organization organization = organizationManager.getMyOrganization();
        Account member = accountManager.getAccount(userDTO.getLogin());
        organization.addMember(member);
        organizationManager.updateOrganization(organization);
        return Response.noContent().build();
    }

    @PUT
    @Path("remove-member")
    @ApiOperation(value = "Remove a member to the authenticated user's organization",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successful member removal operation"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeMember(
            @ApiParam(required = true, value = "User to remove") UserDTO userDTO)
            throws EntityNotFoundException, AccessRightException {


        Organization organization = organizationManager.getMyOrganization();
        Account member = accountManager.getAccount(userDTO.getLogin());
        organization.removeMember(member);
        organizationManager.updateOrganization(organization);
        return Response.noContent().build();

    }

    @PUT
    @Path("move-member")
    @ApiOperation(value = "Move a member up or down in the authenticated user's organization",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successful member moved operation"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response moveMember(
            @ApiParam(required = true, value = "User to move up") UserDTO userDTO,
            @ApiParam(required = true, value = "Direction (up/down)") @QueryParam("direction") String direction)
            throws EntityNotFoundException, AccessRightException {

        if ("up".equals(direction)) {
            moveMemberUp(userDTO.getLogin());
        } else if ("down".equals(direction)) {
            moveMemberDown(userDTO.getLogin());
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.noContent().build();
    }

    private void moveMemberUp(String login) throws OrganizationNotFoundException, AccountNotFoundException, AccessRightException {
        Organization organization = organizationManager.getMyOrganization();
        Account member = accountManager.getAccount(login);
        List<Account> members = organization.getMembers();
        int i = members.indexOf(member);
        if (i > 0) {
            Collections.swap(members, i - 1, i);
            organizationManager.updateOrganization(organization);
        }
    }

    private void moveMemberDown(String login) throws OrganizationNotFoundException, AccountNotFoundException, AccessRightException {
        Organization organization = organizationManager.getMyOrganization();
        Account member = accountManager.getAccount(login);
        List<Account> members = organization.getMembers();
        int i = members.indexOf(member);
        if (i > -1 && i < members.size() - 1) {
            Collections.swap(members, i + 1, i);
            organizationManager.updateOrganization(organization);
        }
    }

}
