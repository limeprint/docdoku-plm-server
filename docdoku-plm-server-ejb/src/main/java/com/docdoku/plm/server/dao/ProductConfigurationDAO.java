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

import com.docdoku.plm.server.core.configuration.ProductConfiguration;
import com.docdoku.plm.server.core.exceptions.CreationException;
import com.docdoku.plm.server.core.exceptions.ProductConfigurationNotFoundException;
import com.docdoku.plm.server.core.product.ConfigurationItemKey;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class ProductConfigurationDAO {

    @Inject
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(ProductConfigurationDAO.class.getName());

    public ProductConfigurationDAO() {
    }

    public void createProductConfiguration(ProductConfiguration pProductConfiguration) throws CreationException {
        try {
            em.persist(pProductConfiguration);
            em.flush();
        } catch (PersistenceException pPEx) {
            LOGGER.log(Level.FINEST, null, pPEx);
            throw new CreationException();
        }
    }

    public ProductConfiguration getProductConfiguration(int pProductConfigurationId) throws ProductConfigurationNotFoundException {
        ProductConfiguration productConfiguration = em.find(ProductConfiguration.class, pProductConfigurationId);
        if (productConfiguration != null) {
            return productConfiguration;
        } else {
            throw new ProductConfigurationNotFoundException(pProductConfigurationId);
        }
    }

    public List<ProductConfiguration> getAllProductConfigurations(String workspaceId) {
        return em.createNamedQuery("ProductConfiguration.findByWorkspace", ProductConfiguration.class)
                .setParameter("workspaceId", workspaceId)
                .getResultList();
    }

    public List<ProductConfiguration> getAllProductConfigurationsByConfigurationItem(ConfigurationItemKey ciKey) {
        return em.createNamedQuery("ProductConfiguration.findByConfigurationItem", ProductConfiguration.class)
                .setParameter("workspaceId", ciKey.getWorkspace())
                .setParameter("configurationItemId", ciKey.getId())
                .getResultList();
    }

    public void deleteProductConfiguration(ProductConfiguration productConfiguration) {
        em.remove(productConfiguration);
        em.flush();
    }
}
