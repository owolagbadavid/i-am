package me.oreos.iam.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
// import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Where(clause = "is_active = true AND deleted_on IS NULL")
@SuperBuilder
@Entity(name = "users")
@AttributeOverride(
    name = "isActive",
    column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
)
public class User extends MyBaseEntity<Integer> {
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<UserGroup> userGroups;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "user"
    // ,cascade = CascadeType.ALL, orphanRemoval = true
    // , fetch = javax.persistence.FetchType.LAZY
    )
    private List<UserPolicy> userPolicies = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Resource> resources = new ArrayList<>();

    public String getUsername() {
        return username;
    }
}