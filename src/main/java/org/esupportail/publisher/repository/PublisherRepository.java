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

import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 4 juil. 2014
 */
public interface PublisherRepository extends AbstractRepository<Publisher, Long> {

    @Query("SELECT COALESCE(max(e.displayOrder) +1, 0) FROM #{#entityName} e where e.context.organization.id = :orgId")
    int getNextDisplayOrderInOrganization(@Param("orgId") long organization);

    @Query("SELECT COALESCE(e.displayOrder, 0) FROM #{#entityName} e where e.id = :id")
    int getDisplayOrderOf(@Param("id") long id);

    @Modifying(clearAutomatically = true)
    @Query("update #{#entityName} e set e.displayOrder = e.displayOrder + 1 where e.displayOrder >= :from and e.displayOrder < :to and e.context.organization.id = :orgId")
    Integer setUpperDisplayOrderOfRange(@Param("orgId") long organization, @Param("from") int from, @Param("to") int to);

    @Modifying(clearAutomatically = true)
    @Query("update #{#entityName} e set e.displayOrder = e.displayOrder - 1 where e.displayOrder > :from and e.displayOrder <= :to and e.context.organization.id = :orgId")
    Integer setLowerDisplayOrderOfRange(@Param("orgId") long organization, @Param("from") int from, @Param("to") int to);
}
