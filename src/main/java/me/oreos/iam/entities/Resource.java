package me.oreos.iam.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Entity
@Table(name = "resources", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "resource_id", "resource_type_id" })
})
@AttributeOverride(name = "isActive", column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false))
public class Resource extends MyBaseEntity<Integer> {
    @Column(name = "resource_id", nullable = false)
    private Integer resourceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "resource_type_id", nullable = false)
    private ResourceType resourceType;

}
