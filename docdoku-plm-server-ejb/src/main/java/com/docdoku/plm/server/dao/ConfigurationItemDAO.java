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
package com.docdoku.plm.server.dao;


import com.docdoku.plm.server.core.exceptions.ConfigurationItemAlreadyExistsException;
import com.docdoku.plm.server.core.exceptions.ConfigurationItemNotFoundException;
import com.docdoku.plm.server.core.exceptions.CreationException;
import com.docdoku.plm.server.core.product.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;


@RequestScoped
public class ConfigurationItemDAO {

    public static final String WORKSPACE_ID = "workspaceId";

    @Inject
    private EntityManager em;

    public ConfigurationItemDAO() {
    }

    public ConfigurationItem removeConfigurationItem(ConfigurationItemKey pKey) throws ConfigurationItemNotFoundException {
        ConfigurationItem ci = loadConfigurationItem(pKey);

        removeLayersFromConfigurationItem(pKey);
        removeEffectivitiesFromConfigurationItem(pKey);

        em.remove(ci);
        return ci;
    }

    public void removeLayersFromConfigurationItem(ConfigurationItemKey pKey){
        TypedQuery<Layer> query = em.createNamedQuery("Layer.removeLayersFromConfigurationItem", Layer.class);
        query.setParameter(WORKSPACE_ID, pKey.getWorkspace());
        query.setParameter("configurationItemId", pKey.getId());
        query.executeUpdate();
    }

    public void removeEffectivitiesFromConfigurationItem(ConfigurationItemKey pKey){
        TypedQuery<Effectivity> query = em.createNamedQuery("Effectivity.removeEffectivitiesFromConfigurationItem", Effectivity.class);
        query.setParameter(WORKSPACE_ID, pKey.getWorkspace());
        query.setParameter("configurationItemId", pKey.getId());
        query.executeUpdate();
    }

    public List<ConfigurationItem> findAllConfigurationItems(String pWorkspaceId) {
        TypedQuery<ConfigurationItem> query = em.createNamedQuery("ConfigurationItem.getConfigurationItemsInWorkspace", ConfigurationItem.class);
        query.setParameter(WORKSPACE_ID, pWorkspaceId);
        return query.getResultList();
    }

    public ConfigurationItem loadConfigurationItem(ConfigurationItemKey pKey)
            throws ConfigurationItemNotFoundException {
        ConfigurationItem ci = em.find(ConfigurationItem.class, pKey);
        if (ci == null) {
            throw new ConfigurationItemNotFoundException(pKey.getId());
        } else {
            return ci;
        }
    }

    public void createConfigurationItem(ConfigurationItem pCI) throws ConfigurationItemAlreadyExistsException, CreationException {
        try {
            //the EntityExistsException is thrown only when flush occurs
            em.persist(pCI);
            em.flush();
        } catch (EntityExistsException pEEEx) {
            throw new ConfigurationItemAlreadyExistsException(pCI);
        } catch (PersistenceException pPEx) {
            //EntityExistsException is case sensitive
            //whereas MySQL is not thus PersistenceException could be
            //thrown instead of EntityExistsException
            throw new CreationException();
        }
    }

    public List<ConfigurationItem> findConfigurationItemsByDesignItem(PartMaster partMaster) {

        TypedQuery<ConfigurationItem> query = em.createNamedQuery("ConfigurationItem.findByDesignItem", ConfigurationItem.class);
        return query.setParameter("designItem", partMaster).getResultList();

    }

    public boolean isPartMasterLinkedToConfigurationItem(PartMaster partMaster){
        return !findConfigurationItemsByDesignItem(partMaster).isEmpty();
    }
}
