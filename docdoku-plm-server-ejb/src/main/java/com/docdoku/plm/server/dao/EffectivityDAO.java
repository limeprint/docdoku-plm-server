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

import com.docdoku.plm.server.core.exceptions.CreationException;
import com.docdoku.plm.server.core.exceptions.EffectivityAlreadyExistsException;
import com.docdoku.plm.server.core.exceptions.EffectivityNotFoundException;
import com.docdoku.plm.server.core.product.Effectivity;
import com.docdoku.plm.server.core.product.PartRevision;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.List;


@RequestScoped
public class EffectivityDAO {

    @Inject
    private EntityManager em;

    public EffectivityDAO() {
    }

    public Effectivity loadEffectivity(int pId) throws EffectivityNotFoundException {
        Effectivity effectivity = em.find(Effectivity.class, pId);
        if (effectivity == null) {
            throw new EffectivityNotFoundException(String.valueOf(pId));
        } else {
            return effectivity;
        }
    }

    public void updateEffectivity(Effectivity effectivity) {
        em.merge(effectivity);
        em.flush();
    }

    public void removeEffectivity(Effectivity pEffectivity) {
        em.remove(pEffectivity);
        em.flush();
    }

    public void createEffectivity(Effectivity pEffectivity) throws EffectivityAlreadyExistsException, CreationException {
        try {
            //the EntityExistsException is thrown only when flush occurs
            em.persist(pEffectivity);
            em.flush();
        } catch (EntityExistsException pEEEx) {
            throw new EffectivityAlreadyExistsException(pEffectivity);
        } catch (PersistenceException pPEx) {
            //EntityExistsException is case sensitive
            //whereas MySQL is not thus PersistenceException could be
            //thrown instead of EntityExistsException
            throw new CreationException();
        }
    }

    public PartRevision getPartRevisionHolder(int pId) {

        TypedQuery<PartRevision> query =
                em.createNamedQuery("Effectivity.findPartRevisionHolder", PartRevision.class);
        query.setParameter("effectivityId", pId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void removeEffectivityConstraints(String workspaceId) {
        TypedQuery<Effectivity> query = em.createNamedQuery("Effectivity.getEffectivitiesInWorkspace", Effectivity.class);
        query.setParameter("workspaceId", workspaceId);
        List<Effectivity> effectivities = query.getResultList();
        effectivities.forEach(effectivity -> effectivity.setConfigurationItem(null));
    }
}
