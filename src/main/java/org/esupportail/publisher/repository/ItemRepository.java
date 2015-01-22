/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.repository;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 4 juil. 2014
 */
public interface ItemRepository<T extends AbstractItem> extends
		AbstractRepository<T, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update #{#entityName} e set e.status = org.esupportail.publisher.domain.enums.ItemStatus.ARCHIVED where e.status = org.esupportail.publisher.domain.enums.ItemStatus.PUBLISHED and e.endDate <= CURRENT_DATE")
    Integer archiveExpiredPublished();

    @Modifying(clearAutomatically = true)
    @Query("update #{#entityName} e set e.status = org.esupportail.publisher.domain.enums.ItemStatus.PUBLISHED where e.status = org.esupportail.publisher.domain.enums.ItemStatus.SCHEDULED and e.startDate <= CURRENT_DATE")
    Integer publishScheduled();
}
