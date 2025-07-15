package me.oreos.iam.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Where(clause = "is_active = true AND deleted_on IS NULL")
@Entity
@Table(name = "roles")
@AttributeOverride(name = "isActive", column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false))
public class Role extends MyBaseEntity<Integer> {
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", nullable = true)
    private String description;

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "role")
    private List<RolePermission> rolePermissions = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles = new ArrayList<>();
}
