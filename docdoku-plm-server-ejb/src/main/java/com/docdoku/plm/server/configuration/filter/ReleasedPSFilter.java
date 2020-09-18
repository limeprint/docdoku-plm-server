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


package com.docdoku.plm.server.configuration.filter;

import com.docdoku.plm.server.core.configuration.ProductStructureFilter;
import com.docdoku.plm.server.core.product.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link com.docdoku.plm.server.core.configuration.ProductStructureFilter} implementation
 * which selects the last iteration of all released revisions.
 *
 * @author Taylor LABEJOF
 * @version 2.0, 29/08/14
 * @since   V2.0
 */

public class ReleasedPSFilter implements ProductStructureFilter, Serializable {

    private boolean diverge = false;

    public ReleasedPSFilter(boolean diverge) {
        this.diverge = diverge;
    }

    @Override
    public List<PartIteration> filter(PartMaster part) {
        List<PartIteration> partIterations = new ArrayList<>();
        List<PartRevision> partRevisions = part.getAllReleasedRevisions();
        for(PartRevision partRevision: partRevisions){
            // Taking last iteration, can't be checked out if released.
            partIterations.add(partRevision.getLastIteration());
        }
        return partIterations;
    }

    @Override
    public List<PartLink> filter(List<PartLink> path) {

        List<PartLink> links = new ArrayList<>();

        PartLink link = path.get(path.size()-1);
        links.add(link);

        if(diverge){
            for(PartSubstituteLink substituteLink: link.getSubstitutes()){
                links.add(substituteLink);
            }
        }

        return links;
    }

}
