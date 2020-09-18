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

package com.docdoku.plm.server.core.exceptions;

/**
 * Base class for implementing an Exception that represents a failed attempt
 * to retrieve an entity because it has not been found in the persistent store.
 *
 * @author Taylor Labejof
 */
public abstract class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String pMessage) {
        super(pMessage);
    }

    public EntityNotFoundException(Throwable pCause) {
        super(pCause);
    }
}
