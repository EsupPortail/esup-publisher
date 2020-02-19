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
package org.esupportail.publisher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.validation.constraints.Email;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A user.
 */
@Entity
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "login")
@Table(name = "T_USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractEntity<SubjectKey> implements Serializable, Subject {

	 private static final long serialVersionUID = 1L;

    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    //private Long id;

    @NotNull
    @Id
    @Size(min = 1, max = CstPropertiesLength.USERID)
    @Column(name = "user_id", length = CstPropertiesLength.USERID)
    private String login;

    @NotNull
    @Size(min = 0, max = CstPropertiesLength.USER_DISPLAYNAME)
    @Column(name = "display_name", length = CstPropertiesLength.USER_DISPLAYNAME)
    private String displayName;

    /** This field corresponds to the database column news_subject.enabled. */
    @Column(length = 1, nullable = false)
    private boolean enabled = true;

    @Column(length = 1, nullable = false, name = "accept_notifications")
    private boolean acceptNotifications;

    @Email
    @Size(min = 0, max = CstPropertiesLength.EMAIL)
    @Column(length = CstPropertiesLength.EMAIL)
    private String email;

    @Size(min = 2, max = CstPropertiesLength.LANG)
    @Column(name = "lang_key", length = CstPropertiesLength.LANG)
    private String langKey;

    @CreatedDate
    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    // @Size(min = 0, max = 20)
    // @Column(name = "activation_key", length = 20)
    // private String activationKey;

    // @JsonIgnore
    // @ManyToMany
    // @JoinTable(
    // name = "T_USER_AUTHORITY",
    // joinColumns = {@JoinColumn(name = "login", referencedColumnName =
    // "login")},
    // inverseJoinColumns = {@JoinColumn(name = "name", referencedColumnName =
    // "name")})
    // @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // private Set<Authority> authorities = new HashSet<>();

    // @JsonIgnore
    // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy =
    // "user")
    // @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // private Set<PersistentToken> persistentTokens = new HashSet<>();

    /**
     * @param userId
     * @param displayName
     */
    public User(@NotNull final String userId, @NotNull final String displayName) {
        super();
        this.login = userId;
        this.displayName = displayName;
    }

    @Override
    public SubjectKey getSubject() {
        return new SubjectKey(this.login, SubjectType.PERSON);
    }

    @JsonIgnore
    public SubjectKey getId() {
        return getSubject();
    }
}
