package me.oreos.iam.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import me.oreos.iam.entities.enums.EffectiveScopeEnum;


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
@Entity(name = "policy_permissions")
@AttributeOverride(
    name = "isActive",
    column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
)
@TypeDefs({
    @TypeDef(
        name = "pgsql_enum",
        typeClass = EnumType.class
    )
})
public class PolicyPermission extends MyBaseEntity<Integer> {
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @org.hibernate.annotations.Type(type = "pgsql_enum")
    @Column(name = "scope", nullable = false)
    private EffectiveScopeEnum scope = EffectiveScopeEnum.DEFAULT;

    @Column(name = "resource_id", nullable = true)
    private Integer resourceId;
}

// CREATE UNIQUE INDEX uq_policy_permission_active
// ON policy_permissions (policy_id, permission_id)
// WHERE deleted_on IS NULL;
