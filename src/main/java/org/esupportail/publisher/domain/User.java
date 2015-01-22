package org.esupportail.publisher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.domain.util.CustomDateTimeDeserializer;
import org.esupportail.publisher.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "created_date", nullable = false)
    private DateTime createdDate = DateTime.now();

    @LastModifiedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "last_modified_date")
    private DateTime lastModifiedDate = DateTime.now();

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
