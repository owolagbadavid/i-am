package me.oreos.iam.entities;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;


import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
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
@Entity(name = "roles")
@AttributeOverride(
    name = "isActive",
    column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
)
public class Role extends MyBaseEntity<Integer> {
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "role")
    private List<RolePermission> rolePermissions;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles;
}
