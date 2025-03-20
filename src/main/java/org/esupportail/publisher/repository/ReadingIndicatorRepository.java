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

import org.esupportail.publisher.domain.ReadingIndicator;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingIndicatorRepository extends AbstractRepository<ReadingIndicator, Long> {

    @Modifying
    @Query("UPDATE #{#entityName} r SET r.isRead = :isRead WHERE r.item.id = :itemId AND r.user = :userId")
    void readingManagement(@Param("itemId") Long itemId, @Param("userId") String userId,
        @Param("isRead") boolean isRead);

    @Modifying
    @Query("UPDATE #{#entityName} r SET r.readingCounter = r.readingCounter + 1 WHERE r.item.id = :itemId AND r.user = :userId")
    void incrementReadingCounter(@Param("itemId") Long itemId, @Param("userId") String userId);

}
