package me.oreos.iam.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import me.oreos.iam.entities.enums.EffectiveScopeEnum;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

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
@Table(name = "role_permissions")
@TypeDef(name = "pgsql_enum", typeClass = me.oreos.iam.types.PostgreEffectiveScopeEnum.class)
@AttributeOverride(name = "isActive", column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false))
public class RolePermission extends MyBaseEntity<Integer> {
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, columnDefinition = "effective_scope_enum DEFAULT 'DEFAULT'")
    @Type(type = "pgsql_enum")
    private EffectiveScopeEnum scope = EffectiveScopeEnum.DEFAULT;

    @Column(name = "resource_id", nullable = true)
    private Integer resourceId;
}

// CREATE UNIQUE INDEX uq_role_permission_active
// ON role_permissions (role_id, permission_id)
// WHERE deleted_on IS NULL;
