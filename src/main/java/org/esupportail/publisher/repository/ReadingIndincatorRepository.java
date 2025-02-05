package org.esupportail.publisher.repository;

import java.util.List;

import org.esupportail.publisher.domain.ReadingIndincator;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingIndincatorRepository extends AbstractRepository<ReadingIndincator, Long> {

    @Query("SELECT r FROM #{#entityName} r WHERE r.user = :userId")
    List<ReadingIndincator> findAllByUserId(@Param("userId") String userId);

    @Query("SELECT r FROM #{#entityName} r WHERE r.item.id = :itemId")
    List<ReadingIndincator> findAllByItemId(@Param("itemId") Long itemId);

    @Query("SELECT COUNT(r) > 0 FROM #{#entityName} r WHERE r.item.id = :itemId AND r.user = :userId")
    boolean existsByItemIdAndUserId(@Param("itemId") Long itemId, @Param("userId") String userId);

    @Modifying
    @Query("UPDATE #{#entityName} r SET r.isRead = :isRead WHERE r.item.id = :itemId AND r.user = :userId")
    void readingManagement(@Param("itemId") Long itemId, @Param("userId") String userId,
        @Param("isRead") boolean isRead);

    @Modifying
    @Query("UPDATE #{#entityName} r SET r.readingCounter = r.readingCounter + 1 WHERE r.item.id = :itemId AND r.user = :userId")
    void incrementReadingCounter(@Param("itemId") Long itemId, @Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM #{#entityName} r WHERE r.user = :userId")
    void deleteAllByUserId(@Param("userId") String userId);

}
