package org.esupportail.publisher.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "SHEDLOCK")
public class ShedLock {

    @Id
    @NotNull
    @Column(length = 64)
    private String name;

    @NotNull
    @Column(name = "lock_until")
    private Instant lockUntil = Instant.now();

    @NotNull
    @Column(name = "locked_at")
    private Instant lockedAt = Instant.now();

    @NotNull
    @Column(name = "locked_by", length = 255)
    private String lockedBy;
}
